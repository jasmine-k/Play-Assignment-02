package controllers

import akka.stream.Materializer
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class HomeControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite {

  val userForms = new UserForms
  implicit lazy val materializer: Materializer = app.materializer

  val homeController = new HomeController

  "Home controller" should {
    "be able to show index page" in {

      val result = call(homeController.index(), FakeRequest(GET, "/index"))
      status(result) mustBe 200

    }
  }

}