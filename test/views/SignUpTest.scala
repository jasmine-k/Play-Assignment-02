package views

import controllers.UserForms
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

class SignUpTest extends PlaySpec with MockitoSugar {

  val allFormsObj = new UserForms
  val signUpForm = allFormsObj.userRegistrationConstraintList
  val hobbies = List("Singing", "Dancing", "Travelling", "Swimming", "Sports")

  "signUp template" should {

    "signUp page" in {

      val mockMesssages = mock[Messages]
      val mockFlash = mock[Flash]
      when(mockFlash.get("error")) thenReturn None
      when(mockFlash.get("success")) thenReturn None
      val updateProfile = views.html.signUp.render("Knoldus", signUpForm ,hobbies, mockMesssages, mockFlash)

      updateProfile.toString.contains("Submit") mustEqual true
    }
  }
}
