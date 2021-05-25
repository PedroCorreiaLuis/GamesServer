package service.account

import akka.actor.{Actor, ActorRef, Props}
import service.player.PlayerService.Player

import java.util.UUID

class AccountManager(appManager: ActorRef) extends Actor {
  import AccountManager._
  override def receive: Receive =
    delegateFundsMovement(appManager = appManager, Map.empty[UUID, Int])

  def delegateFundsMovement(appManager: ActorRef,
                            accounts: Map[UUID, Int]): Receive = {
    case (player: Player, betAmount: Option[Int]) =>
      val playerFundsActor: ActorRef = context.actorOf(Props[PlayerFunds])
      val playerBalance: Option[Int] = accounts.get(player.playerID)

      //TODO add ask pattern
      playerFundsActor ! PlayerFundsRequest(
        player = player,
        balance = playerBalance,
        betAmount = betAmount
      )

      if (playerBalance.isDefined)
        context.become(
          delegateFundsMovement(
            appManager = appManager,
            accounts = accounts + (player.playerID -> playerBalance.get)
          )
        )

    case playerWithFundsReply: PlayerFundsReply =>
      context.become(
        delegateFundsMovement(
          appManager = appManager,
          accounts = accounts + (playerWithFundsReply.playerWithFunds.playerIdentifier -> playerWithFundsReply.playerWithFunds.balance)
        )
      )

    case ShowBalance(playerId) =>
      val playerBalance: Option[String] = accounts.get(playerId).map(_.toString)
      println(playerBalance.getOrElse("Player doesn't exist"))

  }
}

object AccountManager {
  import PlayerFunds._
  case class ShowBalance(playerId: UUID)
  case class PlayerFundsRequest(player: Player,
                                balance: Option[Int],
                                betAmount: Option[Int])
  case class PlayerFundsReply(playerWithFunds: PlayerWithFunds)
}
