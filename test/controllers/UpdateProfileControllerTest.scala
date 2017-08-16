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
class UpdateProfileControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite {


  val mockUserRepository = mock[UserRepository]
  val mockUserForms = mock[UserForms]
  val mockMessagesApi = mock[MessagesApi]
  val mockHobbyRepository = mock[HobbyRepository]
  val mockUserHobbyRepository = mock[UserHobbyRepository]
  val userForms = new UserForms

  implicit lazy val materializer: Materializer = app.materializer

  val updateProfileController = new UpdateProfileController(mockUserRepository, mockUserForms, mockHobbyRepository,
    mockUserHobbyRepository, mockMessagesApi)

  val mobile = 9999999999L
  val age = 18
  val name = Name("jas", Option("kaur"), "kaur")
  val updatedProfile = UpdateUserDetails(name, mobile, "female", age, List("Singing"))

  "updateProfileController" should {
    "be able to update profile" in {

      when(mockUserForms.userUpdateConstraintList).thenReturn(userForms.userUpdateConstraintList.fill(updatedProfile))
      when(mockUserRepository.updateUserData(updatedProfile,1)).thenReturn(Future(true))
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing", "Dancing", "Travelling", "Swimming", "Sports")))
      when(mockUserHobbyRepository.deleteUserHobby(1)).thenReturn(Future(true))
      when(mockHobbyRepository.getHobbiesId(List("Singing"))).thenReturn(Future(List(List(1))))
      when(mockUserHobbyRepository.addUserHobby(1, List(List(1)))).thenReturn(Future(true))
      val result = call(updateProfileController.updateProfile(), FakeRequest(POST, "/profile").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9999999999", "gender" -> "female", "age" -> "18",
        "hobbies[1]" -> "Singing").withSession("userId"->"1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/profile")

    }

    "not be able to update profile coz session not created" in {

      when(mockUserForms.userUpdateConstraintList).thenReturn(userForms.userUpdateConstraintList.fill(updatedProfile))
      when(mockUserRepository.updateUserData(updatedProfile,1)).thenReturn(Future(true))
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing", "Dancing", "Travelling", "Swimming", "Sports")))
      when(mockUserHobbyRepository.deleteUserHobby(1)).thenReturn(Future(true))
      when(mockHobbyRepository.getHobbiesId(List("Singing"))).thenReturn(Future(List(List(1))))
      when(mockUserHobbyRepository.addUserHobby(1, List(List(1)))).thenReturn(Future(true))
      val result = call(updateProfileController.updateProfile(), FakeRequest(POST, "/profile").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9999999999", "gender" -> "female", "age" -> "18",
        "hobbies[1]" -> "Singing").withSession())

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/index")

    }

    "not be able to update profile" in {

      when(mockUserForms.userUpdateConstraintList).thenReturn(userForms.userUpdateConstraintList.fill(updatedProfile))
      when(mockUserRepository.updateUserData(updatedProfile,1)).thenReturn(Future(false))
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing", "Dancing", "Travelling", "Swimming", "Sports")))
      when(mockUserHobbyRepository.deleteUserHobby(1)).thenReturn(Future(true))
      when(mockHobbyRepository.getHobbiesId(List("Singing"))).thenReturn(Future(List(List(1))))
      when(mockUserHobbyRepository.addUserHobby(1, List(List(1)))).thenReturn(Future(true))
      val result = call(updateProfileController.updateProfile(), FakeRequest(POST, "/profile").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9999999999", "gender" -> "female", "age" -> "18",
        "hobbies[1]" -> "Singing").withSession("userId"->"1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/profile")

    }


