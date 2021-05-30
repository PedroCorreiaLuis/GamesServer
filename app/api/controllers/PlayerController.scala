package api.controllers

import akka.actor.{ActorSelection, ActorSystem}
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import api.controllers.JsonObjectsUtils.jsonErrors
import api.model.PlayerDTO
import play.api.libs.json.{JsValue, _}
import play.api.mvc.{AbstractController, Action, ControllerComponents, Request}
import play.api.routing.sird.?
import service.games.GameService

import javax.inject.Inject
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class PlayerController @Inject() (
  implicit ec: ExecutionContext,
  system: ActorSystem,
  mat: Materializer,
  cc: ControllerComponents,
  playerService: GameService
) extends AbstractController(cc) {
  implicit val timeout: Timeout = Timeout(1 second)

  def signUp: Action[JsValue] = Action(parse.json).async { request: Request[JsValue] =>
    val playerResult: JsResult[PlayerDTO] = request.body.validate[PlayerDTO]

    playerResult.fold(
      errors => Future.successful { BadRequest(jsonErrors(errors)) },
      player => {
        val actorSelected: ActorSelection = system.actorSelection("system/appManager")
        val futureResponse: Future[Any] = actorSelected ? player
        futureResponse.map(_ => Ok)
      }
    )

  }

}
