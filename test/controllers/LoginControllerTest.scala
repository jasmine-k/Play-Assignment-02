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

class LoginControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite {


  val mockUserRepository = mock[UserRepository]
  val mockUserForms = mock[UserForms]
  val mockMessagesApi = mock[MessagesApi]
  val mockHobbyRepository = mock[HobbyRepository]
  val mockUserHobbyRepository = mock[UserHobbyRepository]
  val mockBCrypt = mock[BCrypt]
  val userForms = new UserForms
  implicit lazy val materializer: Materializer = app.materializer

  val loginController = new LoginController(mockUserRepository, mockUserForms, mockHobbyRepository,
    mockUserHobbyRepository, mockMessagesApi)

  val newUserToLogin = UserLogin("jas@gmail.com", "jasmine")

  "signUpController" should {

    "be able to login new user who is Active but not Admin" in {

      when(mockUserForms.UserLoginConstraintList).thenReturn(userForms.UserLoginConstraintList.fill(newUserToLogin))
      when(mockUserRepository.checkIfUserExists("jas@gmail.com", "jasmine")).thenReturn(Future(true))
      when(mockUserRepository.getUserId("jas@gmail.com")).thenReturn(Future(1))
      when(mockUserRepository.isAdmin("jas@gmail.com")).thenReturn(Future(false))
      when(mockUserRepository.isActive("jas@gmail.com")).thenReturn(Future(true))
      val result = call(loginController.loginValidation, FakeRequest(POST, "/login").withFormUrlEncodedBody(
        "email" -> "jas@gmail.com", "password" -> "jasmine"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/profile")

    }

    "not be able to login new user because user does not exists" in {

      when(mockUserForms.UserLoginConstraintList).thenReturn(userForms.UserLoginConstraintList.fill(newUserToLogin))
      when(mockUserRepository.checkIfUserExists("jas@gmail.com", "jasmine")).thenReturn(Future(false))
      when(mockUserRepository.getUserId("jas@gmail.com")).thenReturn(Future(1))
      when(mockUserRepository.isAdmin("jas@gmail.com")).thenReturn(Future(false))
      when(mockUserRepository.isActive("jas@gmail.com")).thenReturn(Future(true))
      val result = call(loginController.loginValidation, FakeRequest(POST, "/login").withFormUrlEncodedBody(
        "email" -> "jas@gmail.com", "password" -> "jasmine"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")

    }

    "not be able to login new user because user id is negative" in {

      when(mockUserForms.UserLoginConstraintList).thenReturn(userForms.UserLoginConstraintList.fill(newUserToLogin))
      when(mockUserRepository.checkIfUserExists("jas@gmail.com", "jasmine")).thenReturn(Future(true))
      when(mockUserRepository.getUserId("jas@gmail.com")).thenReturn(Future(-1))
      when(mockUserRepository.isAdmin("jas@gmail.com")).thenReturn(Future(false))
      when(mockUserRepository.isActive("jas@gmail.com")).thenReturn(Future(true))
      val result = call(loginController.loginValidation, FakeRequest(POST, "/login").withFormUrlEncodedBody(
        "email" -> "jas@gmail.com", "password" -> "jasmine"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")

    }

    "not be able to login new user because user is inactive" in {

      when(mockUserForms.UserLoginConstraintList).thenReturn(userForms.UserLoginConstraintList.fill(newUserToLogin))
      when(mockUserRepository.checkIfUserExists("jas@gmail.com", "jasmine")).thenReturn(Future(true))
      when(mockUserRepository.getUserId("jas@gmail.com")).thenReturn(Future(1))
      when(mockUserRepository.isAdmin("jas@gmail.com")).thenReturn(Future(false))
      when(mockUserRepository.isActive("jas@gmail.com")).thenReturn(Future(false))
      val result = call(loginController.loginValidation, FakeRequest(POST, "/login").withFormUrlEncodedBody(
        "email"->"jas@gmail.com","password"->"jasmine"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")

    }

    "be able to login new user who is admin" in {

      when(mockUserForms.UserLoginConstraintList).thenReturn(userForms.UserLoginConstraintList.fill(newUserToLogin))
      when(mockUserRepository.checkIfUserExists("jas@gmail.com", "jasmine")).thenReturn(Future(true))
      when(mockUserRepository.getUserId("jas@gmail.com")).thenReturn(Future(1))
      when(mockUserRepository.isAdmin("jas@gmail.com")).thenReturn(Future(true))
      when(mockUserRepository.isActive("jas@gmail.com")).thenReturn(Future(true))
      val result = call(loginController.loginValidation, FakeRequest(POST, "/login").withFormUrlEncodedBody(
        "email" -> "jas@gmail.com", "password" -> "jasmine"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/profile")

    }
  }

}
