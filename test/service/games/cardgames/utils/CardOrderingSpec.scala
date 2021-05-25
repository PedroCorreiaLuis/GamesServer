package service.games.cardgames.utils

import base.BaseSpec
import service.games.cardgames.CardFixtures.{hand1, _}
import service.games.cardgames.utils.CardOrdering._

class CardOrderingSpec extends BaseSpec {

  it should "correctly compare Cards by their values" in {

    compare(aceOfSpades, aceOfDiamonds) shouldBe 0
    compare(aceOfSpades, kingOfClubs) shouldBe 1
    compare(tenOfClubs, kingOfClubs) shouldBe -1
    aceOfDiamonds > kingOfClubs shouldBe true
    aceOfDiamonds > aceOfSpades shouldBe false

  }

  it should "correctly sort Cards by their values" in {
    hand1.sorted(CardOrdering) shouldBe hand1
    hand2.sorted(CardOrdering) shouldBe hand2.reverse
  }

}
