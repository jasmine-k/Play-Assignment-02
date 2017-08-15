package models

import com.google.inject.Inject
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.QueryBase
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

case class UserHobby(id: Int, userId: Int, hobbyId: Int)

class UserHobbyRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HobbyRepositoryTable with UserHobbyRepositoryTable{

  import driver.api._

  def addUserHobby(userId: Int, listOfHobby: List[List[Int]]) :Future[Boolean]= {
    Logger.info("Adding list of hobbies into database")
    val listOfValidHobbyId = listOfHobby.filter(_ != Nil)
    val listOfResult: List[Future[Boolean]] = listOfValidHobbyId.map (
      hobbyId => db.run(userHobbyQuery += UserHobby(1, userId, hobbyId.head)).map(_ > 0))

    Future.sequence(listOfResult).map(
      result =>
        if (result.contains(false)) {
          false
        }
        else{
          true
        })

  }

  def getUserHobby(userId: Int): Future[List[String]] = {

    Logger.info("Extracting user hobby list from database")
    val userHobbyJoin : QueryBase[Seq[(Int, String)]] = for {

      (user, hobby) <- userHobbyQuery join hobbyQuery on(_.hobbyId === _.id)
    } yield (user.userId, hobby.name)
    val userHobbyJoinResult: Future[Seq[(Int, String)]] = db.run(userHobbyJoin.result)
    userHobbyJoinResult.map(hobby => hobby.filter(_._1 == userId).map(_._2).toList)

  }

  def deleteUserHobby(userId: Int):Future[Boolean]={
    Logger.info("Deleting user hobbies from database")
    db.run(userHobbyQuery.filter(_.userId === userId).delete).map(_>0)
  }


}

trait UserHobbyRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val userHobbyQuery = TableQuery[UserHobbyTable]

  class UserHobbyTable(tag: Tag) extends Table[UserHobby](tag, "userhobby") {

    def id = column[Int]("id", O.AutoInc, O.PrimaryKey)

    def userId = column[Int]("userid")

    def hobbyId = column[Int]("hobbyid")

    def * = (id, userId, hobbyId) <> (UserHobby.tupled, UserHobby.unapply)
  }

}