package api.controllers

import play.api.mvc.{AbstractController, ControllerComponents}
import service.games.GameService

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class PlayerController @Inject()(implicit val ec: ExecutionContext,
                                 cc: ControllerComponents,
                                 chatService: GameService)
    extends AbstractController(cc) {}
