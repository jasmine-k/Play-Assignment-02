package models

import com.google.inject.Inject
import controllers.UpdateUserDetails
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import org.mindrot.jbcrypt.BCrypt
import slick.lifted.ProvenShape

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class UserData(userId: Int, firstName: String, middleName: Option[String], lastName: String, mobileNumber: Long,
                    gender: String, age: Int, email: String, password: String, isAdmin: Boolean, isActive: Boolean)

class UserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends UserRepositoryTable {

  import driver.api._

  def addNewUser(user: UserData): Future[Boolean] = {

    Logger.info("data: " + user)
    db.run(userQuery += user) map (_ > 0)
  }

  def emailValidation(email: String): Future[Option[String]] = {

    val emailDuplicateQuery = userQuery.filter(_.email === email).map(_.email).result.headOption
    db.run(emailDuplicateQuery)

  }

  def getUserByEmail(email: String): Future[Option[UserData]] = {

    val emailDuplicateQuery = userQuery.filter(_.email === email).result.headOption
    db.run(emailDuplicateQuery)

  }

  def getUserId(email: String): Future[Int] = {
    val extractIdQueryResult = db.run(userQuery.filter(_.email === email).to[List].result)
    extractIdQueryResult.map { user =>
      if (user.isEmpty) {
        -1
      }
      else {
        user.head.userId
      }
    }

  }

  def checkIfUserExists(email: String, password: String): Future[Boolean] = {

    val userDetails = db.run(userQuery.filter(_.email === email).to[List].result)
    userDetails.map { user =>
      if (user.isEmpty) {
        false
      }
      else if (!BCrypt.checkpw(password, user.head.password)) {
        false
      }
      else {
        true
      }
    }
  }

  def getUserById(userId: Int): Future[UserData] = {
    Logger.info("Getting user details from database")
    val userDetailQuery = userQuery.filter(_.userId === userId).to[List].result.head
    db.run(userDetailQuery)
  }

  def checkIfEmailExists(email: String, id: Int): Future[Boolean] = {
    Logger.info("checkUpdatedEmail")
    val checkUpdatedEmail = db.run(userQuery.filter(_.email === email).to[List].result)
    checkUpdatedEmail.map { user =>
      if (user.isEmpty) {
        false
      }
      else if (user.head.userId == id) {
        true
      }
      else {
        false
      }
    }
  }

  def updateUserData(updatedUserData: UpdateUserDetails, userId: Int): Future[Boolean] = {
    Logger.info("Updating user details in database")
    db.run(userQuery.filter(_.userId === userId).map(user => (user.firstName, user.middleName, user.lastName,
      user.mobileNumber, user.gender, user.age)).update(updatedUserData.name.firstName,
      updatedUserData.name.middleName, updatedUserData.name.lastName, updatedUserData.mobileNumber,
      updatedUserData.gender, updatedUserData.age)).map(_ > 0)
  }

  def updateUserPassword(email: String, password: String): Future[Boolean] = {
    Logger.info("Updating password in database")
    db.run(userQuery.filter(_.email === email).map(user => (user.password)).update(password)).map(_ > 0)
  }

  def isAdmin(email: String): Future[Boolean] = {
    Logger.info("Checking if is admin from database")
    val resultIsAdmin = db.run(userQuery.filter(_.email === email).map(_.isAdmin).result.headOption)
    resultIsAdmin.map {
      case Some(isAdmin) =>
        if (isAdmin) {
          true
        }
        else {
          false
        }

      case None => false
    }
  }

  def isAdminById(userId: Int): Future[Boolean] = {
    Logger.info("Checking if is admin from database")
    val resultIsAdmin = db.run(userQuery.filter(_.userId === userId).map(_.isAdmin).result.headOption)
    resultIsAdmin.map {
      case Some(isAdmin) =>
        if (isAdmin) {
          true
        }
        else {
          false
        }
      case None => false
    }

  }

  def isActive(email: String): Future[Boolean] = {
    Logger.info("Checking if is active from database")
    val resultIsActive = db.run(userQuery.filter(_.email === email).map(_.isActive).result.headOption)
    resultIsActive.map {
      case Some(isActive) =>
        if (isActive) {
          true
        }
        else {
          false
        }
      case None => false
    }

  }

  def getNormalUser(): Future[List[UserData]] = {

    Logger.info("Getting normal user")
    db.run(userQuery.filter(_.isAdmin === false).to[List].result)
  }

  def updateIsActive(email: String, isActive: Boolean): Future[Boolean] = {
    Logger.info("Updating isActive field")
    db.run(userQuery.filter(_.email === email).map(user => user.isActive).update(isActive)).map(_ > 0)
  }

}

trait UserRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val userQuery: TableQuery[UserDataTable] = TableQuery[UserDataTable]

  class UserDataTable(tag: Tag) extends Table[UserData](tag, "userdata") { //tag->table name

    def userId: Rep[Int] = column[Int]("userid", O.PrimaryKey, O.AutoInc)

    def firstName: Rep[String] = column[String]("firstname")

    def middleName: Rep[Option[String]] = column[Option[String]]("middlename")

    def lastName: Rep[String] = column[String]("lastname")

    def mobileNumber: Rep[Long] = column[Long]("mobilenumber")

    def gender: Rep[String] = column[String]("gender")

    def age: Rep[Int] = column[Int]("age")

    def email: Rep[String] = column[String]("email")

    def password: Rep[String] = column[String]("password")

    def isAdmin: Rep[Boolean] = column[Boolean]("isadmin")

    def isActive: Rep[Boolean] = column[Boolean]("isactive")

    def * : ProvenShape[UserData] = (userId, firstName, middleName, lastName,
      mobileNumber, gender, age, email, password, isAdmin, isActive) <> (UserData.tupled, UserData.unapply)
  }

}
