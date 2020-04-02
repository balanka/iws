package com.toracoya.petstore.error.json

import cats.data.{NonEmptyChain, NonEmptyList}
import com.toracoya.petstore.APIValidation

case class ErrorsJson(errors: NonEmptyList[ErrorJson])

object ErrorsJson {

  def from(errors: NonEmptyChain[APIValidation]): ErrorsJson =
    ErrorsJson(
      errors.toNonEmptyList.map(ErrorJson.from)
    )

}