    "not be able to update profile due to session issue" in {

      when(mockUserForms.userUpdateConstraintList).thenReturn(userForms.userUpdateConstraintList.fill(updatedProfile))
      when(mockUserRepository.updateUserData(updatedProfile,1)).thenReturn(Future(false))
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing", "Dancing", "Travelling", "Swimming", "Sports")))
      when(mockUserHobbyRepository.deleteUserHobby(1)).thenReturn(Future(true))
      when(mockHobbyRepository.getHobbiesId(List("Singing"))).thenReturn(Future(List(List(1))))
      when(mockUserHobbyRepository.addUserHobby(1, List(List(1)))).thenReturn(Future(true))
      val result = call(updateProfileController.updateProfile(), FakeRequest(POST, "/profile").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9999999999", "gender" -> "female", "age" -> "18",
        "hobbies[1]" -> "Singing").withSession())

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/index")

    }

    "not be able to delete hobbies" in {

      when(mockUserForms.userUpdateConstraintList).thenReturn(userForms.userUpdateConstraintList.fill(updatedProfile))
      when(mockUserRepository.updateUserData(updatedProfile,1)).thenReturn(Future(true))
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing", "Dancing", "Travelling", "Swimming", "Sports")))
      when(mockUserHobbyRepository.deleteUserHobby(1)).thenReturn(Future(false))
      when(mockHobbyRepository.getHobbiesId(List("Singing"))).thenReturn(Future(List(List(1))))
      when(mockUserHobbyRepository.addUserHobby(1, List(List(1)))).thenReturn(Future(true))
      val result = call(updateProfileController.updateProfile(), FakeRequest(POST, "/profile").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9999999999", "gender" -> "female", "age" -> "18",
        "hobbies[1]" -> "Singing").withSession("userId"->"1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/profile")

    }

    "not be able to delete hobbies due to session" in {

      when(mockUserForms.userUpdateConstraintList).thenReturn(userForms.userUpdateConstraintList.fill(updatedProfile))
      when(mockUserRepository.updateUserData(updatedProfile,1)).thenReturn(Future(true))
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing", "Dancing", "Travelling", "Swimming", "Sports")))
      when(mockUserHobbyRepository.deleteUserHobby(1)).thenReturn(Future(false))
      when(mockHobbyRepository.getHobbiesId(List("Singing"))).thenReturn(Future(List(List(1))))
      when(mockUserHobbyRepository.addUserHobby(1, List(List(1)))).thenReturn(Future(true))
      val result = call(updateProfileController.updateProfile(), FakeRequest(POST, "/profile").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9999999999", "gender" -> "female", "age" -> "18",
        "hobbies[1]" -> "Singing").withSession())

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/index")

    }

    "not be able to add hobbies in database" in {

      when(mockUserForms.userUpdateConstraintList).thenReturn(userForms.userUpdateConstraintList.fill(updatedProfile))
      when(mockUserRepository.updateUserData(updatedProfile,1)).thenReturn(Future(true))
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing", "Dancing", "Travelling", "Swimming", "Sports")))
      when(mockUserHobbyRepository.deleteUserHobby(1)).thenReturn(Future(true))
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockHobbyRepository.getHobbiesId(List("Singing"))).thenReturn(Future(List(List(1))))
      when(mockUserHobbyRepository.addUserHobby(1, List(List(1)))).thenReturn(Future(false))
      val result = call(updateProfileController.updateProfile(), FakeRequest(POST, "/profile").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9999999999", "gender" -> "female", "age" -> "18",
        "hobbies[1]" -> "Singing").withSession("userId"->"1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/profile")

    }

    "not be able to add hobbies in database due to session issue" in {

      when(mockUserForms.userUpdateConstraintList).thenReturn(userForms.userUpdateConstraintList.fill(updatedProfile))
      when(mockUserRepository.updateUserData(updatedProfile,1)).thenReturn(Future(true))
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing", "Dancing", "Travelling", "Swimming", "Sports")))
      when(mockUserHobbyRepository.deleteUserHobby(1)).thenReturn(Future(true))
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockHobbyRepository.getHobbiesId(List("Singing"))).thenReturn(Future(List(List(1))))
      when(mockUserHobbyRepository.addUserHobby(1, List(List(1)))).thenReturn(Future(false))
      val result = call(updateProfileController.updateProfile(), FakeRequest(POST, "/profile").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9999999999", "gender" -> "female", "age" -> "18",
        "hobbies[1]" -> "Singing").withSession())

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/index")

    }
  }
}