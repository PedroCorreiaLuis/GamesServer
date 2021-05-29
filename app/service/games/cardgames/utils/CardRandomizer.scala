package service.games.cardgames.utils

import org.scalacheck.Gen._
import service.games.cardgames.{Card, CardType, Hand, Suit}

import java.util.UUID
import scala.annotation.tailrec

object CardRandomizer extends RandomizerImplicits {

  def generateCard: Card = {
    val suit: Suit = oneOf(Suit.allSuits)
    val cardType: CardType = oneOf(CardType.allCardTypes)

    Card(suit = suit, cardType = cardType)
  }

  def promoteCards(targetNumberOfCards: Int): Set[Card] = {
    @tailrec
    def recursionPromoteCards(targetNumberOfCardsAux: Int, promotedCards: Set[Card] = Set.empty[Card]): Set[Card] = {
      val newCard: Card = generateCard
      val currentPromotedCards: Set[Card] = promotedCards + newCard
      if (currentPromotedCards.size == targetNumberOfCards) currentPromotedCards
      else recursionPromoteCards(targetNumberOfCardsAux, currentPromotedCards)
    }
    recursionPromoteCards(targetNumberOfCards)
  }

  def createHands(promotedCards: Set[Card], players: List[UUID]): Iterator[Hand] = {
    val playersNumber: Int = players.length
    val groupedCards: Iterator[List[Card]] = promotedCards.toList.grouped(playersNumber)
    groupedCards.map(cards => Hand(cards: _*))

  }

}
