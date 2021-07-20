package com.iamsmkr

package object primecommon {

  case class InvalidNumberArg(msg: String)

  def validateNumberArg(number: Long): Option[InvalidNumberArg] =
    if (number < 2) Some(InvalidNumberArg("Please provide a number greater 1")) else None
}
