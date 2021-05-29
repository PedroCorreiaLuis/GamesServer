package service.account

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.TestKit
import base.ActorSpec
import service.account.AccountDB.{Get, Update}

import java.util.UUID

class AccountDBSpec extends TestKit(ActorSystem("AccountDBSpec")) with ActorSpec {
  "Account DB" should {

    "get a player that doesn't have a balance" in {
      val uuid: UUID = UUID.randomUUID()
      val accountDBActor: ActorRef = system.actorOf(Props[AccountDB])
      accountDBActor ! Get(uuid)
      expectMsg(None)
    }

    "get a player that does have a balance" in {
      val uuid: UUID = UUID.randomUUID()
      val accountDBActor: ActorRef = system.actorOf(Props[AccountDB])
      accountDBActor ! Update(uuid, 1000)
      accountDBActor ! Get(uuid)
      expectMsg(Some(1000))
      accountDBActor ! Update(uuid, 1050)
      accountDBActor ! Get(uuid)
      expectMsg(Some(1050))
    }

  }
}
