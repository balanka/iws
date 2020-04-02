package com.toracoya.petstore.error.json

import com.toracoya.petstore.APIValidation

case class ErrorJson(message: String)

object ErrorJson {

  def from(validation: APIValidation): ErrorJson =
    ErrorJson(validation.message)
}
