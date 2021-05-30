import akka.actor.{ActorRef, ActorSystem, Props}
import com.google.inject.AbstractModule
import service.AppManager

import scala.concurrent.ExecutionContext

/** Outline the database to be used implicitly */
class Module extends AbstractModule {

  override def configure(): Unit = {

    implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

    implicit val system: ActorSystem = ActorSystem("GamesActorSystem")
    val appManager: ActorRef = system.actorOf(Props[AppManager], "appManager")

  }
}
