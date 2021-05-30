package service

import akka.actor.{Actor, ActorRef, Props, Stash}
import akka.pattern.ask
import akka.util.Timeout
import service.account.AccountManager
import service.games.GameEngine.Start
import service.games.GameManager.GameJoinReply
import service.games.cardgames.implementations.HigherCardGame.GameResult
import service.games.{GameEngine, GameManager}
import service.player.PlayerService.PlayerAction

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

//On start up spawn all necessary actors
class AppManager extends Actor with Stash {
  //TODO add stashing
  implicit val timeout: Timeout = Timeout(1 second)
  implicit val executionContext: ExecutionContext = context.dispatcher

  val accountManager: ActorRef = context.actorOf(Props[AccountManager])
  val gameManager: ActorRef = context.actorOf(Props[GameManager])

  override def receive: Receive = {
    case GameJoinReply(gameId, playersIds, sessionId) =>
      val gameEngine: ActorRef = {
        context.actorOf(Props(new GameEngine(players = playersIds, gameId = gameId, accountManager = accountManager)))
      }

      val gameEngineResponse: Future[Any] = gameEngine ? Start
      val typedGameEngineResponse: Future[GameResult] = gameEngineResponse.mapTo[GameResult]

      //TODO send cards to users
      ???
      context.become(waitingForPlayers(gameEngine))

  }

  def waitingForPlayers(gameEngine: ActorRef): Receive = {
    case playerAction: PlayerAction => gameEngine ! playerAction
    //case unblock
    case a => context.unbecome()
  }

}

object AppManager {}
