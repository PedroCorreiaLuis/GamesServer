package service.games.cardgames

sealed trait Suit

case object Suit {
  case object Clubs extends Suit
  case object Diamonds extends Suit
  case object Hearts extends Suit
  case object Spades extends Suit

  val allSuits: Seq[Suit] = Seq(Clubs, Diamonds, Hearts, Spades)
}
