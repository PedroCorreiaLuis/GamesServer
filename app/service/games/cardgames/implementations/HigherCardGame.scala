package service.games.cardgames.implementations

import service.games.cardgames.implementations.HigherCardGame.{calculateGameResult, dealHands}
import service.games.cardgames.utils.CardOrdering
import service.games.cardgames.utils.CardRandomizer._
import service.games.cardgames.{Card, Hand}

import java.util.UUID

case class HigherCardGame(player1: UUID, player2: UUID, numberOfCards: Int) {

  val players: List[UUID] = List(player1, player2)

  def play(): HigherCardGame.GameResult = {
    val promotedCards: Set[Card] = promoteCards(players.length * numberOfCards)
    val hands: Iterator[Hand] = createHands(promotedCards, players)
    calculateGameResult(dealHands(players, hands))
  }

}

object HigherCardGame {
  case class PlayerWithHand(playerId: UUID, hand: Hand)
  sealed trait GameResult
  case class Tie(playersWithHands: List[PlayerWithHand]) extends GameResult
  case class ResultWithWinner(winner: PlayerWithHand, loser: PlayerWithHand) extends GameResult

  def dealHands(players: List[UUID], hands: Iterator[Hand]): List[PlayerWithHand] = {
    players.zip(hands).map { case (player, hand) => PlayerWithHand(playerId = player, hand = hand) }
  }

  def calculateGameResult(playersWithHands: List[PlayerWithHand]): GameResult = {

    val multipleHands: List[Seq[Card]] = playersWithHands.map(_.hand.cards).sorted(CardOrdering.compareSeq).reverse

    multipleHands match {
      case List(player1Cards, player2Cards) if player1Cards == player2Cards => Tie(playersWithHands)
      case List(firstHand, _) =>
        val (winningPlayers: List[PlayerWithHand], losingPlayers: List[PlayerWithHand]) =
          playersWithHands.partition(_.hand == firstHand)

        val winningPlayerWithHand: PlayerWithHand = winningPlayers.head
        val losingPlayerWithHand: PlayerWithHand = losingPlayers.head
        ResultWithWinner(winningPlayerWithHand, losingPlayerWithHand)
    }

  }
}
