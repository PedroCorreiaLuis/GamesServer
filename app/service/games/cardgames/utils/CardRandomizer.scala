package service.games.cardgames.utils

import org.scalacheck.Gen._
import service.games.cardgames.{Card, CardType, Suit}

object CardRandomizer extends RandomizerImplicits {

  def cardGenerator: Card = {
    val suit: Suit = oneOf(Suit.allSuits)
    val cardType: CardType = oneOf(CardType.allCardTypes)

    Card(suit = suit, cardType = cardType)
  }

}
