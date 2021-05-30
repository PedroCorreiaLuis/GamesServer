package service.account

import akka.actor.{Actor, ActorLogging}

import java.util.UUID

//TODO abstract this
class AccountDB extends Actor with ActorLogging {
  override def receive: Receive = online(Map.empty[UUID, Int])
  import service.account.AccountDB._

  def online(database: Map[UUID, Int]): Receive = {
    case Get(key) =>
      log.info(s"Checking balance of: $key")
      val balance: Option[Int] = database.get(key)
      log.info(s"Balance of : $key is ${balance.getOrElse(0)}")
      sender() ! balance
    case Update(key, value) =>
      log.info(s"Updating the balance of: $key")
      log.info(s"$key balance is now $value")
      context.become(online(database + (key -> value)))
  }
}

object AccountDB {
  case class Get(key: UUID)
  case class Update(key: UUID, value: Int)
}
