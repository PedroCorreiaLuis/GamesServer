package service.player

import akka.actor.{Actor, ActorRef}
import api.model.PlayerDTO
import service.account.AccountManager.PlayerTransactionRequest
import service.player.PlayerService.{createPlayer, spawnAccount, Player}

import java.util.UUID

class PlayerService(accountManagerRef: ActorRef) extends Actor {

  override def receive: Receive = {
    case playerDTO: PlayerDTO =>
      val newPlayer: Player = createPlayer(playerDTO)
      spawnAccount(player = newPlayer, accountManagerRef = accountManagerRef)
  }
}

object PlayerService {
  case class Player(firstName: String, lastName: String, username: String, age: Int, playerID: UUID)

  def createPlayer(playerDTO: PlayerDTO): Player = {
    Player(
      firstName = playerDTO.firstName,
      lastName = playerDTO.lastName,
      username = playerDTO.username,
      age = playerDTO.age,
      playerID = UUID.randomUUID()
    )
  }

  def spawnAccount(player: Player, accountManagerRef: ActorRef): Unit = {
    accountManagerRef ! PlayerTransactionRequest(player.playerID, None)
  }

}
