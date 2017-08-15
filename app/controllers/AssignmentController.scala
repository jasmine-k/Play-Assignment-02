package controllers

import com.google.inject.Inject
import models._
import org.mindrot.jbcrypt.BCrypt
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
class AssignmentController @Inject()(val userRepository: UserRepository,
                                     val userForms: UserForms,
                                     assignmentRepository: AssignmentRepository,
                                     val messagesApi: MessagesApi
                                    ) extends Controller with I18nSupport {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */

  implicit val messages = messagesApi

  def addAssignment(assignmentId: Int): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>

      Future.successful(Redirect(routes.Application.viewUser()).flashing("error" -> "Something went wrong"))

  }

  def deleteAssignment(assignmentId: Int): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>

      Logger.info("assign id: " + assignmentId)
      val adminId = request.session.get("userId")
      adminId match {
        case Some(adminId) =>
          userRepository.isAdminById(adminId.toInt).flatMap {
            case true =>
              assignmentRepository.deleteAssignment(assignmentId).map {

                case true =>
                  Logger.info("Assignment deleted successfully")
                  Redirect(routes.Application.viewAssignment()).flashing("success" -> "Assignment deleted successfully")
                case false =>
                  Logger.info("Something went wrong")
                  Redirect(routes.Application.viewAssignment()).flashing("error" -> "Something went wrong")
              }
            case false => Future.successful(Redirect(routes.Application.index()).flashing("error" -> "You are not admin")) //-------session destroy

          }
        case None => Future.successful(Redirect(routes.Application.index()).flashing("error" -> "You need to login first"))
      }
  }

  def addAssignment(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      userForms.AssignmentConstraintList.bindFromRequest.fold(
        formWithErrors => {
          Future.successful(BadRequest(views.html.addAssignment("Error", formWithErrors)))
        },
        assignmentDetails => {
          Logger.info("Assignment Details: " + assignmentDetails)
          val adminId = request.session.get("userId")
          adminId match {
            case Some(adminId) =>
              userRepository.isAdminById(adminId.toInt).flatMap {
                case true =>
                  val newAssignment = AssignmentDetails(1,assignmentDetails.title, assignmentDetails.description)
                  assignmentRepository.addAssignment(newAssignment).map {

                    case true =>
                      Logger.info("Assignment added successfully")
                      Redirect(routes.Application.viewAssignment()).flashing("success" -> "Assignment added successfully") //-----redirect
                    case false =>
                      Logger.info("Something went wrong")
                      Redirect(routes.Application.viewAssignment()).flashing("error" -> "Something went wrong")//redirect
                  }
                case false => Future.successful(Redirect(routes.Application.index()).flashing("error" -> "You are not admin")) //-------session destroy

              }
            case None => Future.successful(Redirect(routes.Application.index()).flashing("error" -> "You need to login first"))
          }

        })

  }



}

