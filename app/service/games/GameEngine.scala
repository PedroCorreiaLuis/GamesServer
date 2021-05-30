package service.games

import akka.actor.{Actor, ActorRef}
import service.account.AccountManager.PlayerTransactionRequest
import service.games.cardgames.implementations.{CardGameDefinition, HigherCardGame}
import akka.actor.PoisonPill

import java.util.UUID

class GameEngine(players: List[UUID], gameId: Int, accountManager: ActorRef) extends Actor {
  import service.player.PlayerService._
  import service.games.GameEngine._
  import service.games.cardgames.implementations.CardGameDefinition._

  override def receive: Receive = {

    case Start =>
      val player1: UUID = players.head
      val player2: UUID = players.last

      val gameDefinition: CardGameDefinition = listOfAllGames.find(_.id == gameId).get

      val game: HigherCardGame = gameDefinition match {
        case HIGHER_SINGLE_CARD_DEFINITION => HigherCardGame(player1 = player1, player2 = player2, numberOfCards = 1)
        case HIGHER_DOUBLE_CARD_DEFINITION => HigherCardGame(player1 = player1, player2 = player2, numberOfCards = 2)
      }

      val gameResults: HigherCardGame.GameResult = game.play()

      {
        context.become(waiting(playerActions = Nil, gameResult = gameResults, gameDefinition = gameDefinition))
        gameResults
      }

  }

  def waiting(
    playerActions: List[PlayerAction],
    gameResult: HigherCardGame.GameResult,
    gameDefinition: CardGameDefinition
  ): Receive = {
    case playerAction: PlayerAction =>
      val currentPlayerActions: List[PlayerAction] = playerActions :+ playerAction

      if (currentPlayerActions.length == 2) {

        val playerAction1: PlayerAction = currentPlayerActions.head
        val playerAction2: PlayerAction = currentPlayerActions.last

        (playerAction1, playerAction2) match {
          case (PlayerAction(FOLD, player1, _, _), PlayerAction(FOLD, player2, _, _)) =>
            accountManager ! PlayerTransactionRequest(
              playerId = player1,
              amount = Some(-gameDefinition.doubleFoldValue)
            )

            accountManager ! PlayerTransactionRequest(
              playerId = player2,
              amount = Some(-gameDefinition.doubleFoldValue)
            )

          case (PlayerAction(FOLD, player1, _, _), PlayerAction(PLAY, player2, _, _)) =>
            accountManager ! PlayerTransactionRequest(
              playerId = player1,
              amount = Some(-gameDefinition.singleFoldValue)
            )

            accountManager ! PlayerTransactionRequest(
              playerId = player2,
              amount = Some(gameDefinition.singleFoldValue)
            )

          case (PlayerAction(PLAY, player1, _, _), PlayerAction(FOLD, player2, _, _)) =>
            accountManager ! PlayerTransactionRequest(
              playerId = player1,
              amount = Some(gameDefinition.singleFoldValue)
            )

            accountManager ! PlayerTransactionRequest(
              playerId = player2,
              amount = Some(-gameDefinition.singleFoldValue)
            )

          case _ =>
            gameResult match {
              case HigherCardGame.ResultWithWinner(winner, loser) =>
                accountManager ! PlayerTransactionRequest(
                  playerId = winner.playerId,
                  amount = Some(gameDefinition.winValue)
                )

                accountManager ! PlayerTransactionRequest(
                  playerId = loser.playerId,
                  amount = Some(-gameDefinition.winValue)
                )

              case HigherCardGame.Tie(_) => ()
            }
        }

        sender() ! Remove(gameId = playerAction1.gameId, sessionId = playerAction1.sessionId)

        self ! PoisonPill

      } else {
        context.become(waiting(currentPlayerActions, gameResult, gameDefinition))
      }

  }

}

object GameEngine {
  object Start
  case class Remove(gameId: Int, sessionId: UUID)
}
