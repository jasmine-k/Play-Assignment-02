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

class ApplicationTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite {


  val mockUserRepository = mock[UserRepository]
  val mockUserForms = mock[UserForms]
  val mockMessagesApi = mock[MessagesApi]
  val mockHobbyRepository = mock[HobbyRepository]
  val mockUserHobbyRepository = mock[UserHobbyRepository]
  val mockAssignmentRepository = mock[AssignmentRepository]

  val userForms = new UserForms

  implicit lazy val materializer: Materializer = app.materializer

  val application = new Application(mockMessagesApi, mockUserForms, mockUserRepository,
    mockHobbyRepository, mockUserHobbyRepository, mockAssignmentRepository)

  val mobile = 9898989898L
  val age = 18
  val name = Name("jas", Option("kaur"), "kaur")
  val newUserDataWithoutId = User(name, mobile, "female", age, "jas@gmail.com", "jasmine", "jasmine", List("Singing", "Dancing"))
  val newUser = UserData(1, "jas", Option("kaur"), "kaur", mobile, "female", age, "jas@gmail.com", "jasmine", true, true)
  val newUserToLogin = UserLogin("jas@gmail.com", "jasmine")

  "application controller" should {
    "be able to show sign up page" in {

      when(mockUserForms.userRegistrationConstraintList).thenReturn(userForms.userRegistrationConstraintList)
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing", "Dancing", "Travelling", "Swimming", "Sports")))
      val result = call(application.signUp(), FakeRequest(GET, "/"))

      status(result) mustBe 200
    }

    "be able to show login up page" in {

      when(mockUserForms.userLoginConstraintList).thenReturn(userForms.userLoginConstraintList)
      val result = call(application.signUp(), FakeRequest(GET, "/login"))

      status(result) mustBe 200
    }

    "be able to show forget password up page" in {

      when(mockUserForms.updatePasswordConstraintList).thenReturn(userForms.updatePasswordConstraintList)
      val result = call(application.signUp(), FakeRequest(GET, "/password"))

      status(result) mustBe 200
    }

    "be able to show index page" in {

      val result = call(application.index(), FakeRequest(GET, "/index"))
      status(result) mustBe 200
    }

    /*"be able to show admin profile page" in {

      val result = call(application.(), FakeRequest(GET, "/index"))
      status(result) mustBe 200
    }*/

    "be able to show view user profile page" in {

      when(mockUserRepository.getNormalUser()).thenReturn(Future(List(newUser)))
      val result = call(application.viewUser(), FakeRequest(GET, "/user"))
      status(result) mustBe 200
    }

    "be able to show add assignments page" in {

      when(mockUserForms.assignmentConstraintList).thenReturn(userForms.assignmentConstraintList)
      val result = call(application.addAssignment(), FakeRequest(GET, "/addassignment"))
      status(result) mustBe 200
    }

    "be able to show view assignment page for normal user" in {

      val assignmentOneDetails = AssignmentDetails(1, "Assignment1", "Description1")
      val assignmentTwoDetails = AssignmentDetails(2, "Assignment2", "Description2")
      when(mockUserRepository.getUserById(1)).thenReturn(Future(newUser))
      when(mockAssignmentRepository.getAssignments).thenReturn(Future(List(assignmentOneDetails, assignmentTwoDetails)))
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(false))
      val result = call(application.viewAssignment(), FakeRequest(GET, "/assignment").withSession("userId" -> "1"))
      status(result) mustBe 200
    }

    "not be able to show view assignment page for normal user due to session problem" in {

      val assignmentOneDetails = AssignmentDetails(1, "Assignment1", "Description1")
      val assignmentTwoDetails = AssignmentDetails(2, "Assignment2", "Description2")
      when(mockUserRepository.getUserById(1)).thenReturn(Future(newUser))
      when(mockAssignmentRepository.getAssignments).thenReturn(Future(List(assignmentOneDetails, assignmentTwoDetails)))
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(false))
      val result = call(application.viewAssignment(), FakeRequest(GET, "/assignment").withSession())
      status(result) mustBe 303
    }

    "be able to show view assignment page for admin" in {

      val assignmentOneDetails = AssignmentDetails(1, "Assignment1", "Description1")
      val assignmentTwoDetails = AssignmentDetails(2, "Assignment2", "Description2")
      when(mockUserRepository.getUserById(1)).thenReturn(Future(newUser))
      when(mockAssignmentRepository.getAssignments).thenReturn(Future(List(assignmentOneDetails, assignmentTwoDetails)))
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      val result = call(application.viewAssignment(), FakeRequest(GET, "/assignment").withSession("userId" -> "1"))
      status(result) mustBe 200
    }

    "not be able to show view assignment page for admin due to session problem" in {

      val assignmentOneDetails = AssignmentDetails(1, "Assignment1", "Description1")
      val assignmentTwoDetails = AssignmentDetails(2, "Assignment2", "Description2")
      when(mockUserRepository.getUserById(1)).thenReturn(Future(newUser))
      when(mockAssignmentRepository.getAssignments).thenReturn(Future(List(assignmentOneDetails, assignmentTwoDetails)))
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      val result = call(application.viewAssignment(), FakeRequest(GET, "/assignment").withSession())
      status(result) mustBe 303
    }

    "be able to show update profile" in {

      when(mockUserRepository.getUserById(1)).thenReturn(Future(newUser))
      when(mockUserHobbyRepository.getUserHobby(1)).thenReturn(Future(List("Signing")))
      when(mockUserForms.userUpdateConstraintList).thenReturn(userForms.userUpdateConstraintList)
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing", "Dancing", "Travelling", "Swimming", "Sports")))
      val result = call(application.viewAssignment(), FakeRequest(GET, "/profile").withSession("userId" -> "1"))
      status(result) mustBe 200
    }

    "not be able to show update profile due to session" in {

      when(mockUserRepository.getUserById(1)).thenReturn(Future(newUser))
      when(mockUserHobbyRepository.getUserHobby(1)).thenReturn(Future(List("Signing")))
      when(mockUserForms.userUpdateConstraintList).thenReturn(userForms.userUpdateConstraintList)
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing", "Dancing", "Travelling", "Swimming", "Sports")))
      val result = call(application.viewAssignment(), FakeRequest(GET, "/profile").withSession())
      status(result) mustBe 303
    }

    "not be able to show update profile" in {

      when(mockUserRepository.getUserById(1)).thenReturn(Future(newUser))
      when(mockUserHobbyRepository.getUserHobby(1)).thenReturn(Future(List()))
      when(mockUserForms.userUpdateConstraintList).thenReturn(userForms.userUpdateConstraintList)
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing", "Dancing", "Travelling", "Swimming", "Sports")))
      val result = call(application.viewAssignment(), FakeRequest(GET, "/profile").withSession("userId" -> "1"))
      status(result) mustBe 200
    }


    "not be able to show update profile due to session problem" in {

      when(mockUserRepository.getUserById(1)).thenReturn(Future(newUser))
      when(mockUserHobbyRepository.getUserHobby(1)).thenReturn(Future(List()))
      when(mockUserForms.userUpdateConstraintList).thenReturn(userForms.userUpdateConstraintList)
      when(mockHobbyRepository.getHobbies()).thenReturn(Future(List("Singing", "Dancing", "Travelling", "Swimming", "Sports")))
      val result = call(application.viewAssignment(), FakeRequest(GET, "/profile").withSession())
      status(result) mustBe 303
    }

  }
}