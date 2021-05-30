package api.model
import play.api.libs.json.{Json, OFormat}

case class PlayerDTO(firstName: String, lastName: String, username: String, age: Int)

object PlayerDTO {
  implicit val playerDTOReader: OFormat[PlayerDTO] = Json.format[PlayerDTO]
}
