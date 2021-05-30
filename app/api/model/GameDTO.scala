package api.model

import play.api.libs.json.{Json, OFormat}

import java.util.UUID

case class GameDTO(gameId: Int, sessionId: UUID, playerId: UUID, gameAction: String)

object GameDTO {
  implicit val gameDTOReader: OFormat[GameDTO] = Json.format[GameDTO]
}
