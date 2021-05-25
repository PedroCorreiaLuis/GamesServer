package service.games.cardgames

import service.games.cardgames.CardType.{Ace, King, Ten}
import service.games.cardgames.Suit.{Clubs, Diamonds, Spades}

object CardFixtures {
  val aceOfSpades: Card = Card(Spades, Ace)
  val aceOfDiamonds: Card = Card(Diamonds, Ace)

  val kingOfClubs: Card = Card(Clubs, King)
  val tenOfClubs: Card = Card(Clubs, Ten)

  val hand1 = List(tenOfClubs, kingOfClubs)
  val hand2 = List(aceOfDiamonds, kingOfClubs)
}
