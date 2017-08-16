package models

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class AssignmentRepositoryTest extends PlaySpec with MockitoSugar {

  val assignmentDetails = AssignmentDetails(1,"Assign-1","Description Of Assignment-1")
  val assignmentRepository = new ModelsTest[AssignmentRepository]
  "Assignment Repository" should {

    "be able to add assignment" in {
      val testResult = assignmentRepository.result(assignmentRepository.repository.addAssignment(assignmentDetails))
      testResult mustBe true
    }

    "be able to get list of assignments" in {
      val testResult = assignmentRepository.result(assignmentRepository.repository.getAssignments)
      testResult mustBe List(assignmentDetails)
    }

    "be able to delete assignment" in {
      val testResult = assignmentRepository.result(assignmentRepository.repository.deleteAssignment(1))
      testResult mustBe true
    }

    "not be able to delete assignment due to incorrect userId" in {
      val id = 900
      val testResult = assignmentRepository.result(assignmentRepository.repository.deleteAssignment(id))
      testResult mustBe false
    }
  }

}
