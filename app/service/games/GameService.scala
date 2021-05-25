package service.games

import akka.actor.Actor
import service.games.GameManager.GameCreationRequest

class GameService extends Actor {
  override def receive: Receive = ???
}

object GameService {

  def generateGameSession(gameCreationRequest: GameCreationRequest) = {
    gameCreationRequest
  }

}
