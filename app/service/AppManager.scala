package service

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import api.model.PlayerDTO
import service.AppManager.{Game, ShowCards}
import service.account.AccountManager
import service.games.GameEngine.{Remove, Start}
import service.games.GameManager.{GameCreationRequest, GameJoinReply, GameJoinRequest}
import service.games.cardgames.Hand
import service.games.cardgames.implementations.HigherCardGame
import service.games.cardgames.implementations.HigherCardGame.GameResult
import service.games.{GameEngine, GameManager}
import service.player.PlayerService
import service.player.PlayerService.PlayerAction

import java.util.UUID
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class AppManager extends Actor {
  implicit val timeout: Timeout = Timeout(1 second)
  implicit val executionContext: ExecutionContext = context.dispatcher

  val accountManager: ActorRef = context.actorOf(Props[AccountManager])
  val gameManager: ActorRef = context.actorOf(Props[GameManager])

  override def receive: Receive = waiting(Nil)

  def waiting(games: List[Game] = Nil): Receive = {
    case GameJoinReply(gameId, playersIds, sessionId) =>
      val gameEngine: ActorRef = {
        context.actorOf(Props(new GameEngine(players = playersIds, gameId = gameId, accountManager = accountManager)))
      }

      val gameEngineResponse: Future[Any] = gameEngine ? Start
      val typedGameEngineResponse: Future[GameResult] = gameEngineResponse.mapTo[GameResult]

      val responseCards: Future[List[ShowCards]] = typedGameEngineResponse.map {
        case HigherCardGame.Tie(playersWithHands) =>
          playersWithHands.map(
            playersWithHands => ShowCards(playerId = playersWithHands.playerId, hand = playersWithHands.hand)
          )
        case HigherCardGame.ResultWithWinner(winner, loser) =>
          List(
            ShowCards(playerId = winner.playerId, hand = winner.hand),
            ShowCards(playerId = loser.playerId, hand = loser.hand)
          )
      }

      //TODO missing implementing -> show cards to players
      responseCards.onComplete(
        _.foreach(
          _.foreach(
            showCards =>
              println(
                s"Player ${showCards.playerId} has target cards: ${showCards.hand.cards.map(_.name).mkString(" and ")}"
              )
          )
        )
      )

      val currentGames: List[Game] = games :+ Game(gameId = gameId, sessionId = sessionId, gameEngine = gameEngine)

      {
        context.become(waiting(currentGames))
        responseCards
      }

    case playerAction: PlayerAction =>
      val targetGame: Option[Game] =
        games.find(game => game.gameId == playerAction.gameId && game.sessionId == playerAction.sessionId)

      targetGame.getOrElse(throw new Exception("This shouldn't happen")).gameEngine ! playerAction

    case Remove(gameId, sessionId) =>
      val currentGames: List[Game] = games.filterNot(game => game.gameId == gameId && game.sessionId == sessionId)
      context.become(waiting(currentGames))

    case playerDTO: PlayerDTO =>
      val playerServiceActor: ActorRef = context.actorOf(Props(new PlayerService(accountManager)))

      playerServiceActor ! playerDTO

    case gameCreationRequest: GameCreationRequest => gameManager ! gameCreationRequest
    case gameJoinRequest: GameJoinRequest         => gameManager ! gameJoinRequest
//    case validateBet: ValidateBet                 =>
//    case TransactionSupported(playerId)           =>
//    case TransactionDenied(playerId, message)     =>
  }

}

object AppManager {
  case class Game(gameId: Int, sessionId: UUID, gameEngine: ActorRef)
  case class ShowCards(playerId: UUID, hand: Hand)
}
