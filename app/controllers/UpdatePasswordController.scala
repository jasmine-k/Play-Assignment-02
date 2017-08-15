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
class UpdatePasswordController @Inject()(val userRepository: UserRepository,
                                         val userForms: UserForms,
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

  def updatePassword(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      userForms.UpdatePasswordConstraintList.bindFromRequest.fold(
        formWithErrors => {
          Future.successful(BadRequest(views.html.forgotPassword("Error", formWithErrors)))
        },
        userData => {
          Logger.info("Checking if user exits!")
          userRepository.getUserId(userData.email).flatMap {

            case id: Int if (id > 0) => {
              Logger.info("User exits!")
              /* userRepository.getUserId(userData.email).flatMap {
                 case id: Int =>{*/
              Logger.info("Updating Password " + userData.password)
              val hashPassword = BCrypt.hashpw(userData.password, BCrypt.gensalt())
              userRepository.updateUserPassword(userData.email, hashPassword).map {
                case true =>
                  Logger.info("Update password pass")

                  Redirect(routes.Application.loginPage()).flashing("success" -> "Password updated successfully")
                case false =>
                  Logger.info("Update password fail")
                  Ok(views.html.index())
              }
            }
            //Redirect(routes.Application.successLoginMessageDisplay())//.withSession("email"-> s"$userData.email","password" -> s"$userData.password")
            case id: Int if (id <= 0) =>
              Logger.info("User does not exists")
              Future.successful(Redirect(routes.Application.loginPage())) //.flashing("error","Incorrect Email or Password")))
          }

        })
  }

}
