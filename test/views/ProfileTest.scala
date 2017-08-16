package views

import controllers.{Assignment, UserForms}
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

class ProfileTest extends PlaySpec with MockitoSugar {

  val allFormsObj = new UserForms
  val profileForm = allFormsObj.userUpdateConstraintList
  val hobbies = List("Singing", "Dancing", "Travelling", "Swimming", "Sports")

  "profile template" should {

    "render profile page" in {

      val mockMesssages = mock[Messages]
      val mockFlash = mock[Flash]
      when(mockFlash.get("error")) thenReturn None
      when(mockFlash.get("success")) thenReturn None
      val updateProfile = views.html.profile.render("Knoldus", profileForm,hobbies,true, mockMesssages, mockFlash)

      updateProfile.toString.contains("Update Profile") mustEqual true
    }
  }
}