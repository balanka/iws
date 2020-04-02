package com.toracoya.petstore

import com.toracoya.petstore.pagination.PaginationValidator.MaxPageSize

sealed trait APIValidation extends Product with Serializable {
  val message: String
}

object APIValidation {

  case object InvalidPage extends APIValidation {
    override val message: String = "Page must be greater than or equal to 0"
  }

  case object InvalidPageSize extends APIValidation {
    override val message: String = s"Page size must be from 0 to $MaxPageSize"
  }
}
