package models

import com.google.inject.Inject
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.Controller
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Hobby(id: Int, name: String)

class HobbyRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HobbyRepositoryTable {

  import driver.api._

  def getHobbies(): Future[List[String]] = {

    Logger.info("Getting hobby list of hobbies from database")
    db.run(hobbyQuery.map(_.name).to[List].result)
  }

  def getHobbiesId(listOfHobby: List[String]): Future[List[List[Int]]] = {

    Logger.info("Getting Ids of given hobbies")
    Future.sequence(listOfHobby.map(hobby => db.run(hobbyQuery.filter(_.name === hobby).map(_.id).to[List].result)))

  }

}

trait HobbyRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val hobbyQuery: TableQuery[HobbyTable] = TableQuery[HobbyTable]

  class HobbyTable(tag: Tag) extends Table[Hobby](tag, "hobby") { //tag->table name

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name: Rep[String] = column[String]("name")

    def * : ProvenShape[Hobby] = (id, name) <> (Hobby.tupled, Hobby.unapply)
  }

}
