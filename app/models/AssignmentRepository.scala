package models

import com.google.inject.Inject
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.Controller
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class AssignmentDetails(id: Int, title: String, description: String)

class AssignmentRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends AssignmentRepositoryTable {

  import driver.api._

  def addAssignment(assignmentDetails: AssignmentDetails): Future[Boolean] = {
    Logger.info("Adding assignment in database")
    db.run(assignmentQuery += assignmentDetails).map(_ > 0)

  }

  def deleteAssignment(id: Int): Future[Boolean] = {
    Logger.info("Deleting assignment in database")
    db.run(assignmentQuery.filter(_.id === id).delete).map(_ > 0)

  }

  def getAssignments: Future[List[AssignmentDetails]] = {
    Logger.info("Getting assignments from database")
    db.run(assignmentQuery.to[List].result)

  }

}

trait AssignmentRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val assignmentQuery: TableQuery[AssignmentTable] = TableQuery[AssignmentTable]

  class AssignmentTable(tag: Tag) extends Table[AssignmentDetails](tag, "assignment") { //tag->table name

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def title: Rep[String] = column[String]("title")

    def description: Rep[String] = column[String]("description")

    def * : ProvenShape[AssignmentDetails] = (id, title, description) <> (AssignmentDetails.tupled, AssignmentDetails.unapply)
  }

}
