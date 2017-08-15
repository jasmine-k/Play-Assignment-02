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

class UpdatePasswordControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite {


  val mockUserRepository = mock[UserRepository]
  val mockUserForms = mock[UserForms]
  val mockMessagesApi = mock[MessagesApi]
  val userForms = new UserForms
  implicit lazy val materializer: Materializer = app.materializer

  val updatePasswordController = new UpdatePasswordController(mockUserRepository, mockUserForms, mockMessagesApi)

  val userUpdatedPassword = UpdatePassword("jas@gmail.com", "jasmine","jasmine")

  "Update Password Controller" should {

    "be able to update password" in {

      when(mockUserForms.UpdatePasswordConstraintList).thenReturn(userForms.UpdatePasswordConstraintList.fill(userUpdatedPassword))
      when(mockUserRepository.getUserId("jas@gmail.com")).thenReturn(Future(1))
      when(mockUserRepository.updateUserPassword(ArgumentMatchers.any(classOf[String]), ArgumentMatchers.any(classOf[String]))).thenReturn(Future(true))
      val result = call(updatePasswordController.updatePassword(), FakeRequest(POST, "/password").withFormUrlEncodedBody(
        "email" -> "jas@gmail.com", "password" -> "jasmine","confirmPassword"->"jasmine"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")

    }

    "not be able to update password in the table" in {

      when(mockUserForms.UpdatePasswordConstraintList).thenReturn(userForms.UpdatePasswordConstraintList.fill(userUpdatedPassword))
      when(mockUserRepository.getUserId("jas@gmail.com")).thenReturn(Future(1))
      when(mockUserRepository.updateUserPassword(ArgumentMatchers.any(classOf[String]), ArgumentMatchers.any(classOf[String]))).thenReturn(Future(false))
      val result = call(updatePasswordController.updatePassword(), FakeRequest(POST, "/password").withFormUrlEncodedBody(
        "email" -> "jas@gmail.com", "password" -> "jasmine","confirmPassword"->"jasmine"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")

    }

    "not be able to update password in the table because user does not exists" in {

      when(mockUserForms.UpdatePasswordConstraintList).thenReturn(userForms.UpdatePasswordConstraintList.fill(userUpdatedPassword))
      when(mockUserRepository.getUserId("jas@gmail.com")).thenReturn(Future(-1))
      when(mockUserRepository.updateUserPassword(ArgumentMatchers.any(classOf[String]), ArgumentMatchers.any(classOf[String]))).thenReturn(Future(false))
      val result = call(updatePasswordController.updatePassword(), FakeRequest(POST, "/password").withFormUrlEncodedBody(
        "email" -> "jas@gmail.com", "password" -> "jasmine","confirmPassword"->"jasmine"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")

    }
  }

}
