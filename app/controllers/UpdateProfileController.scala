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
  * application's home page.*/
class UpdateProfileController @Inject()(val userRepository: UserRepository,
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

  def updateProfile(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      userForms.userUpdateConstraintList.bindFromRequest.fold(
        formWithErrors => {
          hobbyRepository.getHobbies.map(hobbies => BadRequest(views.html.profile("Error", formWithErrors, hobbies)))
        },
        userData => {
          Logger.info("Form submitted")
          val sessionId = request.session.get("userId")
          Logger.info(s"Get session in updateProfile $sessionId")
          sessionId match {
            case Some(sessionId) =>
              val newUpdatedUserData = UpdateUserDetails(userData.name, userData.mobileNumber,
                userData.gender, userData.age, userData.hobbies)
              Logger.info(s"Updated data is $UserData")
              userRepository.updateUserData(newUpdatedUserData, sessionId.toInt).flatMap {
                case true =>
                  userHobbyRepository.deleteUserHobby(sessionId.toInt).flatMap {
                    case true =>
                      val listOfHobbiesID: Future[List[List[Int]]] = hobbyRepository.getHobbiesId(userData.hobbies)
                      listOfHobbiesID.flatMap { hobbyId =>
                        userHobbyRepository.addUserHobby(sessionId.toInt, hobbyId).flatMap {
                          case true =>
                            userForms.userUpdateConstraintList.fill(UpdateUserDetails(
                              Name(userData.name.firstName, userData.name.middleName, userData.name.lastName), userData.mobileNumber,
                              userData.gender, userData.age, userData.hobbies))
                            hobbyRepository.getHobbies().map { hobbies =>
                              Redirect(routes.Application.updateProfile())
                                .flashing("success" -> "Profile updated successfully")
                            }
                          case false => Future.successful(Redirect(routes.Application.updateProfile())
                            .flashing("error" -> "Something went wrong!"))
                        }
                      }
                    case false => Future.successful(Redirect(routes.Application.updateProfile())
                      .flashing("error" -> "Something went wrong!"))
                  }
                case false =>
                  Future.successful(Redirect(routes.Application.updateProfile()).flashing("error" -> "Something went wrong!"))
              }

            case None =>
              Logger.info("Session does not exits")
              Future.successful(Redirect(routes.Application.index()).flashing("error" -> "You need to login first"))
          }
        })
  }

}

