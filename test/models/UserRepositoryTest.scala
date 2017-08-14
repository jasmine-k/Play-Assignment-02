package models

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class UserRepositoryTest extends PlaySpec with MockitoSugar {

  val userRepository = new ModelsTest[UserRepository]
  val newUserData = UserData(1,"jas", Option("kaur"), "kaur",
    9898989898L, "female", 18, "jas@gmail.com", "jasmine", false, true)

  "authenticationController" should {
    "be able to sign up new user" in {
      val storeResult = userRepository.result(userRepository.repository.addNewUser(newUserData))
      storeResult mustBe(true)
    }
  }

}
