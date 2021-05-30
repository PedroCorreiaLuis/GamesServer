package service.games

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import java.util.UUID
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class GameManager extends Actor {
  import GameManager._
  import service.games.GamesDB.{SimpleGet, Update}
  implicit val timeout: Timeout = Timeout(1 second)
  implicit val executionContext: ExecutionContext = context.dispatcher

  private val gameDBActor: ActorRef = context.actorOf(Props[GamesDB])
  private val gameWorkerActor: ActorRef = context.actorOf(Props[GameService])

  override def receive: Receive = {
    case request: GameCreationRequest =>
      val workerResponse: Future[Any] = gameWorkerActor ? request

      val typedResponse: Future[GameCreationReply] = workerResponse.mapTo[GameCreationReply]

      val futureResponse: Future[Update] = typedResponse.map(
        gameCreationReply =>
          Update(
            gameId = gameCreationReply.gameID,
            sessionId = gameCreationReply.sessionId,
            players = List(gameCreationReply.playerID)
          )
      )

      futureResponse.pipeTo(gameDBActor)

    case request: GameJoinRequest =>
      val originalSender: ActorRef = sender()

      val dbResponse: Future[Any] = gameDBActor ? SimpleGet(gameId = request.gameID)
      val typedDbResponse: Future[Option[Map[UUID, List[UUID]]]] = dbResponse.mapTo[Option[Map[UUID, List[UUID]]]]

      val updatedGameJoinRequest: Future[GameJoinRequest] = typedDbResponse.map(
        sessionsAndPlayers =>
          GameJoinRequest(gameID = request.gameID, playerID = request.playerID, sessionsAndPlayers = sessionsAndPlayers)
      )

      val workerResponse: Future[Any] = updatedGameJoinRequest.flatMap(gameWorkerActor ? _)

      val typedResponse: Future[Either[GameJoinReply, GameCreationRequest]] =
        workerResponse.mapTo[Either[GameJoinReply, GameCreationRequest]]

      typedResponse.map {
        case Left(gameJoinReply)        => originalSender ! gameJoinReply
        case Right(gameCreationRequest) => self ! gameCreationRequest
      }

  }

}

object GameManager {
  case class GameCreationRequest(gameID: Int, playerID: UUID)
  case class GameCreationReply(gameID: Int, playerID: UUID, sessionId: UUID)
  case class GameJoinRequest(gameID: Int, playerID: UUID, sessionsAndPlayers: Option[Map[UUID, List[UUID]]])
  case class GameJoinReply(gameId: Int, playersIds: List[UUID], sessionId: UUID)
}
