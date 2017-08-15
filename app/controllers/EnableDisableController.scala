package controllers

import com.google.inject.Inject
import models.{HobbyRepository, UserData, UserHobbyRepository, UserRepository}
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
class EnableDisableController @Inject()(val userRepository: UserRepository,
                                        val userForms: UserForms,
                                        hobbyRepository: HobbyRepository,
                                        userHobbyRepository: UserHobbyRepository,
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

  def enableDisableUser(email: String,userId:Int, isActive: Boolean): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>

      Logger.info("email: " + email + "isActive " + isActive)
      val adminId = request.session.get("userId")
      adminId match {
        case Some(adminId) =>
          userRepository.isAdminById(adminId.toInt).flatMap {
            case true =>
              userRepository.checkIfEmailExists(email,userId).flatMap {
                case true =>
                  Logger.info("Email exists!")
                  userRepository.updateIsActive(email, isActive).map {
                    case true=>
                      Logger.info("Update true")
                     Redirect(routes.Application.viewUser()).flashing("success" -> "Enable-Disable updated successfully")
                    case false=>
                      Logger.info("Update false")

                      Redirect(routes.Application.viewUser()).flashing("error"-> "Something went wrong")
                  }
                case false =>
                  Logger.info("Email does not  exists!")
                  Future.successful(Redirect(routes.Application.viewUser()).flashing("error"->"Something went wrong"))

              }
            case false => Future.successful(Redirect(routes.Application.index()).flashing("error"->"You are not admin")) //-------session destroy
          }
        case None => Future.successful(Redirect(routes.Application.index()).flashing("error"-> "You need to login first"))
      }
  }

}
