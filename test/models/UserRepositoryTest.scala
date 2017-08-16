package models

import controllers.{Name, UpdateUserDetails}
import org.mindrot.jbcrypt.BCrypt
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class UserRepositoryTest extends PlaySpec with MockitoSugar {

  val userRepository = new ModelsTest[UserRepository]
  val password = "jasmine"
  val hashPassword = BCrypt.hashpw(password, BCrypt.gensalt())
  val newUserData = UserData(1, "jas", Option("kaur"), "kaur",
    9898989898L, "female", 18, "jas@gmail.com", hashPassword, false, true)
  val newAdminData = UserData(2, "Sim", Option("kaur"), "kaur",
    9898989898L, "female", 18, "sim@gmail.com", hashPassword, true, true)
  val inactiveUserData = UserData(3, "ruby", Option("kaur"), "kaur",
    9898989898L, "female", 18, "ruby@gmail.com", hashPassword, false, false)
  val updatedUserData = UpdateUserDetails(Name("jas", Option("kaur"), "kaur"),
    9898989898L, "female", 21, List("Swimming"))

  "User Repository" should {

    "be able to sign up new user" in {
      val testResult = userRepository.result(userRepository.repository.addNewUser(newUserData))
      testResult mustBe true
    }

    "be able to validate email" in {
      val testResult = userRepository.result(userRepository.repository.emailValidation("jas@gmail.com"))
      testResult mustBe Some("jas@gmail.com")
    }

    "not be able to validate email" in {
      val testResult = userRepository.result(userRepository.repository.emailValidation("jasmine@gmail.com"))
      testResult mustBe None
    }

    "be able to get user by email" in {
      val testResult = userRepository.result(userRepository.repository.getUserByEmail("jas@gmail.com"))
      testResult mustBe Some(newUserData)
    }

    "not be able to get user by email" in {
      val testResult = userRepository.result(userRepository.repository.getUserByEmail("jasmine@gmail.com"))
      testResult mustBe None
    }

    "be able to check if user exists with correct details" in {

      val testResult = userRepository.result(userRepository.repository.checkIfUserExists("jas@gmail.com",password))
      testResult mustBe true
    }

    "be able to check if user exists with incorrect email" in {
      val testResult = userRepository.result(userRepository.repository.checkIfUserExists("jasmine@gmail.com", "jasmine"))
      testResult mustBe false
    }

    "be able to check if user exists with incorrect password" in {
      val testResult = userRepository.result(userRepository.repository.checkIfUserExists("jasmine@gmail.com", "jasminee"))
      testResult mustBe false
    }

    "be able to get user by id" in {
      val testResult = userRepository.result(userRepository.repository.getUserById(1))
      testResult mustBe newUserData
    }

    "be able to check if email exists with correct details" in {
      val testResult = userRepository.result(userRepository.repository.checkIfEmailExists("jas@gmail.com", 1))
      testResult mustBe true
    }

    "be able to check if email exists with incorrect email" in {
      val id = 900
      val testResult = userRepository.result(userRepository.repository.checkIfEmailExists("jasmine@gmail.com", 1))
      testResult mustBe false
    }

    "be able to check if email exists with incorrect id" in {
      val id = 900
      val testResult = userRepository.result(userRepository.repository.checkIfEmailExists("jas@gmail.com", id))
      testResult mustBe false
    }

    "be able to check if user is admin when user is actually admin" in {

      userRepository.result(userRepository.repository.addNewUser(newAdminData))
      val testResult = userRepository.result(userRepository.repository.isAdmin("sim@gmail.com"))
      testResult mustBe true
    }


    "be able to check if user is admin when user is not actually admin" in {
      val testResult = userRepository.result(userRepository.repository.isAdmin("jas@gmail.com"))
      testResult mustBe false
    }


    "be able to check if user is admin with incorrect details" in {
      val testResult = userRepository.result(userRepository.repository.isAdmin("jasmine@gmail.com"))
      testResult mustBe false
    }

    "be able to check if user is active when user is actually active" in {
      val testResult = userRepository.result(userRepository.repository.isActive("jas@gmail.com"))
      testResult mustBe true
    }

    "be able to check if user is admin when user is not actually active" in {

      val testResult = userRepository.result(userRepository.repository.isActive("jasmine@gmail.com"))
      testResult mustBe false
    }

    "be able to check if user is active with incorrect details" in {
      userRepository.result(userRepository.repository.addNewUser(inactiveUserData))
      val testResult = userRepository.result(userRepository.repository.isActive("jasmine@gmail.com"))
      testResult mustBe false
    }

    "be able to get normal users list" in {

      val testResult = userRepository.result(userRepository.repository.getNormalUser())
      testResult mustBe List(newUserData, inactiveUserData)
    }

    "be able to active the user" in {

      val testResult = userRepository.result(userRepository.repository.updateIsActive("ruby@gmail.com", true))
      testResult mustBe true
    }

    "be able to inactive the user" in {

      val testResult = userRepository.result(userRepository.repository.updateIsActive("ruby@gmail.com", false))
      testResult mustBe true
    }

    "not be able to active/inactive the user due to incorrect email" in {

      val testResult = userRepository.result(userRepository.repository.updateIsActive("jasmine@gmail.com", false))
      testResult mustBe false
    }

    "be able to update user data" in {

      val testResult = userRepository.result(userRepository.repository.updateUserData(updatedUserData, 1))
      testResult mustBe true
    }

    "not be able to update user data" in {
      val id = 900
      val testResult = userRepository.result(userRepository.repository.updateUserData(updatedUserData, id))
      testResult mustBe false
    }

    "be able to update user password" in {
      val testResult = userRepository.result(userRepository.repository.updateUserPassword("jas@gmail.com", "jasmine123"))
      testResult mustBe true
    }

    "not be able to update user password" in {
      val testResult = userRepository.result(userRepository.repository.updateUserPassword("jasmine@gmail.com", "jasmine123"))
      testResult mustBe false
    }

  }

}
