package controllers

import akka.stream.Materializer
import models._
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

class AssignmentControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite {


  val mockUserRepository = mock[UserRepository]
  val mockUserForms = mock[UserForms]
  val mockMessagesApi = mock[MessagesApi]
  val mockAssignmentRepository = mock[AssignmentRepository]
  val userForms = new UserForms
  implicit lazy val materializer: Materializer = app.materializer

  val assignmentController = new AssignmentController(mockUserRepository, mockUserForms, mockAssignmentRepository,mockMessagesApi)
  val assignmentOneDetails = Assignment("Assignment1","Description1")
  val assignmentDetails = AssignmentDetails(1,"Assignment1","Description1")

  "Assignment Controller" should {

    "be able to delete assign to admin" in {

      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockAssignmentRepository.deleteAssignment(1)).thenReturn(Future(true))
      val result = call(assignmentController.deleteAssignment(1), FakeRequest(GET, "/deleteassignment").withSession("userId" -> "1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/assignment")

    }

    "not be able to delete assign to admin due to session problem" in {

      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockAssignmentRepository.deleteAssignment(1)).thenReturn(Future(true))
      val result = call(assignmentController.deleteAssignment(1), FakeRequest(GET, "/deleteassignment").withSession())

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/index")

    }

    "not be able to delete assign to admin" in {

      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockAssignmentRepository.deleteAssignment(1)).thenReturn(Future(false))
      val result = call(assignmentController.deleteAssignment(1), FakeRequest(GET, "/deleteassignment").withSession("userId" -> "1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/assignment")

    }

    "not be able to delete assign to admin due to session" in {

      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockAssignmentRepository.deleteAssignment(1)).thenReturn(Future(false))
      val result = call(assignmentController.deleteAssignment(1), FakeRequest(GET, "/deleteassignment").withSession())

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/index")

    }

    "not be able to delete assign because user is not admin" in {

      when(mockUserRepository.isAdminById(1)).thenReturn(Future(false))
      when(mockAssignmentRepository.deleteAssignment(1)).thenReturn(Future(true))
      val result = call(assignmentController.deleteAssignment(1), FakeRequest(GET, "/deleteassignment").withSession("userId" -> "1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/assignment")
    }

    "not be able to delete assign because session does not exits" in {

      when(mockUserRepository.isAdminById(1)).thenReturn(Future(false))
      when(mockAssignmentRepository.deleteAssignment(1)).thenReturn(Future(true))
      val result = call(assignmentController.deleteAssignment(1), FakeRequest(GET, "/deleteassignment").withSession())

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/index")
    }

    "be able to add assign" in {

      when(mockUserForms.assignmentConstraintList).thenReturn(userForms.assignmentConstraintList.fill(assignmentOneDetails))
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockAssignmentRepository.addAssignment(assignmentDetails)).thenReturn(Future(true))
      val result = call(assignmentController.addAssignment(), FakeRequest(POST, "/addassignment")
        .withFormUrlEncodedBody("title"->"Assignment1", "description"->"Description1").withSession("userId" -> "1"))


      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/assignment")

    }

    "not be able to add assign" in {

      when(mockUserForms.assignmentConstraintList).thenReturn(userForms.assignmentConstraintList.fill(assignmentOneDetails))
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockAssignmentRepository.addAssignment(assignmentDetails)).thenReturn(Future(false))
      val result = call(assignmentController.addAssignment(), FakeRequest(POST, "/addassignment")
        .withFormUrlEncodedBody("title"->"Assignment1", "description"->"Description1").withSession("userId" -> "1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/assignment")

    }

    "not be able to add assign because user is not admin" in {

      when(mockUserForms.assignmentConstraintList).thenReturn(userForms.assignmentConstraintList.fill(assignmentOneDetails))
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(false))
      when(mockAssignmentRepository.addAssignment(assignmentDetails)).thenReturn(Future(true))
      val result = call(assignmentController.addAssignment(), FakeRequest(POST, "/addassignment")
        .withFormUrlEncodedBody("title"->"Assignment1", "description"->"Description1").withSession("userId" -> "1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/assignment")

    }
  }
}
