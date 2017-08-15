/*
package controllers

import models.{HobbyRepository, UserData, UserHobbyRepository, UserRepository}
import org.mindrot.jbcrypt.BCrypt
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.mockito.ArgumentMatchers


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthenticationControllerTest extends PlaySpec with MockitoSugar {


  val mockUserRepository = mock[UserRepository]
  val mockUserForms = mock[UserForms]
  val mockMessagesApi = mock[MessagesApi]
  val mockHobbyRepository = mock[HobbyRepository]
  val mockUserHobbyRepository = mock[UserHobbyRepository]
  val mockBCrypt = mock[BCrypt]
  val userForms = new UserForms

  val authenticationController = new AuthenticationController(mockUserRepository, mockUserForms, mockHobbyRepository,
    mockUserHobbyRepository, mockMessagesApi)

  val name = Name("jas", Option("kaur"), "kaur")
  val newUserDataWithoutId = User(name, 9898989898L, "female", 18, "jas@gmail.com","jasmine", "jasmine", List("Singing", "Dancing"))
  val newUserDataWithId = UserData(1, "jas", Option("kaur"), "kaur", 9898989898L, "female", 18, "jas@gmail.com", "jasmine", false, true)
  val newUserToLogin = UserLogin("jas@gmail.com","jasmine")

  "authenticationController" should {
    "be able to sign up new user who is Active but not Admin" in {

      when(mockUserForms.userRegistrationConstraintList).thenReturn(userForms.userRegistrationConstraintList.fill(newUserDataWithoutId))
      when(mockUserRepository.checkIfUserExists("jas@gmail.com","jasmine")).thenReturn(Future(true))
      when(mockUserRepository.addNewUser(newUserDataWithId)).thenReturn(Future(true))
      when(mockUserRepository.emailValidation("jas@gmail.com")).thenReturn(Future(None))
      when(mockUserRepository.addNewUser(ArgumentMatchers.any(classOf[UserData]))).thenReturn(Future(true))
      when(mockUserRepository.getUserId("jas@gmail.com")).thenReturn(Future(1))
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing","Dancing","Travelling","Swimming","Sports")))
      when(mockHobbyRepository.getHobbiesId(List("Singing", "Dancing"))).thenReturn(Future(List(List(1), List(2))))
      when(mockUserHobbyRepository.addUserHobby(1, List(List(1), List(2)))).thenReturn(Future(true))
      val result = authenticationController.signUpValidation.apply(FakeRequest(POST, "/").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9898989898", "gender" -> "female", "age" -> "18",
        "email" -> "jas@gmail.com", "password" -> "jasmine", "confirmPassword" -> "jasmine", "hobbies[0]" -> "Singing", "hobbies[1]" -> "Dancing"))

      redirectLocation(result) mustBe Some("/index")

    }

    "be not able to sign up new user due to fail of addHobbies" in {

      when(mockUserForms.userRegistrationConstraintList).thenReturn(userForms.userRegistrationConstraintList.fill(newUserDataWithoutId))
      when(mockUserRepository.addNewUser(newUserDataWithId)).thenReturn(Future(true))
      when(mockUserRepository.emailValidation("jas@gmail.com")).thenReturn(Future(None))
      when(mockUserRepository.addNewUser(ArgumentMatchers.any(classOf[UserData]))).thenReturn(Future(true))
      when(mockUserRepository.getUserId("jas@gmail.com")).thenReturn(Future(1))
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing","Dancing","Travelling","Swimming","Sports")))
      when(mockHobbyRepository.getHobbiesId(List("Singing", "Dancing"))).thenReturn(Future(List(List(1), List(2))))
      when(mockUserHobbyRepository.addUserHobby(1, List(List(1), List(2)))).thenReturn(Future(false))
      val result = authenticationController.signUpValidation.apply(FakeRequest(POST, "/").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9898989898", "gender" -> "female", "age" -> "18",
        "email" -> "jas@gmail.com", "password" -> "jasmine", "confirmPassword" -> "jasmine", "hobbies[0]" -> "Singing", "hobbies[1]" -> "Dancing"))

      redirectLocation(result) mustBe Some("/")

    }

    "not be able to sign up new user due to fail of addNewUser" in {

      when(mockUserForms.userRegistrationConstraintList).thenReturn(userForms.userRegistrationConstraintList.fill(newUserDataWithoutId))
      when(mockUserRepository.checkIfUserExists("jas@gmail.com","jasmine")).thenReturn(Future(true))
      when(mockUserRepository.addNewUser(newUserDataWithId)).thenReturn(Future(false))
      when(mockUserRepository.emailValidation("jas@gmail.com")).thenReturn(Future(None))
      when(mockUserRepository.addNewUser(ArgumentMatchers.any(classOf[UserData]))).thenReturn(Future(true))
      when(mockUserRepository.getUserId("jas@gmail.com")).thenReturn(Future(1))
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing","Dancing","Travelling","Swimming","Sports")))
      when(mockHobbyRepository.getHobbiesId(List("Singing", "Dancing"))).thenReturn(Future(List(List(1), List(2))))
      when(mockUserHobbyRepository.addUserHobby(1, List(List(1), List(2)))).thenReturn(Future(true))
      val result = authenticationController.signUpValidation.apply(FakeRequest(POST, "/").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9898989898", "gender" -> "female", "age" -> "18",
        "email" -> "jas@gmail.com", "password" -> "jasmine", "confirmPassword" -> "jasmine", "hobbies[0]" -> "Singing", "hobbies[1]" -> "Dancing"))

      redirectLocation(result) mustBe Some("/")

    }

    "be able to login new user who is Active but not Admin" in {

      when(mockUserForms.userRegistrationConstraintList).thenReturn(userForms.userRegistrationConstraintList.fill(newUserDataWithoutId))
      when(mockUserRepository.checkIfUserExists("jas@gmail.com","jasmine")).thenReturn(Future(true))
      when(mockUserRepository.getUserId("jas@gmail.com")).thenReturn(Future(1))
      when(mockUserRepository.isAdmin("jas@gmail.com")).thenReturn(Future(false))
      when(mockUserRepository.isActive("jas@gmail.com")).thenReturn(Future(true))
      val result = authenticationController.loginValidation.apply(FakeRequest(POST, "/").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9898989898", "gender" -> "female", "age" -> "18",
        "email" -> "jas@gmail.com", "password" -> "jasmine", "confirmPassword" -> "jasmine", "hobbies[0]" -> "Singing", "hobbies[1]" -> "Dancing"))

      redirectLocation(result) mustBe Some("/updateProfile")

    }
  }
/*
  "authenticationController" should {
    "not be able to sign up new user because of issue with addNewUser" in {

      when(mockUserForms.userRegistrationConstraintList).thenReturn(userForms.userRegistrationConstraintList.fill(newUserDataWithoutId))
      when(mockUserRepository.addNewUser(newUserDataWithId)).thenReturn(Future(false))
      when(mockUserRepository.emailValidation("jas@gmail.com")).thenReturn(Future(None))
      when(mockUserRepository.getUserId("jas@gmail.com")).thenReturn(Future(1))
      when(mockHobbyRepository.getHobbiesId(List("Singing", "Dancing"))).thenReturn(Future(List(List(1), List(2))))
      when(mockUserHobbyRepository.addUserHobby(1, List(List(1), List(2)))).thenReturn(Future(true))
      val result = authenticationController.signUpValidation.apply(FakeRequest(POST, "/").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9898989898", "gender" -> "female", "age" -> "18",
        "email" -> "jas@gmail.com", "password" -> "jasmine", "confirmPassword" -> "jasmine", "hobbies[0]" -> "Singing", "hobbies[1]" -> "Dancing"))

      redirectLocation(result) mustBe Some("/")

    }
  }

  "authenticationController" should {
    "not be able to sign up new user because of issue with emailValidation" in {

      when(mockUserForms.userRegistrationConstraintList).thenReturn(userForms.userRegistrationConstraintList.fill(newUserDataWithoutId))
      when(mockUserRepository.addNewUser(newUserDataWithId)).thenReturn(Future(true))
      when(mockUserRepository.emailValidation("jas@gmail.com")).thenReturn(Future(Some("")))
      when(mockUserRepository.getUserId("jas@gmail.com")).thenReturn(Future(1))
      when(mockHobbyRepository.getHobbiesId(List("Singing", "Dancing"))).thenReturn(Future(List(List(1), List(2))))
      when(mockUserHobbyRepository.addUserHobby(1, List(List(1), List(2)))).thenReturn(Future(true))
      val result = authenticationController.signUpValidation.apply(FakeRequest(POST, "/").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9898989898", "gender" -> "female", "age" -> "18",
        "email" -> "jas@gmail.com", "password" -> "jasmine", "confirmPassword" -> "jasmine", "hobbies[0]" -> "Singing", "hobbies[1]" -> "Dancing"))


      //val result2 = authenticationController.addUserHobby.apply(FakeRequest)

      redirectLocation(result) mustBe Some("/")

    }
  }
  "authenticationController" should {
    "not be able to sign up new user because of issue with addUserHobby" in {

      when(mockUserForms.userRegistrationConstraintList).thenReturn(userForms.userRegistrationConstraintList.fill(newUserDataWithoutId))
      when(mockUserRepository.addNewUser(newUserDataWithId)).thenReturn(Future(true))
      when(mockUserRepository.emailValidation("jas@gmail.com")).thenReturn(Future(None))
      when(mockUserRepository.getUserId("jas@gmail.com")).thenReturn(Future(1))
      when(mockHobbyRepository.getHobbiesId(List("Singing", "Dancing"))).thenReturn(Future(List(List(1), List(2))))
      when(mockUserHobbyRepository.addUserHobby(1, List(List(1), List(2)))).thenReturn(Future(false))
      val result = authenticationController.signUpValidation.apply(FakeRequest(POST, "/").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9898989898", "gender" -> "female", "age" -> "18",
        "email" -> "jas@gmail.com", "password" -> "jasmine", "confirmPassword" -> "jasmine", "hobbies[0]" -> "Singing", "hobbies[1]" -> "Dancing"))


      //val result2 = authenticationController.addUserHobby.apply(FakeRequest)

      redirectLocation(result) mustBe Some("/")

    }
  }

  "authenticationController" should {
    "be able to login new user" in {

      when(mockUserForms.UserLoginConstraintList).thenReturn(userForms.UserLoginConstraintList.fill(newUserToLogin))

      val result = authenticationController.loginValidation.apply(FakeRequest(POST, "/login").withFormUrlEncodedBody(
        "email" -> "jas@gmail.com", "password" -> "jasmine"))
      //val result2 = authenticationController.addUserHobby.apply(FakeRequest)
      redirectLocation(result) mustBe Some("/successpage")

    }*/
  }

*/
