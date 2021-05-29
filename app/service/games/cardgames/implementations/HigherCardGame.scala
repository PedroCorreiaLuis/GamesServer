package service.games.cardgames.implementations

import service.games.cardgames.implementations.HigherCardGame.{dealHands, distributePrizes}
import service.games.cardgames.utils.CardOrdering
import service.games.cardgames.utils.CardRandomizer._
import service.games.cardgames.{Card, Hand}

import java.util.UUID

class HigherCardGame(player1: UUID, player2: UUID, numberOfCards: Int) {

  val players: List[UUID] = List(player1, player2)

  def play(): HigherCardGame.GameResult = {
    val promotedCards: Set[Card] = promoteCards(players.length * numberOfCards)
    val hands: Iterator[Hand] = createHands(promotedCards, players)
    distributePrizes(dealHands(players, hands))
  }

}

object HigherCardGame {
  case class PlayerWithHand(playerId: UUID, hand: Hand)
  trait GameResult
  case object Tie extends GameResult
  case class ResultWithWinner(winner: UUID, loser: UUID) extends GameResult

  def dealHands(players: List[UUID], hands: Iterator[Hand]): List[PlayerWithHand] = {
    players.zip(hands).map { case (player, hand) => PlayerWithHand(playerId = player, hand = hand) }
  }

  def distributePrizes(playersWithHands: List[PlayerWithHand]): GameResult = {

    val multipleHands: List[Seq[Card]] = playersWithHands.map(_.hand.cards).sorted(CardOrdering.compareSeq).reverse

    multipleHands match {
      case List(firstHand, secondHand) if firstHand == secondHand => Tie
      case List(firstHand, _) =>
        val (winningPlayers: List[PlayerWithHand], losingPlayers: List[PlayerWithHand]) =
          playersWithHands.partition(_.hand == firstHand)

        val winningPlayer: UUID = winningPlayers.head.playerId
        val losingPlayer: UUID = losingPlayers.head.playerId
        ResultWithWinner(winningPlayer, losingPlayer)
    }

  }
}
