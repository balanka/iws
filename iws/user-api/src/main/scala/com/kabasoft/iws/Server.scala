package com.kabasoft.iws

import com.kabasoft.iws.config.PetStoreConfig
import com.kabasoft.iws.repository.doobie.{
  AccountService,
  ArticleService,
  BankService,
  BankStatementService,
  CostCenterService,
  CustomerService,
  FinancialsTransactionService,
  JournalService,
  MasterfileService,
  PeriodicAccountBalanceService,
  RoutesService,
  SupplierService,
  VatService
}

import cats.Monad
import cats.effect._
import cats.implicits._
import io.circe.config.parser
import org.http4s.implicits._
import org.http4s.server.syntax._
import org.http4s.server.Router
import org.http4s.server.middleware.CORS
import org.http4s.server.blaze.BlazeServerBuilder

import scala.language.higherKinds

object Server extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    buildServer
      .use { _ =>
        IO.never
      }
      .as(ExitCode.Success)

  private def buildServer[F[_]: ContextShift: ConcurrentEffect: Monad: Timer] =
    for {
      config <- Resource.liftF(parser.decodePathF[F, PetStoreConfig]("petstore"))
      httpApp <- buildHttpApp[F](config)
      server <- BlazeServerBuilder[F]
        .bindHttp(config.port, config.host)
        .withHttpApp(CORS(httpApp))
        .resource
    } yield server

  private def buildHttpApp[F[_]: ContextShift: ConcurrentEffect: Monad: Timer](config: PetStoreConfig) =
    for {
      transactor <- config.db.transactor

      cc_endpoints = endpoint.CostCenterEndpoints(CostCenterService(transactor))
      art_endpoints = endpoint.ArticleEndpoints(ArticleService(transactor))
      mtf_endpoints = endpoint.MasterfileEndpoints(MasterfileService(transactor))
      acc_endpoints = endpoint.AccountEndpoints(AccountService(transactor))
      pac_endpoints = endpoint.PeriodicAccountBalanceEndpoints(
        PeriodicAccountBalanceService(transactor)
      )
      customer_endpoints = endpoint.CustomerEndpoints(CustomerService(transactor))
      supplier_endpoints = endpoint.SupplierEndpoints(SupplierService(transactor))
      routes_endpoints = endpoint.RoutesEndpoints(RoutesService(transactor))
      financials_endpoints = endpoint.FinancialsEndpoints(
        FinancialsTransactionService(transactor)
      )
      journal_endpoints = endpoint.JournalEndpoints(JournalService(transactor))
      bankstmt_endpoints = endpoint.BankStatementEndpoints(
        BankStatementService(transactor)
      )
      bank_endpoints = endpoint.BankEndpoints(BankService(transactor))
      vat_endpoints = endpoint.VatEndpoints(VatService(transactor))
      endpoints = mtf_endpoints <+> acc_endpoints <+> art_endpoints <+> bank_endpoints <+> vat_endpoints <+>
        routes_endpoints <+> cc_endpoints <+> customer_endpoints <+> supplier_endpoints <+>
        financials_endpoints <+> pac_endpoints <+> journal_endpoints <+> bankstmt_endpoints
    } yield Router("/pets" -> endpoints).orNotFound //yield Router("/mtf" -> mtf_endpoints, "/pets" -> endpoints, "/acc" -> acc_endpoints).orNotFound //yield Router("/" -> endpoints).orNotFound

}
