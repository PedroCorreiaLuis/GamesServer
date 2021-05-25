package service.games.cardgames

case class Card(suit: Suit, cardType: CardType, name: String)

object Card {

  def apply(suit: Suit, cardType: CardType): Card = {
    Card(
      suit = suit,
      cardType = cardType,
      name = s"${cardType.toString} of ${suit.toString}"
    )
  }
}
