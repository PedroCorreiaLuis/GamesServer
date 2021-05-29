package service.account

import akka.actor.Actor

import java.util.UUID

class PlayerTransaction extends Actor {
  import service.account.AccountManager.PlayerFundsRequest
  import PlayerTransaction._

  override def receive: Receive = {
    case playerFundsRequest: PlayerFundsRequest =>
      sender() ! toPlayerWithFundsReply(playerFundsRequest)
  }
}

object PlayerTransaction {
  import service.account.AccountManager._
  case class PlayerWithFunds(playerIdentifier: UUID, balance: Int)

  def toPlayerWithFundsReply(
    playerRequest: PlayerFundsRequest
  ): PlayerFundsReply = {
    PlayerFundsReply(
      PlayerWithFunds(
        playerIdentifier = playerRequest.playerId,
        balance = (for {
          balance <- playerRequest.balance
          betAmount <- playerRequest.amount
        } yield {
          balance + betAmount
        }).getOrElse(1000)
      )
    )
  }
}
