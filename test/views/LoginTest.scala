package views

import controllers.UserForms
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

class LoginTest extends PlaySpec with MockitoSugar {

  val allFormsObj = new UserForms
  val loginForm = allFormsObj.userLoginConstraintList

  "login template" should {

    "render login page" in {

      val mockMesssages = mock[Messages]
      val mockFlash = mock[Flash]
      when(mockFlash.get("error")) thenReturn None
      when(mockFlash.get("success")) thenReturn None
      val updateProfile = views.html.login.render("Knoldus", loginForm , mockMesssages, mockFlash)

      updateProfile.toString.contains("Login") mustEqual true
    }
  }
}
