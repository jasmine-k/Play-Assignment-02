package controllers

import com.google.inject.Inject
import models._
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
class Application @Inject()(val messagesApi: MessagesApi, userForms: UserForms, userRepository: UserRepository,
                            hobbyRepository: HobbyRepository, userHobbyRepository: UserHobbyRepository,
                            assignmentRepository: AssignmentRepository) extends Controller with I18nSupport {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  implicit val messages: MessagesApi = messagesApi


  def signUp(): Action[AnyContent] = Action.async { implicit request =>

    val signUpForm = userForms.userRegistrationConstraintList
    val listOfHobbies: Future[List[String]] = hobbyRepository.getHobbies
    listOfHobbies.map(hobbies => Ok(views.html.signUp("Sign Up", signUpForm, hobbies)))

  }

  def loginPage(): Action[AnyContent] = Action.async { implicit request =>

    Future.successful(Ok(views.html.login("Login", userForms.UserLoginConstraintList)))

  }

  def index(): Action[AnyContent] = Action.async { implicit request =>

    Future.successful(Ok(views.html.index()))

  }

  def successLoginMessageDisplay(): Action[AnyContent] = Action.async { implicit request =>

   Future.successful(Ok(views.html.successLoginMessage("Welcome!")))

  }

  def successMessageDisplay(): Action[AnyContent] = Action.async { implicit request =>

    Future.successful(Ok(views.html.successMessage("Welcome!")))

  }

  def forgotPassword(): Action[AnyContent] = Action.async { implicit request =>

    Future.successful(Ok(views.html.forgotPassword("Welcome!", userForms.UpdatePasswordConstraintList)))

  }

  def adminProfile(): Action[AnyContent] = Action.async { implicit request =>

    Future.successful(Ok(views.html.index())) //-------left

  }

  def viewUser(): Action[AnyContent] = Action.async { implicit request =>

    Logger.info("Displaying user information to admin")
    userRepository.getNormalUser.map(normalUserList =>
      Ok(views.html.viewUser("Welcome", normalUserList)))
  }

  def updateProfile(): Action[AnyContent] = Action.async { implicit request =>

    val userIdInString: Option[String] = request.session.get("userId")
    Logger.info(s"Get session in updateProfile(Application) $userIdInString")
    userIdInString match {
      case Some(userIdInString) => {

        val userId: Int = userIdInString.toInt
        val userDetails: Future[UserData] = userRepository.getUserById(userId)

        userDetails.flatMap(userData =>
          userData match {
            case userData: UserData =>
              Logger.info("Received user data.. Extracting user hobbies")
              val userListOfHobby: Future[Seq[String]] = userHobbyRepository.getUserHobby(userId)
              userListOfHobby.flatMap { listOfHobby =>
                listOfHobby match {
                  case Nil =>
                    Logger.info("No hobbies found for the given user-id")
                    Logger.info("Redirecting to Welcome Page")
                    Future.successful(Ok(views.html.index()))
                  case userListOfHobby: List[String] =>
                    val updateUserFormValue: Form[UpdateUserDetails] = userForms.userUpdateConstraintList.fill(UpdateUserDetails(
                      Name(userData.firstName, userData.middleName, userData.lastName), userData.mobileNumber,
                      userData.gender, userData.age, userListOfHobby))
                    hobbyRepository.getHobbies().map {
                      hobbies =>
                        Ok(views.html.profile("Edit Profile", updateUserFormValue, hobbies))
                    }
                }
              }
            case _ =>
              Logger.info("No data found for the given user-id")
              Logger.info("Redirecting to Welcome Page")
              Future.successful(Ok(views.html.index()))
          }
        )
      }
      case None => {
        Logger.info("Did not receive user-id")
        Logger.info("Redirecting to Welcome Page")
        Future.successful(Redirect(routes.Application.index()).flashing("error" -> "You need to login first"))

      }
    }
  }

  def viewAssignment(): Action[AnyContent] = Action.async { implicit request =>

    Logger.info("Displaying user information to admin")
    val userIdInString: Option[String] = request.session.get("userId")
    Logger.info(s"Get session in updateProfile(Application) $userIdInString")
    userIdInString match {
      case Some(userIdInString) => {

        val userId: Int = userIdInString.toInt
        userRepository.getUserById(userId).flatMap {
          case user: UserData =>
            assignmentRepository.getAssignments.flatMap{
              case assignmentList: List[AssignmentDetails] =>
                Logger.info("assign:" + assignmentList)
            userRepository.isAdminById(userId).map{
              case true =>
                Logger.info("Showing assignment with delete option to the admin")
             //   Ok(views.html.index())
               Ok(views.html.showAssignmentToAdmin("Welcome",assignmentList))
              case false =>
                Logger.info("Showing assignment to the normal user")
               // Ok(views.html.index())
                Ok(views.html.showAssignmentToUser("Welcome",assignmentList))
            }
              case _ =>
                Logger.info("Assignment did not received")
                Future.successful(Redirect(routes.Application.index()).flashing("error" -> "Something went wrong"))


            }
          case _=>
            Logger.info("Invalid user id")
            Future.successful(Redirect(routes.Application.index()).flashing("error" -> "User doesnot exists"))

        }
      }
    }

  }

  def addAssignment(): Action[AnyContent] = Action.async { implicit request =>

    Future.successful(Ok(views.html.addAssignment("Welcome!", userForms.AssignmentConstraintList)))
  }
}
