package controllers

import com.google.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
class Application @Inject()(implicit val messagesApi: MessagesApi) extends Controller with I18nSupport{

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  val userForms = new UserForms

  def signUp() = Action{ implicit request =>

    val filledForm = userForms.userRegistrationConstraintList.fill(User("Jasmine","Kaur","Female","03/01/1995","jasmine@gmail.com","jasmine","jasmine"))
    Ok(views.html.signUp("Sign Up", filledForm))

  }

  def loginPage() = Action{ implicit request =>

    Ok(views.html.login("Login", userForms.UserLoginConstraintList))

  }

  def successLoginMessageDisplay()= Action{ implicit request =>

    Ok(views.html.successLoginMessage("Welcome!"))

  }

  def successMessageDisplay()= Action{ implicit request =>

    Ok(views.html.successMessage("Welcome"))

  }
}
