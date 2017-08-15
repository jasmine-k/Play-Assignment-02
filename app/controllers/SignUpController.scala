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
class SignUpController @Inject()(val userRepository: UserRepository,
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

  def signUpValidation(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      userForms.userRegistrationConstraintList.bindFromRequest.fold(
        formWithErrors => {
          hobbyRepository.getHobbies.map(hobbies => BadRequest(views.html.signUp("Error", formWithErrors, hobbies)))
        },
        userData => {
          Logger.info("Form submitted")
          val hashPassword = BCrypt.hashpw(userData.password, BCrypt.gensalt())
          val newUserData = UserData(1, userData.name.firstName, userData.name.middleName,
            userData.name.lastName, userData.mobileNumber, userData.gender, userData.age, userData.email, hashPassword, false, true)
          val emailValidationResult = userRepository.emailValidation(userData.email)
          emailValidationResult.flatMap {
            case Some(_) =>
              Future.successful(Redirect(routes.Application.signUp()).flashing("error" -> "Email already exists!"))

            case None =>
              userRepository.addNewUser(newUserData).flatMap {

                case true =>
                  val listOfHobbiesID: Future[List[List[Int]]] = hobbyRepository.getHobbiesId(userData.hobbies)
                  listOfHobbiesID.flatMap(hobbyId => userRepository.getUserId(userData.email).flatMap(userId =>
                    userHobbyRepository.addUserHobby(userId, hobbyId)).map {
                    case true =>
                      Redirect(routes.Application.index()).flashing("success" -> "You are successfully registered")
                    case false =>
                      Redirect(routes.Application.signUp()).flashing("error" -> "Something went wrong with hobbies addition!")
                  })

                case false =>
                  Future.successful(Redirect(routes.Application.signUp()).flashing("error" -> "Email already exists!"))

              }
          }
        })
  }

}
