package service.account

import akka.actor.Actor

import java.util.UUID

class PlayerFunds extends Actor {
  import service.account.AccountManager.PlayerFundsRequest
  import PlayerFunds._
  override def receive: Receive = {
    case playerFundsRequest: PlayerFundsRequest =>
      sender() ! toPlayerWithFundsReply(playerFundsRequest)
  }
}

object PlayerFunds {
  import service.account.AccountManager._
  case class PlayerWithFunds(playerIdentifier: UUID, balance: Int)

  def toPlayerWithFundsReply(
    playerRequest: PlayerFundsRequest
  ): PlayerFundsReply = {
    PlayerFundsReply(
      PlayerWithFunds(
        playerIdentifier = playerRequest.player.playerID,
        balance = (for {
          balance <- playerRequest.balance
          betAmount <- playerRequest.betAmount
        } yield {
          balance + betAmount
        }).getOrElse(1000)
      )
    )
  }
}
