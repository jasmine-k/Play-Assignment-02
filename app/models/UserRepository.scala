package models

import com.google.inject.Inject
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.Controller
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class UserData(userId: Int, firstName: String, lastName: String, gender: String, dateOfBirth: String, email: String, password: String)

class UserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends UserRepositoryTable {

  import driver.api._

  def store(user: UserData) = {

    Logger.info("data: "+user)
    db.run(userQuery += user) map (_ > 0)
  }

  def emailValidation(email: String): Future[Option[String]] = {

    val emailDuplicateQuery = userQuery.filter(_.email === email).map(_.email).result.headOption
    db.run(emailDuplicateQuery)
  }
}

trait UserRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val userQuery: TableQuery[UserDataTable] = TableQuery[UserDataTable]

  class UserDataTable(tag: Tag) extends Table[UserData](tag, "userdata") { //tag->table name

    def userId = column[Int]("userId", O.PrimaryKey, O.AutoInc)

    def firstName = column[String]("firstname")

    def lastName = column[String]("lastname")

    def gender = column[String]("gender")

    def dateOfBirth = column[String]("dateofbirth")

    def email = column[String]("email")

    def password = column[String]("password")

    def * = (userId, firstName, lastName, gender, dateOfBirth, email, password) <> (UserData.tupled, UserData.unapply)
  }

}
