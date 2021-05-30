package service.games.cardgames.implementations

case class CardGameDefinition(id: Int, singleFoldValue: Int, doubleFoldValue: Int, winValue: Int)

object CardGameDefinition {

  val HIGHER_SINGLE_CARD_DEFINITION: CardGameDefinition =
    CardGameDefinition(id = 1, singleFoldValue = 1, doubleFoldValue = 3, winValue = 10)

  val HIGHER_DOUBLE_CARD_DEFINITION: CardGameDefinition =
    CardGameDefinition(id = 2, singleFoldValue = 2, doubleFoldValue = 5, winValue = 20)

  val listOfAllGames: List[CardGameDefinition] = List(HIGHER_SINGLE_CARD_DEFINITION, HIGHER_DOUBLE_CARD_DEFINITION)

}
