package base

import akka.testkit.{ImplicitSender, TestKit, TestKitBase}
import org.scalatest.BeforeAndAfterAll

trait ActorSpec extends TestKitBase with ImplicitSender with BaseSpec with BeforeAndAfterAll {

  override def afterAll(): Unit = {

    TestKit.shutdownActorSystem(system)

  }
}
