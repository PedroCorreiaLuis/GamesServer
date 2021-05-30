package service.games

import akka.actor.{Actor, ActorLogging}

import java.util.UUID

class GamesDB extends Actor with ActorLogging {
  override def receive: Receive = online(Map.empty[Int, Map[UUID, List[UUID]]])
  import service.games.GamesDB._

  def online(database: Map[Int, Map[UUID, List[UUID]]]): Receive = {
    case Get(gameId, sessionId) =>
      log.info(s"Checking game: $gameId")

      val sessionsAndPlayers: Option[Map[UUID, List[UUID]]] = database.get(gameId)

      log.info(s"Checking game session: $sessionId")

      val players: Option[List[UUID]] = sessionsAndPlayers.flatMap(_.get(sessionId))

      log.info(
        s"Game: $gameId with session $sessionId as the following players ${players.getOrElse("invalid game or session")}"
      )

      sender() ! players

    case Update(gameId, sessionId, players) =>
      log.info(s"Updating game: $gameId with the session: $sessionId")

      val sessionsAndPlayers: Option[Map[UUID, List[UUID]]] = database.get(gameId)
      val currentSessionAndPlayers: Map[UUID, List[UUID]] =
        sessionsAndPlayers.map(map => (map + (sessionId -> players))).getOrElse(Map.empty[UUID, List[UUID]])

      log.info(s"Game: $gameId with session $sessionId players are now $players")

      context.become(online(database + (gameId -> currentSessionAndPlayers)))

    case SimpleGet(gameId) => sender() ! database.get(gameId)
  }
}

object GamesDB {
  case class SimpleGet(gameId: Int)
  case class Get(gameId: Int, sessionId: UUID)
  case class Update(gameId: Int, sessionId: UUID, players: List[UUID])
}
