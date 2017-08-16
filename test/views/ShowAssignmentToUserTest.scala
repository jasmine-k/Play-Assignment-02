package views

import controllers.UserForms
import models.AssignmentDetails
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

class ShowAssignmentToUserTest extends PlaySpec with MockitoSugar {

  val allFormsObj = new UserForms
  val assignmentForm = allFormsObj.assignmentConstraintList
  val assignment = List(AssignmentDetails(1,"title", "description"))

  "showAssignment template" should {

    "render showAssignment page for normal user" in {

      val mockMesssages = mock[Messages]
      val mockFlash = mock[Flash]
      when(mockFlash.get("error")) thenReturn None
      when(mockFlash.get("success")) thenReturn None
      val addAssignmentPage = views.html.showAssignmentToUser.render("Knoldus", assignment, mockMesssages, mockFlash)

      addAssignmentPage.toString.contains("Title") mustEqual true
    }
  }
}
