package com.kabasoft.iws.error.json

import cats.data.{NonEmptyChain, NonEmptyList}

import com.kabasoft.iws.APIValidation

case class ErrorsJson(errors: NonEmptyList[ErrorJson])

object ErrorsJson {

  def from(errors: NonEmptyChain[APIValidation]): ErrorsJson =
    ErrorsJson(
      errors.toNonEmptyList.map(ErrorJson.from)
    )

}
