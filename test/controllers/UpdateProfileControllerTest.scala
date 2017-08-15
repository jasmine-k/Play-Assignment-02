/*
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
  val age = 21
  val FOUR = 4
  val name = Name("jas", Option("kaur"), "kaur")
  val updatedUserData = UpdateUserDetails(name, mobile, "female", age, List("Swimming"))

  "UpdateProfileController" should {
    "be able to update profile of user who is Active but not Admin" in {

      when(mockUserForms.userUpdateConstraintList).thenReturn(userForms.userUpdateConstraintList.fill(updatedUserData))
      when(mockUserRepository.updateUserData(updatedUserData,1)).thenReturn(Future(true))
      when(mockUserHobbyRepository.deleteUserHobby(1)).thenReturn(Future(true))
      when(mockUserHobbyRepository.addUserHobby(1, List(List(1)))).thenReturn(Future(true))
      when(mockHobbyRepository.getHobbiesId(List("Swimming"))).thenReturn(Future(List(List(FOUR))))
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing", "Dancing", "Travelling", "Swimming", "Sports")))
      val result = call(updateProfileController.updateProfile(), FakeRequest(POST, "/profile").withFormUrlEncodedBody(
        "name.firstName" -> "jas", "name.middleName" -> "kaur", "name.lastName" -> "kaur", "mobileNumber" -> "9898989898", "gender" -> "female",
        "age" -> "18","hobbies[4]" -> "Swimming").withSession("userId" -> "1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/profile")

    }


  }

}*/
