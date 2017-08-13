package controllers

import models.{UserData, UserRepository}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.mockito.Mockito._
import play.api.i18n.MessagesApi
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class AuthenticationControllerTest extends PlaySpec with MockitoSugar{


  val mockUserRepository = mock[UserRepository]
  val mockUserForms = mock[UserForms]
  val mockMessagesApi = mock[MessagesApi]

  val userForms = new UserForms

  val authenticationController = new AuthenticationController(mockUserRepository, mockUserForms,mockMessagesApi)

  "authenticationController" should {
    "be able to sign up new user" in {

      val newUserDataWithoutId = User("jas","kaur", "female","03/01/1995","jas@gmail.com","jasmine","jasmine")
      val newUserDataWithId = UserData(1, "jas","kaur", "female","03/01/1995","jas@gmail.com","jasmine")
      when(mockUserForms.userRegistrationConstraintList).thenReturn( userForms.userRegistrationConstraintList.fill(newUserDataWithoutId))
      when(mockUserRepository.store(newUserDataWithId)).thenReturn(Future(true))
      when(mockUserRepository.emailValidation("jas@gmail.com")).thenReturn(Future(None))

      val result = authenticationController.signUpValidation.apply(FakeRequest(POST,"/").withFormUrlEncodedBody(
        "firstName" -> "jas", "lastName" -> "kaur", "gender" -> "female", "dateOfBirth" -> "03/01/1995",
        "email" -> "jas@gmail.com", "password" -> "jasmine","confirmPassword"->"jasmine"))


      redirectLocation(result) mustBe Some("/home")
    }
  }

}
