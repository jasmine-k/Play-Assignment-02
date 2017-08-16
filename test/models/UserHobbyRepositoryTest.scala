package models

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class UserHobbyRepositoryTest extends PlaySpec with MockitoSugar {

  val listOfUserHobbies = List("Singing","Dancing","Travelling","Swimming","Sports")
  val id1 =1
  val id2 =2
  val id3 = 3
  val id4 = 4
  val id5 = 5
  val listOfUserHobbiesId = List(List(id1),List(id2),List(id3),List(id4),List(id5))
  val userHobbyRepository = new ModelsTest[UserHobbyRepository]
  "User Hobby Repository" should {

    "be able to add list of hobbies of the user" in {
      val testResult = userHobbyRepository.result(userHobbyRepository.repository.addUserHobby(1,listOfUserHobbiesId))
      testResult mustBe true
    }

    "be able to get list of hobbies of the user" in {
      val testResult = userHobbyRepository.result(userHobbyRepository.repository.getUserHobby(1))
      testResult mustBe listOfUserHobbies
    }

    "be able to delete list of hobbies of the user" in {
      val testResult = userHobbyRepository.result(userHobbyRepository.repository.deleteUserHobby(1))
      testResult mustBe true
    }

    "not be able to delete list of hobbies of the user due to incorrect userId" in {
      val testResult = userHobbyRepository.result(userHobbyRepository.repository.deleteUserHobby(1))
      testResult mustBe false
    }

  }

}
