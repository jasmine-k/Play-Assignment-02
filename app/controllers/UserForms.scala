package controllers

import play.api.data.Forms.{mapping, text, _}
import play.api.data._
import play.api.data.validation.{Constraint, Invalid, Valid}

import scala.concurrent.Future

case class User(name: Name, mobileNumber: Long, gender: String,
                age: Int, email: String, password: String, confirmPassword: String, hobbies: List[String])

case class UserLogin(email: String, password: String)

case class Name(firstName: String, middleName: Option[String], lastName: String)

case class UpdateUserDetails(name: Name, mobileNumber: Long, gender: String,
                             age: Int, email: String, hobbies: List[String])

case class UpdatePassword(email: String, password: String, confirmPassword: String)


class UserForms {

  val MIN_AGE = 18
  val MAX_AGE = 75
  val userRegistrationConstraintList = Form(
    mapping(
      "name" -> mapping(
      "firstName" -> nonEmptyText, //text.verifying("Enter your First Name", firstName => !firstName.isEmpty),
      "middleName" -> optional(text),
      "lastName" -> nonEmptyText)(Name.apply)(Name.unapply), // text.verifying("Enter your Last Name", lastName => !lastName.isEmpty),
      "mobileNumber" -> longNumber.verifying(mobileNumberValidation), //.verifying(numberOfDigits),
      "gender" -> text,
      "age" -> number(MIN_AGE,MAX_AGE),
      "email" -> email,
      "password" -> text.verifying(passwordValidation),
      "confirmPassword" -> nonEmptyText,//.verifying(passwordValidation),
      "hobbies" -> list(text).verifying(hobbiesValidation)
    )(User.apply)(User.unapply).verifying("Checking for password match", data =>
      data.password == data.confirmPassword))


  def mobileNumberValidation: Constraint[Long] = {
    Constraint("mobileNumberCheck")({
      mobileNumber =>
        if (mobileNumber.toString.length == 10) {
          Valid
        }
        else {
          Invalid("Invalid Mobile Number")
        }
    })
  }

  def passwordValidation: Constraint[String] = {
    Constraint("passwordCheck")({
      password =>
        if (password.length() >= 5 && password.length() <= 20) {
          Valid
        }
        else {
          Invalid("Password length should be between 5 to 20 characters")
        }
    })
  }

  def hobbiesValidation: Constraint[List[String]] ={
    Constraint("hobbiesCheck")({
      hobbies =>
        if(!hobbies.isEmpty) {
          Valid
        }
        else {
          Invalid("Select at least one hobby")
        }
    })
  }

  val UserLoginConstraintList = Form(
    mapping(
      "email" -> email,
      "password" -> text.verifying(passwordValidation)
    )(UserLogin.apply)(UserLogin.unapply))

  val userUpdateConstraintList = Form(
    mapping(
      "name" -> mapping(
        "firstName" -> nonEmptyText,
        "middleName" -> optional(text),
        "lastName" -> nonEmptyText)(Name.apply)(Name.unapply),
      "mobileNumber" -> longNumber.verifying(mobileNumberValidation),
      "gender" -> text,
      "age" -> number(MIN_AGE,MAX_AGE),
      "email" -> email,
      "hobbies" -> list(text).verifying(hobbiesValidation)
    )(UpdateUserDetails.apply)(UpdateUserDetails.unapply))

  val UpdatePasswordConstraintList = Form(
    mapping(
      "email" -> email,
      "password" -> text.verifying(passwordValidation),
      "confirmPassword" -> nonEmptyText
    )(UpdatePassword.apply)(UpdatePassword.unapply).verifying("Checking for password match", data =>
    data.password == data.confirmPassword))

}
