package service.games.cardgames.utils

import service.games.cardgames.Card

object CardOrdering extends Ordering[Card] {

  def compare(a: Card, b: Card): Int =
    a.cardType.value.compare(b.cardType.value)

  def compareSeq(a: Seq[Card], b: Seq[Card]): Int = {
    val sortedHandA: Seq[Card] = a.sorted(this)
    val sortedHandB: Seq[Card] = b.sorted(this)

    sortedHandA.zip(sortedHandB).foldLeft(0) {
      case (acc, (cardA, cardB)) =>
        acc match {
          case 0      => compare(cardA, cardB)
          case result => result
        }
    }

  }
}
