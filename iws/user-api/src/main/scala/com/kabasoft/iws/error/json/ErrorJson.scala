package com.kabasoft.iws.error.json

import com.kabasoft.iws.APIValidation

case class ErrorJson(message: String)

object ErrorJson {

  def from(validation: APIValidation): ErrorJson =
    ErrorJson(validation.message)
}
