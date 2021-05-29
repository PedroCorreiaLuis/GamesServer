package api.controllers

import play.api.mvc.{AbstractController, ControllerComponents}
import service.games.GameService

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class GameController @Inject() (implicit val ec: ExecutionContext, cc: ControllerComponents, gameService: GameService)
    extends AbstractController(cc) {}
