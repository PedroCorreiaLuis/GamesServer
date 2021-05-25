package service.games.cardgames

sealed trait CardType {
  val value: Int
}

object CardType {

  case object Ace extends CardType {
    override val value: Int = 13
  }

  case object King extends CardType {
    override val value: Int = 12
  }

  case object Queen extends CardType {
    override val value: Int = 11
  }

  case object Jack extends CardType {
    override val value: Int = 10
  }

  case object Ten extends CardType {
    override val value: Int = 9
  }

  case object Nine extends CardType {
    override val value: Int = 8
  }

  case object Eight extends CardType {
    override val value: Int = 7
  }

  case object Seven extends CardType {
    override val value: Int = 6
  }

  case object Six extends CardType {
    override val value: Int = 5
  }

  case object Five extends CardType {
    override val value: Int = 4
  }

  case object Four extends CardType {
    override val value: Int = 3
  }

  case object Three extends CardType {
    override val value: Int = 2
  }

  case object Two extends CardType {
    override val value: Int = 1
  }

  val allCardTypes: Seq[CardType] = Seq(
    Ace,
    King,
    Queen,
    Jack,
    Ten,
    Nine,
    Eight,
    Seven,
    Six,
    Five,
    Four,
    Three,
    Two
  )

}
