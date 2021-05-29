package service.games.cardgames.utils

import base.BaseSpec
import service.games.cardgames.Card
import service.games.cardgames.utils.CardRandomizer.generateCard

class CardRandomizerSpec extends BaseSpec {

  "Card randomizer" should {

    "generate random Cards" in {
      val range: Seq[Int] = 1 to 100

      def generatedCards: Seq[Card] = range.map(_ => generateCard)

      generatedCards should not be generatedCards

    }

  }
}
