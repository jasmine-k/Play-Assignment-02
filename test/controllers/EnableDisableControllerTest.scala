package controllers

import akka.stream.Materializer
import models.{HobbyRepository, UserData, UserHobbyRepository, UserRepository}
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

class EnableDisableControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite {


  val mockUserRepository = mock[UserRepository]
  val mockMessagesApi = mock[MessagesApi]
  implicit lazy val materializer: Materializer = app.materializer
  val enableDisableController = new EnableDisableController(mockUserRepository,mockMessagesApi)

  "signUpController" should {

    "be able to enable the user" in {
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockUserRepository.checkIfEmailExists("jas@gmail.com",2)).thenReturn(Future(true))
      when(mockUserRepository.updateIsActive("jas@gmail.com",true)).thenReturn(Future(true))
      val result = call(enableDisableController.enableDisableUser("jas@gmail.com",2,true), FakeRequest(GET, "/isactive")
        .withSession("userId"->"1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/user")

    }

    "not be able to enable the user due to session" in {
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockUserRepository.checkIfEmailExists("jas@gmail.com",2)).thenReturn(Future(true))
      when(mockUserRepository.updateIsActive("jas@gmail.com",true)).thenReturn(Future(true))
      val result = call(enableDisableController.enableDisableUser("jas@gmail.com",2,true), FakeRequest(GET, "/isactive")
        .withSession())

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/index")

    }

    "not be able to enable the user" in {
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockUserRepository.checkIfEmailExists("jas@gmail.com",2)).thenReturn(Future(true))
      when(mockUserRepository.updateIsActive("jas@gmail.com",true)).thenReturn(Future(false))
      val result = call(enableDisableController.enableDisableUser("jas@gmail.com",2,true), FakeRequest(GET, "/isactive")
        .withSession("userId"->"1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/user")

    }


    "not be able to enable the user because session does not exits" in {
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockUserRepository.checkIfEmailExists("jas@gmail.com",2)).thenReturn(Future(true))
      when(mockUserRepository.updateIsActive("jas@gmail.com",true)).thenReturn(Future(false))
      val result = call(enableDisableController.enableDisableUser("jas@gmail.com",2,true), FakeRequest(GET, "/isactive")
        .withSession())

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/index")

    }

    "be able to disable the user" in {
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockUserRepository.checkIfEmailExists("jas@gmail.com",2)).thenReturn(Future(true))
      when(mockUserRepository.updateIsActive("jas@gmail.com",false)).thenReturn(Future(true))
      val result = call(enableDisableController.enableDisableUser("jas@gmail.com",2,false), FakeRequest(GET, "/isactive")
        .withSession("userId"->"1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/user")

    }

    "not be able to disable the user" in {
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockUserRepository.checkIfEmailExists("jas@gmail.com",2)).thenReturn(Future(true))
      when(mockUserRepository.updateIsActive("jas@gmail.com",false)).thenReturn(Future(false))
      val result = call(enableDisableController.enableDisableUser("jas@gmail.com",2,false), FakeRequest(GET, "/isactive")
        .withSession("userId"->"1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/user")

    }

    "not be able to enable the user because user is not admin" in {
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(false))
      when(mockUserRepository.checkIfEmailExists("jas@gmail.com",2)).thenReturn(Future(true))
      when(mockUserRepository.updateIsActive("jas@gmail.com",true)).thenReturn(Future(true))
      val result = call(enableDisableController.enableDisableUser("jas@gmail.com",2,true), FakeRequest(GET, "/isactive")
        .withSession("userId"->"1"))
      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/user")

    }

    "not be able to enable the user becuase email does not exists" in {
      when(mockUserRepository.isAdminById(1)).thenReturn(Future(true))
      when(mockUserRepository.checkIfEmailExists("jas@gmail.com",2)).thenReturn(Future(false))
      when(mockUserRepository.updateIsActive("jas@gmail.com",true)).thenReturn(Future(true))
      val result = call(enableDisableController.enableDisableUser("jas@gmail.com",2,true), FakeRequest(GET, "/isactive")
        .withSession("userId"->"1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/user")

    }

  }

}
