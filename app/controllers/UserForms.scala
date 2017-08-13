package controllers

import play.api.data.Forms.{mapping, text, _}
import play.api.data._
import play.api.data.validation.{Constraint, Invalid, Valid}
import scala.concurrent.ExecutionContext.Implicits.global

case class User(firstName:String,lastName: String, gender:String, dateOfBirth:String ,email: String, password: String, confirmPassword: String)
case class UserLogin(email:String, password: String)

class UserForms {

  val userRegistrationConstraintList = Form(
    mapping(
      "firstName"-> text.verifying("Enter your First Name", firstName => !firstName.isEmpty),
      "lastName" -> text.verifying("Enter your Last Name", lastName => !lastName.isEmpty),
      "gender" -> text,
      "dateOfBirth" -> text,
      "email" -> email,
      "password" -> text.verifying(passwordValidation),
      "confirmPassword" -> text.verifying("Confirm Password should be greater than 5 characters",password => password.length>5)
    )(User.apply)(User.unapply).verifying("Checking for password match", data=>
  data.password == data.confirmPassword))

  def passwordValidation: Constraint[String] ={
    Constraint("passwordCheck")({
      password =>
        if(password.length()>5 && password.length()<20){
          Valid
        }
        else{
          Invalid("Password length should be between 5 to 20 characters")
        }
    }

    )
  }
  val UserLoginConstraintList = Form(
    mapping(
      "email" -> email,
      "password" -> text.verifying(passwordValidation)
    )(UserLogin.apply)(UserLogin.unapply))


}
