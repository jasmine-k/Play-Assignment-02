package views

import controllers.UserForms
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

class ForgotPasswordTest extends PlaySpec with MockitoSugar {

  val allFormsObj = new UserForms
  val passwordForm = allFormsObj.updatePasswordConstraintList

  "forgot password template" should {

    "render forgot password page" in {

      val mockMesssages = mock[Messages]
      val mockFlash = mock[Flash]
      when(mockFlash.get("error")) thenReturn None
      when(mockFlash.get("success")) thenReturn None
      val updatePassword = views.html.forgotPassword.render("Knoldus", passwordForm, mockMesssages, mockFlash)

      updatePassword.toString.contains("Update Password") mustEqual true
    }
  }
}
