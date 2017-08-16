package views

import controllers.{Assignment, UserForms}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash
import org.mockito.Mockito.when
import views.html.addAssignment

class AddAssignmentTest extends PlaySpec with MockitoSugar {

  val allFormsObj = new UserForms
  val addAssignmentForm = allFormsObj.assignmentConstraintList
  val assignment = List(Assignment("title", "description"))

  "addAssignment template" should {

    "render addAssignment page" in {

      val mockMesssages = mock[Messages]
      val mockFlash = mock[Flash]
      when(mockFlash.get("error")) thenReturn None
      when(mockFlash.get("success")) thenReturn None
      val addAssignmentPage = views.html.addAssignment.render("Knoldus", addAssignmentForm, mockMesssages, mockFlash)

      addAssignmentPage.toString.contains("Title") mustEqual true
    }
  }
}