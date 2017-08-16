package views

import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

class IndexTest extends PlaySpec with MockitoSugar{

  "Index" should {
    "display welcome page" in {
      val messages = mock[Messages]
      val flash = mock[Flash]
      when(flash.get("error")) thenReturn None
      when(flash.get("success")) thenReturn None
      val html = views.html.index.render(flash)
      assert(html.toString.contains("Welcome to Knoldus!"))
    }
  }

}