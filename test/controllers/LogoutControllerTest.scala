package controllers

import akka.stream.Materializer
import models.{HobbyRepository, UserData, UserHobbyRepository, UserRepository}
import org.mindrot.jbcrypt.BCrypt
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.mockito.ArgumentMatchers
import org.scalatestplus.play.guice.GuiceOneServerPerSuite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LogoutControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite {

  val mockMessagesApi = mock[MessagesApi]
  implicit lazy val materializer: Materializer = app.materializer

  val logoutController = new LogoutController(mockMessagesApi)
  val userUpdatedPassword = UpdatePassword("jas@gmail.com", "jasmine", "jasmine")

  "Logout Controller" should {

    "be able to logout successfully" in {
      val result = call(logoutController.logout(), FakeRequest(GET, "/logout").withSession("userId"->"1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/index")

    }
    "not be able to logout" in {
      val result = call(logoutController.logout(), FakeRequest(GET, "/logout"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/index")
    }
  }
}
