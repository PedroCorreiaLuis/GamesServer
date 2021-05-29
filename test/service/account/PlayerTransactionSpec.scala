package service.account

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.TestKit
import base.ActorSpec
import service.account.AccountManager.{PlayerFundsReply, PlayerFundsRequest}
import service.account.PlayerTransaction.PlayerWithFunds

import java.util.UUID

class PlayerTransactionSpec extends TestKit(ActorSystem("PlayerTransactionSpec")) with ActorSpec {

  "Player transaction" should {
    "correctly add an amount to the current balance " in {
      val uuid: UUID = UUID.randomUUID()
      val playerTransactionActor: ActorRef = system.actorOf(Props[PlayerTransaction])
      playerTransactionActor ! PlayerFundsRequest(playerId = uuid, balance = Some(1000), amount = Some(100))
      expectMsg(PlayerFundsReply(PlayerWithFunds(playerIdentifier = uuid, balance = 1100)))
    }

    "correctly remove an amount to the current balance " in {
      val uuid: UUID = UUID.randomUUID()
      val playerTransactionActor: ActorRef = system.actorOf(Props[PlayerTransaction])
      playerTransactionActor ! PlayerFundsRequest(playerId = uuid, balance = Some(1000), amount = Some(-100))
      expectMsg(PlayerFundsReply(PlayerWithFunds(playerIdentifier = uuid, balance = 900)))
    }

    "Initialize with a default of 1000" in {
      val uuid: UUID = UUID.randomUUID()
      val playerTransactionActor: ActorRef = system.actorOf(Props[PlayerTransaction])
      playerTransactionActor ! PlayerFundsRequest(playerId = uuid, balance = None, amount = None)
      expectMsg(PlayerFundsReply(PlayerWithFunds(playerIdentifier = uuid, balance = 1000)))
    }
  }

}
