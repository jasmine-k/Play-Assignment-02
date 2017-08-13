package controllers

import com.google.inject.Inject
import models.{UserData, UserRepository}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
class AuthenticationController @Inject()(val userRepository: UserRepository,
                                         val userForms : UserForms,
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
  def loginValidation() = Action {
    implicit request: Request[AnyContent] =>
      userForms.UserLoginConstraintList.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(views.html.login("Error", formWithErrors))
        },
        userData => {
          Redirect(routes.Application.successLoginMessageDisplay())
        })

  }


  def signUpValidation() = Action.async {
    implicit request: Request[AnyContent] =>
      userForms.userRegistrationConstraintList.bindFromRequest.fold(
        formWithErrors => {
          Future.successful(BadRequest(views.html.signUp("Error", formWithErrors)))
        },
        userData => {
          val newUserData = UserData(1, userData.firstName, userData.lastName, userData.gender, userData.dateOfBirth, userData.email, userData.password)
          val emailValidationResult = userRepository.emailValidation(userData.email)
          emailValidationResult.flatMap {
            case Some(_) =>
              //Future.successful(Redirect(routes.Application.signUp()))
              Future.successful(Redirect(routes.Application.signUp()).flashing("error"->"Email already exists!"))
              //Future.successful(BadRequest(views.html.signUp("Error", userForms.userRegistrationConstraintList.fill(userData))))

            case None =>
              userRepository.store(newUserData).map {

                case true => Redirect(routes.Application.successMessageDisplay()).flashing("success"->"You are successfully registered")//.withSession("user" -> "rishabh")

                case false => Redirect(routes.Application.signUp()).flashing("error"->"Something went wrong!")//.withSession("user" -> "rishabh")
              }
          }
        })
  }

}
