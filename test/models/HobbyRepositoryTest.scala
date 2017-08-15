package models

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class HobbyRepositoryTest extends PlaySpec with MockitoSugar {

  val hobbyRepository = new ModelsTest[HobbyRepository]
  val listOfHobbies = List("Singing","Dancing","Travelling","Swimming","Sports")
  val id1 =1
  val id2 =2
  val id3 = 3
  val id4 = 4
  val id5 = 5
  val listOfHobbiesId = List(List(id1),List(id2),List(id3),List(id4),List(id5))
  "Hobby Repository" should {

    "be able to get list of hobbies" in {
      val testResult = hobbyRepository.result(hobbyRepository.repository.getHobbies())
      testResult mustBe (listOfHobbies)
    }

    "be able to get list of IDs of hobbies" in {
      val testResult = hobbyRepository.result(hobbyRepository.repository.getHobbiesId(listOfHobbies))
      testResult mustBe (listOfHobbiesId)
    }
  }

}
