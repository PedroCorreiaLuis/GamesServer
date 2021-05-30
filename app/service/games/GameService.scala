package service.games

import akka.actor.Actor
import service.games.GameManager._

import java.util.UUID

class GameService extends Actor {

  import service.games.GameService._

  override def receive: Receive = {
    case gameCreationRequest: GameCreationRequest => sender() ! generateGameSession(gameCreationRequest)
    case gameJoinRequest: GameJoinRequest         => sender() ! pairPlayers(gameJoinRequest)
  }
}

object GameService {

  def generateGameSession(gameCreationRequest: GameCreationRequest): GameCreationReply = {
    val sessionId: UUID = UUID.randomUUID()

    GameCreationReply(
      gameID = gameCreationRequest.gameID,
      playerID = gameCreationRequest.playerID,
      sessionId = sessionId
    )
  }

  def pairPlayers(gameJoinRequest: GameJoinRequest): Either[GameJoinReply, GameCreationRequest] = {

    val sessionsAndPlayers: Map[UUID, List[UUID]] =
      gameJoinRequest.sessionsAndPlayers.getOrElse(Map.empty[UUID, List[UUID]])

    val firstValidSession: Option[(UUID, List[UUID])] = sessionsAndPlayers.find {
      case (_, players) => players.length < 2
    }

    val sessionId: Option[UUID] = firstValidSession.map(_._1)

    val players: List[UUID] = firstValidSession
      .map {
        case (_, players) => players :+ gameJoinRequest.playerID
      }
      .getOrElse(Nil)

    sessionId match {
      case Some(id) => Left(GameJoinReply(gameId = gameJoinRequest.gameID, playersIds = players, sessionId = id))
      case None =>
        Right(
          GameCreationRequest(gameID = gameJoinRequest.gameID, playerID = gameJoinRequest.playerID)
        )
    }

  }

}
