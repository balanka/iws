package com.kabasoft.iws.pagination

import org.http4s.QueryParamDecoder
import org.http4s.dsl.impl.OptionalQueryParamDecoderMatcher

case class Pagination(page: Int, pageSize: Int) {

  def range: (Int, Int) = {
    val from = page * pageSize
    val until = from + pageSize
    (from, until)
  }
}

object Pagination {

  import QueryParamDecoder._

  object OffsetMatcher extends OptionalQueryParamDecoderMatcher[Int]("offset")
  object PageSizeMatcher extends OptionalQueryParamDecoderMatcher[Int]("pageSize")

  val DefaultPage: Int = 0
  val DefaultPageSize: Int = 1000000
}
