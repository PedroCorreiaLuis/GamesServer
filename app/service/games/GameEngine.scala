package service.games

import akka.actor.{Actor, ActorRef}
import service.account.AccountManager.PlayerTransactionRequest
import service.games.cardgames.implementations.{CardGameDefinition, HigherCardGame}

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
      sender() ! gameResults
      context.become(waiting(playerActions = Nil, gameResult = gameResults, gameDefinition = gameDefinition))

  }

  def waiting(
    playerActions: List[PlayerAction],
    gameResult: HigherCardGame.GameResult,
    gameDefinition: CardGameDefinition
  ): Receive = {
    case playerAction: PlayerAction =>
      //todo validate action
      val currentPlayerActions: List[PlayerAction] = playerActions :+ playerAction

      if (currentPlayerActions.length == 2) {

        val playerAction1: PlayerAction = currentPlayerActions.head
        val playerAction2: PlayerAction = currentPlayerActions.last

        (playerAction1, playerAction2) match {
          case (PlayerAction(FOLD, player1), PlayerAction(FOLD, player2)) =>
            accountManager ! PlayerTransactionRequest(
              playerId = player1,
              amount = Some(-gameDefinition.doubleFoldValue)
            )

            accountManager ! PlayerTransactionRequest(
              playerId = player2,
              amount = Some(-gameDefinition.doubleFoldValue)
            )

          case (PlayerAction(FOLD, player1), PlayerAction(PLAY, player2)) =>
            accountManager ! PlayerTransactionRequest(
              playerId = player1,
              amount = Some(-gameDefinition.singleFoldValue)
            )

            accountManager ! PlayerTransactionRequest(
              playerId = player2,
              amount = Some(gameDefinition.singleFoldValue)
            )

          case (PlayerAction(PLAY, player1), PlayerAction(FOLD, player2)) =>
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

        //TODO add why to "unblock" APPManager
        context.unbecome()

      } else {
        context.become(waiting(currentPlayerActions, gameResult, gameDefinition))
      }

  }

}

object GameEngine {
  object Start
}
