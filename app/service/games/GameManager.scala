package service.games

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt

class GameManager extends Actor {
  import GameManager._
  implicit val timeout: Timeout = Timeout(1 second)
  implicit val executionContext: ExecutionContext = context.dispatcher
  override def receive: Receive = {
    case request: GameCreationRequest =>
      val originalSender: ActorRef = sender()
      val gameWorker: ActorRef = context.actorOf(Props[GameService])
      val workerResponse: Future[Any] = gameWorker ? request
      val typedResponse = workerResponse.mapTo[Option[String]]
      originalSender ! ???
    case request: GameJoinRequest =>
  }

}

object GameManager {
  case class GameCreationRequest(gameID: Int, playerID: UUID)
  case class GameCreationReply(gameID: Int, playerID: UUID, sessionID: Int)
  case class GameJoinRequest(gameID: Int, playerID: UUID)
  case class GameJoinReply()
}
