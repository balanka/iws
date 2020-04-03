package com.kabasoft.iws

import com.kabasoft.iws.config.PetStoreConfig
import com.kabasoft.iws.service.{
  AccountService,
  ArticleService,
  CostCenterService,
  CustomerService,
  FinancialsTransactionDetailsService,
  FinancialsTransactionService,
  JournalService,
  MasterfileService,
  PeriodicAccountBalanceService,
  RoutesService,
  SupplierService
}
import com.kabasoft.iws.repository.doobie.{
  DoobieAccountRepository,
  DoobieArticleRepository,
  DoobieCostCenterRepository,
  DoobieCustomerRepository,
  DoobieFinancialsTransactionDetailsRepository,
  DoobieFinancialsTransactionRepository,
  DoobieJournalRepository,
  DoobieMasterfileRepository,
  DoobiePeriodicAccountBalanceRepository,
  DoobieRoutesRepository,
  DoobieSupplierRepository
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
        //.withHttpApp(CORS(httpApp))
        .resource
    } yield server

  private def buildHttpApp[F[_]: ContextShift: ConcurrentEffect: Monad: Timer](config: PetStoreConfig) =
    for {
      transactor <- config.db.transactor
      artRepository = DoobieArticleRepository[F](transactor)
      accRepository = DoobieAccountRepository[F](transactor)
      masterfileRepository = DoobieMasterfileRepository[F](transactor)
      costCenterRepository = DoobieCostCenterRepository[F](transactor)
      customerRepository = DoobieCustomerRepository[F](transactor)
      supplierRepository = DoobieSupplierRepository[F](transactor)
      routesRepository = DoobieRoutesRepository[F](transactor)
      journalRepository = DoobieJournalRepository[F](transactor)
      pacRepository = DoobiePeriodicAccountBalanceRepository[F](transactor)
      //financialsDetailsRepository = DoobieFinancialsTransactionDetailsRepository[F](transactor)
      financialsRepository = DoobieFinancialsTransactionRepository[F](transactor)
      art_endpoints = endpoint.ArticleEndpoints(ArticleService(artRepository))
      mtf_endpoints = endpoint.MasterfileEndpoints(MasterfileService(masterfileRepository))
      acc_endpoints = endpoint.AccountEndpoints(AccountService(accRepository))
      cc_endpoints = endpoint.CostCenterEndpoints(CostCenterService(costCenterRepository))
      pac_endpoints = endpoint.PeriodicAccountBalanceEndpoints(PeriodicAccountBalanceService(pacRepository))
      customer_endpoints = endpoint.CustomerEndpoints(CustomerService(customerRepository))
      supplier_endpoints = endpoint.SupplierEndpoints(SupplierService(supplierRepository))
      routes_endpoints = endpoint.RoutesEndpoints(RoutesService(routesRepository))
      financials_endpoints = endpoint.FinancialsEndpoints(FinancialsTransactionService(financialsRepository))
      journal_endpoints = endpoint.JournalEndpoints(JournalService(journalRepository))
      endpoints = mtf_endpoints <+> acc_endpoints <+> art_endpoints <+>
        routes_endpoints <+> cc_endpoints <+> customer_endpoints <+> supplier_endpoints <+>
        financials_endpoints <+> pac_endpoints <+> journal_endpoints
    } yield Router("/pets" -> endpoints).orNotFound //yield Router("/mtf" -> mtf_endpoints, "/pets" -> endpoints, "/acc" -> acc_endpoints).orNotFound //yield Router("/" -> endpoints).orNotFound

}
