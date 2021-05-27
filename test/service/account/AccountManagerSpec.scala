package service.account

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import service.account.AccountManager._

import java.util.UUID
import scala.language.postfixOps

class AccountManagerSpec
    extends TestKit(ActorSystem("AccountManagerSpec"))
    with ImplicitSender
    with AnyWordSpecLike
    with BeforeAndAfterAll
    with Matchers {

  override def afterAll(): Unit = {

    TestKit.shutdownActorSystem(system)

  }

  "Account manager tests" should {
    "transaction denied with no player found " in {
      val uuid: UUID = UUID.randomUUID()
      val accountManager: ActorRef = system.actorOf(Props[AccountManager])
      accountManager ! ValidateBet(uuid, 10)
      expectMsg(TransactionDenied(PLAYER_NOT_FOUND))
    }

    "transaction validated" in {
      val uuid: UUID = UUID.randomUUID()
      val accountManager: ActorRef = system.actorOf(Props[AccountManager])
      accountManager ! PlayerTransactionRequest(uuid, None)
      Thread.sleep(500)
      accountManager ! ValidateBet(uuid, 10)
      expectMsg(TransactionSupported)
    }

    "transaction denied with insufficient funds" in {
      val uuid: UUID = UUID.randomUUID()
      val accountManager: ActorRef = system.actorOf(Props[AccountManager])
      accountManager ! PlayerTransactionRequest(uuid, None)
      Thread.sleep(500)
      accountManager ! ValidateBet(uuid, 1001)
      expectMsg(TransactionDenied(INSUFFICIENT_FUNDS))
    }

    "test the delegation of transactions" in {
      val uuid: UUID = UUID.randomUUID()
      val accountManager: ActorRef = system.actorOf(Props[AccountManager])
      accountManager ! PlayerTransactionRequest(uuid, None)
      Thread.sleep(500)
      accountManager ! PlayerTransactionRequest(uuid, Some(-10))
      Thread.sleep(500)
      accountManager ! PlayerTransactionRequest(uuid, Some(3))
      Thread.sleep(500)
      accountManager ! PlayerTransactionRequest(uuid, Some(-1))
      Thread.sleep(500)
      accountManager ! ValidateBet(uuid, 992)
      expectMsg(TransactionSupported)
      accountManager ! ValidateBet(uuid, 993)
      expectMsg(TransactionDenied(INSUFFICIENT_FUNDS))
    }

  }

}
