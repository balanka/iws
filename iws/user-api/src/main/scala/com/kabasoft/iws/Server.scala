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
  FinancialsTransactionService,
  JournalService,
  MasterfileService,
  PeriodicAccountBalanceService,
  RoutesService,
  SupplierService,
  VatService
}

import cats.effect._
import cats.implicits._
import io.circe.config.parser
import org.http4s.implicits._
import org.http4s.server.{Router, Server => H4Server}
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS
import doobie.util.ExecutionContexts

object Server extends IOApp {

  def createServer[F[_]: ContextShift: ConcurrentEffect: Timer]: Resource[F, H4Server[F]] =
    for {
      config <- Resource.liftF(parser.decodePathF[F, IwsConfig]("iws"))
      serverEc <- ExecutionContexts.cachedThreadPool[F]
      connEc <- ExecutionContexts.fixedThreadPool[F](config.db.connections.poolSize)
      txnEc <- ExecutionContexts.cachedThreadPool[F]
      xa <- DatabaseConfig.dbTransactor(config.db, connEc, Blocker.liftExecutionContext(txnEc))
      cc_endpoints = endpoint.CostCenterEndpoints(CostCenterService(xa))
      art_endpoints = endpoint.ArticleEndpoints(ArticleService(xa))
      mtf_endpoints = endpoint.MasterfileEndpoints(MasterfileService(xa))
      acc_endpoints = endpoint.AccountEndpoints(
        AccountService(xa, config.app.balanceSheetAccountId, config.app.incomeStmtAccountId)
      )
      pac_endpoints = endpoint.PeriodicAccountBalanceEndpoints(
        PeriodicAccountBalanceService(xa)
      )
      customer_endpoints = endpoint.CustomerEndpoints(CustomerService(xa))
      supplier_endpoints = endpoint.SupplierEndpoints(SupplierService(xa))
      routes_endpoints = endpoint.RoutesEndpoints(RoutesService(xa))
      financials_endpoints = endpoint.FinancialsEndpoints(
        FinancialsTransactionService(xa)
      )
      journal_endpoints = endpoint.JournalEndpoints(JournalService(xa))
      bankstmt_endpoints = endpoint.BankStatementEndpoints(
        BankStatementService(xa)
      )
      bank_endpoints = endpoint.BankEndpoints(BankService(xa))
      vat_endpoints = endpoint.VatEndpoints(VatService(xa))
      endpoints = mtf_endpoints <+> acc_endpoints <+> art_endpoints <+> bank_endpoints <+> vat_endpoints <+>
        routes_endpoints <+> cc_endpoints <+> customer_endpoints <+> supplier_endpoints <+>
        financials_endpoints <+> pac_endpoints <+> journal_endpoints <+> bankstmt_endpoints
      httpApp = Router("/pets" -> endpoints).orNotFound
      server <- BlazeServerBuilder[F](serverEc)
        .bindHttp(config.server.port, config.server.host)
        .withHttpApp(CORS(httpApp))
        .resource
    } yield server

  def run(args: List[String]): IO[ExitCode] = createServer.use(_ => IO.never).as(ExitCode.Success)
}
