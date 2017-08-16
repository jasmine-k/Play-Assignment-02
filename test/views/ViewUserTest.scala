package views

import controllers.{Name, User, UserForms}
import models.UserData
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

class ViewUserTest extends PlaySpec with MockitoSugar {

  val allFormsObj = new UserForms

  val mobile = 9999999999L
  val age = 18
  val newUserData = UserData(1,"jas", Option("kaur"), "kaur", mobile, "female", age, "jas@gmail.com", "jasmine",true,true)

  val profileForm = allFormsObj.userUpdateConstraintList
  val hobbies = List("Singing", "Dancing", "Travelling", "Swimming", "Sports")

  "profile template" should {

    "render profile page" in {

      val mockMesssages = mock[Messages]
      val mockFlash = mock[Flash]
      when(mockFlash.get("error")) thenReturn None
      when(mockFlash.get("success")) thenReturn None
      val viewProfile = views.html.viewUser.render("Knoldus",List(newUserData),mockMesssages, mockFlash)

      viewProfile.toString.contains("Enable") mustEqual true
    }
  }
}
