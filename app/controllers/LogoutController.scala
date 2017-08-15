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
class LogoutController @Inject()(val userRepository: UserRepository,
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

  def logout: Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>

      val userId = request.session.get("userId")
      userId match {
        case Some(_) =>
          Logger.info("Successfully logged out")
          Future.successful(Redirect(routes.Application.index()).withNewSession.flashing("success" -> "You have logged out!"))
        case None =>
          Logger.info("Error occurred while logout")
          Future.successful(Redirect(routes.Application.index()).flashing("error" -> "Something went wrong!"))

      }
  }

}
