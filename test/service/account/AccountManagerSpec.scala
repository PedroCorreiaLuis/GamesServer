package service.account

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.TestKit
import base.ActorSpec
import service.account.AccountManager._

import java.util.UUID
import scala.language.postfixOps

class AccountManagerSpec extends TestKit(ActorSystem("AccountManagerSpec")) with ActorSpec {
//TODO remove Thread.sleep when bug is fixed
  "Account manager" should {
    "transaction denied with no player found " in {
      val uuid: UUID = UUID.randomUUID()
      val accountManagerActor: ActorRef = system.actorOf(Props[AccountManager])
      accountManagerActor ! ValidateBet(uuid, 10)
      expectMsg(TransactionDenied(PLAYER_NOT_FOUND))
    }

    "transaction validated" in {
      val uuid: UUID = UUID.randomUUID()
      val accountManagerActor: ActorRef = system.actorOf(Props[AccountManager])
      accountManagerActor ! PlayerTransactionRequest(uuid, None)
      Thread.sleep(500)
      accountManagerActor ! ValidateBet(uuid, 10)
      expectMsg(TransactionSupported)
    }

    "transaction denied with insufficient funds" in {
      val uuid: UUID = UUID.randomUUID()
      val accountManagerActor: ActorRef = system.actorOf(Props[AccountManager])
      accountManagerActor ! PlayerTransactionRequest(uuid, None)
      Thread.sleep(500)
      accountManagerActor ! ValidateBet(uuid, 1001)
      expectMsg(TransactionDenied(INSUFFICIENT_FUNDS))
    }

    "test the delegation of transactions" in {
      val uuid: UUID = UUID.randomUUID()
      val accountManagerActor: ActorRef = system.actorOf(Props[AccountManager])
      accountManagerActor ! PlayerTransactionRequest(uuid, None)
      Thread.sleep(500)
      accountManagerActor ! PlayerTransactionRequest(uuid, Some(-10))
      Thread.sleep(500)
      accountManagerActor ! PlayerTransactionRequest(uuid, Some(3))
      Thread.sleep(500)
      accountManagerActor ! PlayerTransactionRequest(uuid, Some(-1))
      Thread.sleep(500)
      accountManagerActor ! ValidateBet(uuid, 992)
      expectMsg(TransactionSupported)
      accountManagerActor ! ValidateBet(uuid, 993)
      expectMsg(TransactionDenied(INSUFFICIENT_FUNDS))
    }

  }

}
