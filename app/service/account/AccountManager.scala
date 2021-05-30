package service.account

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import service.account.PlayerTransaction.PlayerWithFunds

import java.util.UUID
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class AccountManager extends Actor {
  import AccountManager._
  import service.account.AccountDB._

  implicit val timeout: Timeout = Timeout(1 second)
  implicit val executionContext: ExecutionContext = context.dispatcher

  private val accountDBActor: ActorRef = context.actorOf(Props[AccountDB])
  private val playerFundsActor: ActorRef = context.actorOf(Props[PlayerTransaction])

  override def receive: Receive = delegateFundsMovement

  def delegateFundsMovement: Receive = {
    //TODO cant handle multiple request at the same time with the same id, strange behaviour in AccountDB
    case PlayerTransactionRequest(playerId, amount) =>
      //TODO add more actors to improve responsiveness

      val dbResponse: Future[Any] = accountDBActor ? Get(playerId)

      val dbTypedResponse: Future[Option[Int]] = dbResponse.mapTo[Option[Int]]

      val transactionResponse: Future[Any] = dbTypedResponse.flatMap { playerBalance =>
        playerFundsActor ? PlayerFundsRequest(
          playerId = playerId,
          balance = playerBalance,
          amount = amount
        )
      }

      val transactionTypedResponse: Future[PlayerFundsReply] = transactionResponse.mapTo[PlayerFundsReply]

      val futureResponse: Future[Update] = transactionTypedResponse.map { playerWithFundsReply: PlayerFundsReply =>
        val playerWithFunds: PlayerWithFunds = playerWithFundsReply.playerWithFunds
        Update(
          key = playerWithFunds.playerIdentifier,
          value = playerWithFunds.balance
        )
      }

      futureResponse.pipeTo(accountDBActor)

    case ValidateBet(playerID, betAmount) =>
      val originalSender: ActorRef = sender()
      val dbResponse: Future[Any] = accountDBActor ? Get(playerID)
      val dbTypedResponse: Future[Option[Int]] = dbResponse.mapTo[Option[Int]]
      val typedReply: Future[TransactionReply] = dbTypedResponse.map {
        case None => TransactionDenied(PLAYER_NOT_FOUND)
        case Some(balance) =>
          if (balance >= betAmount) TransactionSupported else TransactionDenied(INSUFFICIENT_FUNDS)
      }

      typedReply.pipeTo(originalSender)
  }
}

object AccountManager {
  sealed trait TransactionReply
  case object TransactionSupported extends TransactionReply
  case class TransactionDenied(message: String) extends TransactionReply

  case class PlayerTransactionRequest(playerId: UUID, amount: Option[Int])
  case class ValidateBet(playerId: UUID, betAmount: Int)
  case class PlayerFundsRequest(playerId: UUID, balance: Option[Int], amount: Option[Int])
  case class PlayerFundsReply(playerWithFunds: PlayerWithFunds)

  val PLAYER_NOT_FOUND: String = "Player doesn't exist"
  val INSUFFICIENT_FUNDS: String = "Insufficient funds"
}
