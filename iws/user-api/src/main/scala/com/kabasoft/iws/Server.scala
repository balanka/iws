package com.kabasoft.iws

import com.kabasoft.iws.config.IwsConfig
import com.kabasoft.iws.config.IwsConfig._
import com.kabasoft.iws.repository.doobie.{
  AccountService,
  ArticleService,
  BankService,
  BankStatementService,
  CostCenterService,
  CustomerService,
  DatabaseConfig,
  DoobieAuthRepositoryInterpreter,
  DoobieUserRepositoryInterpreter,
  FinancialsTransactionService,
  JournalService,
  MasterfileService,
  PeriodicAccountBalanceService,
  RoutesService,
  SupplierService,
  UserService,
  UserValidationInterpreter,
  VatService
}
import cats.effect._
//import cats.implicits._
import io.circe.config.parser
import org.http4s.implicits._
import org.http4s.server.{Router, Server => H4Server}
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS
import doobie.util.ExecutionContexts
import tsec.mac.jca.HMACSHA256
import tsec.passwordhashers.jca.BCrypt
import tsec.authentication.SecuredRequestHandler
import com.kabasoft.iws.auth.Auth
import com.kabasoft.iws.endpoint.{
  AccountEndpoints,
  ArticleEndpoints,
  BankEndpoints,
  BankStatementEndpoints,
  CostCenterEndpoints,
  CustomerEndpoints,
  FinancialsEndpoints,
  JournalEndpoints,
  MasterfileEndpoints,
  PeriodicAccountBalanceEndpoints,
  RoutesEndpoints,
  SupplierEndpoints,
  UserEndpoints,
  VatEndpoints
}

object Server extends IOApp {

  def createServer[F[_]: ContextShift: ConcurrentEffect: Timer]: Resource[F, H4Server[F]] =
    for {
      config <- Resource.liftF(parser.decodePathF[F, IwsConfig]("iws"))
      serverEc <- ExecutionContexts.cachedThreadPool[F]
      connEc <- ExecutionContexts.fixedThreadPool[F](config.db.connections.poolSize)
      txnEc <- ExecutionContexts.cachedThreadPool[F]
      xa <- DatabaseConfig.dbTransactor(config.db, connEc, Blocker.liftExecutionContext(txnEc))
      key <- Resource.liftF(HMACSHA256.generateKey[F])
      authRepo = DoobieAuthRepositoryInterpreter[F, HMACSHA256](key, xa)
      userRepo = DoobieUserRepositoryInterpreter[F](xa)
      userValidation = UserValidationInterpreter[F](userRepo)
      userService = UserService[F](userRepo, userValidation)
      authenticator = Auth.jwtAuthenticator[F, HMACSHA256](key, authRepo, userRepo)
      routeAuth = SecuredRequestHandler(authenticator)
      userEndpoints = UserEndpoints
        .endpoints[F, BCrypt, HMACSHA256](userService, BCrypt.syncPasswordHasher[F], routeAuth)
      cc_endpoints = CostCenterEndpoints.endpoints[F, HMACSHA256](CostCenterService(xa), routeAuth)
      art_endpoints = ArticleEndpoints.endpoints[F, HMACSHA256](ArticleService(xa), routeAuth)
      mtf_endpoints = MasterfileEndpoints.endpoints[F, HMACSHA256](MasterfileService(xa), routeAuth)
      acc_endpoints = AccountEndpoints.endpoints[F, HMACSHA256](
        AccountService(xa, config.app.balanceSheetAccountId, config.app.incomeStmtAccountId),
        routeAuth
      )
      pac_endpoints = PeriodicAccountBalanceEndpoints.endpoints[F, HMACSHA256](
        PeriodicAccountBalanceService(xa),
        routeAuth
      )
      customer_endpoints = CustomerEndpoints.endpoints[F, HMACSHA256](CustomerService(xa), routeAuth)
      supplier_endpoints = SupplierEndpoints.endpoints[F, HMACSHA256](SupplierService(xa), routeAuth)
      routes_endpoints = RoutesEndpoints.endpoints[F, HMACSHA256](RoutesService(xa), routeAuth)
      financials_endpoints = FinancialsEndpoints.endpoints[F, HMACSHA256](FinancialsTransactionService(xa), routeAuth)
      journal_endpoints = JournalEndpoints.endpoints[F, HMACSHA256](JournalService(xa), routeAuth)
      bankstmt_endpoints = BankStatementEndpoints.endpoints[F, HMACSHA256](BankStatementService(xa), routeAuth)
      bank_endpoints = BankEndpoints.endpoints[F, HMACSHA256](BankService(xa), routeAuth)
      vat_endpoints = VatEndpoints.endpoints[F, HMACSHA256](VatService(xa), routeAuth)
      // endpoints = mtf_endpoints <+> acc_endpoints <+> art_endpoints <+> vat_endpoints <+>
      //  routes_endpoints <+> cc_endpoints <+> customer_endpoints <+> supplier_endpoints <+>
      //   financials_endpoints <+> pac_endpoints <+> journal_endpoints <+> bankstmt_endpoints
      httpApp = Router(
        "/acc" -> acc_endpoints,
        "/art" -> art_endpoints,
        "/cc" -> cc_endpoints,
        "/bank" -> bank_endpoints,
        "/bs" -> bankstmt_endpoints,
        "/cust" -> customer_endpoints,
        "/ftr" -> financials_endpoints,
        "/jou" -> journal_endpoints,
        "/pac" -> pac_endpoints,
        "/mf" -> mtf_endpoints,
        "/rt" -> routes_endpoints,
        "/sup" -> supplier_endpoints,
        "/users" -> userEndpoints,
        "/vat" -> vat_endpoints
      ).orNotFound
      server <- BlazeServerBuilder[F](serverEc)
        .bindHttp(config.server.port, config.server.host)
        .withHttpApp(CORS(httpApp))
        .resource
    } yield server

  def run(args: List[String]): IO[ExitCode] = createServer.use(_ => IO.never).as(ExitCode.Success)
}
