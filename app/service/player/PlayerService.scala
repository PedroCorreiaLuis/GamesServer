package service.player

import akka.actor.{Actor, ActorRef}
import api.model.PlayerDTO
import service.account.AccountManager.PlayerTransactionRequest
import service.player.PlayerService.{createPlayer, spawnAccount, PlayerCreation}

import java.util.UUID

class PlayerService(accountManagerRef: ActorRef) extends Actor {

  override def receive: Receive = {
    case playerDTO: PlayerDTO =>
      val newPlayer: PlayerCreation = createPlayer(playerDTO)
      spawnAccount(player = newPlayer, accountManagerRef = accountManagerRef)
  }
}

object PlayerService {
  case class PlayerCreation(firstName: String, lastName: String, username: String, age: Int, playerID: UUID)
  case class PlayerAction(action: String, playerId: UUID, gameId: Int, sessionId: UUID)
  val FOLD: String = "fold"
  val PLAY: String = "play"

  def createPlayer(playerDTO: PlayerDTO): PlayerCreation = {
    PlayerCreation(
      firstName = playerDTO.firstName,
      lastName = playerDTO.lastName,
      username = playerDTO.username,
      age = playerDTO.age,
      playerID = UUID.randomUUID()
    )
  }

  def spawnAccount(player: PlayerCreation, accountManagerRef: ActorRef): Unit = {
    accountManagerRef ! PlayerTransactionRequest(player.playerID, None)
  }

}
