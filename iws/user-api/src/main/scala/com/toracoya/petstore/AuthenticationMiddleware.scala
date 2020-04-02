package com.toracoya.petstore

import cats.data.{Kleisli, OptionT}
import com.toracoya.petstore.auth.Authenticator.AuthToken
import com.toracoya.petstore.auth.{AuthenticationHeaders, Authenticator}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.AuthMiddleware
import org.http4s.{AuthedRoutes, Request}
import zio._
import zio.interop.catz._

trait AuthenticationMiddleware {

  type AppEnvironment <: Authenticator
  type AppTask[A] = RIO[AppEnvironment, A]

  val dsl: Http4sDsl[AppTask] = Http4sDsl[AppTask]
  import dsl._

  val authenticationHeaders = new AuthenticationHeaders[AppEnvironment] {}

  def authUser: Kleisli[AppTask, Request[AppTask], Either[String, AuthToken]] =
    Kleisli({ request =>
      authenticationHeaders.getToken(request).map { e =>
        {
          e.left.map(_.toString)
        }
      }
    })

  val onFailure: AuthedRoutes[String, AppTask] = Kleisli(
    req =>
      OptionT.liftF {
        Forbidden()
        // Forbidden(req.authInfo)
      }
  )

  val authenticationMiddleware: AuthMiddleware[AppTask, AuthToken] = AuthMiddleware(authUser, onFailure)
}
