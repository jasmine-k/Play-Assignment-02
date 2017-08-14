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
class AuthenticationController @Inject()(val userRepository: UserRepository,
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

  def loginValidation(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      userForms.UserLoginConstraintList.bindFromRequest.fold(
        formWithErrors => {
          Future.successful(BadRequest(views.html.login("Error", formWithErrors)))
        },
        userData => {
          Logger.info("Checking if user exits!")
          userRepository.checkIfUserExists(userData.email, userData.password).flatMap {

            case true =>
              Logger.info("User exits!")
              userRepository.getUserId(userData.email).map {
                case id: Int => Redirect(routes.Application.updateProfile()).withSession("userId" -> s"$id")
                case _ => Ok(views.html.index())

              }
            //Redirect(routes.Application.successLoginMessageDisplay())//.withSession("email"-> s"$userData.email","password" -> s"$userData.password")
            case false =>
              Logger.info("User does not exists")
              Future.successful(Redirect(routes.Application.loginPage()))//.flashing("error","Incorrect Email or Password")))
          }

        })

  }


  def signUpValidation(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      userForms.userRegistrationConstraintList.bindFromRequest.fold(
        formWithErrors => {
          hobbyRepository.getHobbies.map(hobbies => BadRequest(views.html.signUp("Error", formWithErrors, hobbies)))
        },
        userData => {
          Logger.info("Form submitted")
          val hashPassword = BCrypt.hashpw(userData.password, BCrypt.gensalt())
          Logger.info("password len = "+hashPassword.length)
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
                      Redirect(routes.Application.successMessageDisplay()).flashing("success" -> "You are successfully registered")
                    //.withSession("user" -> "rishabh")
                    case false =>
                      Redirect(routes.Application.signUp()).flashing("error" -> "Something went wrong!") //.withSession("user" -> "rishabh")
                  }
                  )

                case false =>
                  Future.successful(Redirect(routes.Application.signUp()).flashing("error" -> "Email already exists!"))

              }
          }
        })
  }

  def updateProfile(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      userForms.userUpdateConstraintList.bindFromRequest.fold(
        formWithErrors => {
          hobbyRepository.getHobbies.map(hobbies => BadRequest(views.html.profile("Error", formWithErrors, hobbies)))
        },
        userData => {
          Logger.info("Form submitted")
          val sessionId = request.session.get("userId")
          Logger.info(s"Get session in updateProfile(Authentication) $sessionId")
          sessionId match {
            case Some(sessionId) =>
              val newUpdatedUserData = UpdateUserDetails(userData.name, userData.mobileNumber,
                userData.gender, userData.age, userData.email, userData.hobbies)
              Logger.info(s"Updated data is $UserData")
              val ifEmailExists: Future[Boolean] = userRepository.checkIfEmailExists(userData.email, sessionId.toInt)
              ifEmailExists.flatMap {
                case true => Future.successful(Redirect(routes.Application.updateProfile())
                  .flashing("error" -> "Email already exists!").withSession("userId" -> s"$sessionId"))
                case false =>
                  userRepository.updateUserData(newUpdatedUserData, sessionId.toInt).flatMap {
                    case true =>
                      userHobbyRepository.deleteUserHobby(sessionId.toInt).flatMap {
                        case true =>
                          val listOfHobbiesID: Future[List[List[Int]]] = hobbyRepository.getHobbiesId(userData.hobbies)
                          listOfHobbiesID.flatMap { hobbyId =>
                            userHobbyRepository.addUserHobby(sessionId.toInt, hobbyId).flatMap {
                              case true =>
                                val updatedValues = userForms.userUpdateConstraintList.fill(UpdateUserDetails(
                                  Name(userData.name.firstName, userData.name.middleName, userData.name.lastName), userData.mobileNumber,
                                  userData.gender, userData.age, userData.email, userData.hobbies))
                                hobbyRepository.getHobbies().map {
                                  hobbies =>Ok(views.html.profile("Edit Profile", updatedValues, hobbies)).flashing("success" -> "Profile updated successfully")
                                }
                              case false => Future.successful(Redirect(routes.Application.updateProfile())
                                .flashing("error" -> "Something went wrong!")) //.withSession("user" -> "rishabh")
                            }
                          }
                        case false => Future.successful(Redirect(routes.Application.updateProfile())
                          .flashing("error" -> "Something went wrong!")) //.withSession("user" -> "rishabh"))
                      }
                    case false =>
                      Future.successful(Redirect(routes.Application.updateProfile()).flashing("error" -> "Something went wrong!"))
                  }
              }
            case None =>
              Logger.info("Session does not exits")
              Future.successful(Ok(views.html.index()))
          }
        })
  }

}
