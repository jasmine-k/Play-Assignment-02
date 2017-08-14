package controllers

import com.google.inject.Inject
import models.{HobbyRepository, UserData, UserHobbyRepository, UserRepository}
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
                            hobbyRepository: HobbyRepository, userHobbyRepository: UserHobbyRepository) extends Controller with I18nSupport {

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
    //.fill(User("Jasmine",Option("Kaur"),"Kaur",9898989898L,"Female",19,"jasmine@gmail.com","jasmine","jasmine"))
    val listOfHobbies: Future[List[String]] = hobbyRepository.getHobbies
    listOfHobbies.map(hobbies => Ok(views.html.signUp("Sign Up", signUpForm, hobbies)))

  }

  def loginPage(): Action[AnyContent] = Action { implicit request =>

    Ok(views.html.login("Login", userForms.UserLoginConstraintList))

  }

  def index(): Action[AnyContent] = Action { implicit request =>

    Ok(views.html.index())

  }

  def successLoginMessageDisplay(): Action[AnyContent] = Action { implicit request =>

    Ok(views.html.successLoginMessage("Welcome!"))

  }

  def successMessageDisplay(): Action[AnyContent] = Action { implicit request =>

    Ok(views.html.successMessage("Welcome!"))

  }

  def forgotPassword():Action[AnyContent]=  Action { implicit request =>

   // val updatePasswordForm: Form[UpdatePassword] = userForms.UpdatePasswordConstraintList
    Ok(views.html.forgotPassword("Welcome!", userForms.UpdatePasswordConstraintList))

  }

  def adminProfile(): Action[AnyContent] = Action { implicit request =>

    Ok(views.html.adminProfile("Welcome!"))

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
              userListOfHobby.flatMap { listOfHobby=>
                listOfHobby match {
                  case Nil =>
                    Logger.info("No hobbies found for the given user-id")
                    Logger.info("Redirecting to Welcome Page")
                    Future.successful(Ok(views.html.index()))
                  case userListOfHobby: List[String] =>
                    val updateUserFormValue: Form[UpdateUserDetails] = userForms.userUpdateConstraintList.fill(UpdateUserDetails(
                      Name(userData.firstName, userData.middleName, userData.lastName), userData.mobileNumber,
                      userData.gender, userData.age, userData.email, userListOfHobby))
                    hobbyRepository.getHobbies().map {
                      hobbies =>
                          Ok(views.html.profile("Edit Profile", updateUserFormValue,hobbies))
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
        Future.successful(Ok(views.html.index()))
      }
    }
  }

}
