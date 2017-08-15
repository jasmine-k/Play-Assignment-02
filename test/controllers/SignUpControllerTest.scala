package controllers

import akka.stream.Materializer
import models._
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
class SignUpControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite {


  val mockUserRepository = mock[UserRepository]
  val mockUserForms = mock[UserForms]
  val mockMessagesApi = mock[MessagesApi]
  val mockHobbyRepository = mock[HobbyRepository]
  val mockUserHobbyRepository = mock[UserHobbyRepository]
  val userForms = new UserForms

  implicit lazy val materializer: Materializer = app.materializer

  val signUpController = new SignUpController(mockUserRepository, mockUserForms, mockHobbyRepository,
    mockUserHobbyRepository, mockMessagesApi)

  val mobile = 9999999999L
  val age = 18
  val name = Name("jas", Option("kaur"), "kaur")
  val newUserDataWithoutId = User(name, mobile, "female", age, "jas@gmail.com", "jasmine", "jasmine", List("Singing", "Dancing"))
  val newUserDataWithId = UserData(1, "jas", Option("kaur"), "kaur", mobile, "female", age, "jas@gmail.com", "jasmine", false, true)
  val newUserToLogin = UserLogin("jas@gmail.com", "jasmine")

  "signUpController" should {
    "be able to sign up new user who is Active but not Admin" in {

      when(mockUserForms.userRegistrationConstraintList).thenReturn(userForms.userRegistrationConstraintList.fill(newUserDataWithoutId))
      when(mockUserRepository.checkIfUserExists("jas@gmail.com", "jasmine")).thenReturn(Future(true))
      when(mockUserRepository.emailValidation("jas@gmail.com")).thenReturn(Future(None))
      when(mockUserRepository.addNewUser(ArgumentMatchers.any(classOf[UserData]))).thenReturn(Future(true))
      when(mockUserRepository.getUserId("jas@gmail.com")).thenReturn(Future(1))
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing", "Dancing", "Travelling", "Swimming", "Sports")))
      when(mockHobbyRepository.getHobbiesId(List("Singing", "Dancing"))).thenReturn(Future(List(List(1), List(2))))
      when(mockUserHobbyRepository.addUserHobby(1, List(List(1), List(2)))).thenReturn(Future(true))
      val result = call(signUpController.signUpValidation(), FakeRequest(POST, "/").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9898989898", "gender" -> "female", "age" -> "18",
        "email" -> "jas@gmail.com", "password" -> "jasmine", "confirmPassword" -> "jasmine", "hobbies[0]" -> "Singing", "hobbies[1]" -> "Dancing"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/index")

    }

    "be not able to sign up new user due to fail of addHobbies" in {

      when(mockUserForms.userRegistrationConstraintList).thenReturn(userForms.userRegistrationConstraintList.fill(newUserDataWithoutId))
      when(mockUserRepository.emailValidation("jas@gmail.com")).thenReturn(Future(None))
      when(mockUserRepository.addNewUser(ArgumentMatchers.any(classOf[UserData]))).thenReturn(Future(true))
      when(mockUserRepository.getUserId("jas@gmail.com")).thenReturn(Future(1))
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing", "Dancing", "Travelling", "Swimming", "Sports")))
      when(mockHobbyRepository.getHobbiesId(List("Singing", "Dancing"))).thenReturn(Future(List(List(1), List(2))))
      when(mockUserHobbyRepository.addUserHobby(1, List(List(1), List(2)))).thenReturn(Future(false))
      val result = call(signUpController.signUpValidation(), FakeRequest(POST, "/").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9898989898", "gender" -> "female", "age" -> "18",
        "email" -> "jas@gmail.com", "password" -> "jasmine", "confirmPassword" -> "jasmine", "hobbies[0]" -> "Singing", "hobbies[1]" -> "Dancing"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")

    }

    "not be able to sign up new user due to fail of addNewUser" in {

      when(mockUserForms.userRegistrationConstraintList).thenReturn(userForms.userRegistrationConstraintList.fill(newUserDataWithoutId))
      when(mockUserRepository.emailValidation("jas@gmail.com")).thenReturn(Future(None))
      when(mockUserRepository.addNewUser(ArgumentMatchers.any(classOf[UserData]))).thenReturn(Future(false))
      when(mockUserRepository.getUserId("jas@gmail.com")).thenReturn(Future(1))
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing", "Dancing", "Travelling", "Swimming", "Sports")))
      when(mockHobbyRepository.getHobbiesId(List("Singing", "Dancing"))).thenReturn(Future(List(List(1), List(2))))
      when(mockUserHobbyRepository.addUserHobby(1, List(List(1), List(2)))).thenReturn(Future(true))
      val result = call(signUpController.signUpValidation(), FakeRequest(POST, "/").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9898989898", "gender" -> "female", "age" -> "18",
        "email" -> "jas@gmail.com", "password" -> "jasmine", "confirmPassword" -> "jasmine", "hobbies[0]" -> "Singing", "hobbies[1]" -> "Dancing"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")

    }

    "not be able to sign up new user due to email already exists" in {

      when(mockUserForms.userRegistrationConstraintList).thenReturn(userForms.userRegistrationConstraintList.fill(newUserDataWithoutId))
      when(mockUserRepository.emailValidation("jas@gmail.com")).thenReturn(Future(Some("")))
      when(mockUserRepository.addNewUser(ArgumentMatchers.any(classOf[UserData]))).thenReturn(Future(true))
      when(mockUserRepository.getUserId("jas@gmail.com")).thenReturn(Future(1))
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing", "Dancing", "Travelling", "Swimming", "Sports")))
      when(mockHobbyRepository.getHobbiesId(List("Singing", "Dancing"))).thenReturn(Future(List(List(1), List(2))))
      when(mockUserHobbyRepository.addUserHobby(1, List(List(1), List(2)))).thenReturn(Future(true))
      val result = call(signUpController.signUpValidation(), FakeRequest(POST, "/").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9898989898", "gender" -> "female", "age" -> "18",
        "email" -> "jas@gmail.com", "password" -> "jasmine", "confirmPassword" -> "jasmine", "hobbies[0]" -> "Singing", "hobbies[1]" -> "Dancing"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")

    }
  }
}