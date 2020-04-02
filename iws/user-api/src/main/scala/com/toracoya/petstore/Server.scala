package com.toracoya.petstore

import cats.Monad
import cats.effect._
import cats.implicits._
import com.toracoya.petstore.config.PetStoreConfig
import com.toracoya.petstore.pet.{
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
import com.toracoya.petstore.repository.doobie.{
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
      financialsDetailsRepository = DoobieFinancialsTransactionDetailsRepository[F](transactor)
      financialsRepository = DoobieFinancialsTransactionRepository[F](transactor)
      art_endpoints = pets.ArticleEndpoints(ArticleService(artRepository))
      mtf_endpoints = pets.MasterfileEndpoints(MasterfileService(masterfileRepository))
      acc_endpoints = pets.AccountEndpoints(AccountService(accRepository))
      cc_endpoints = pets.CostCenterEndpoints(CostCenterService(costCenterRepository))
      pac_endpoints = pets.PeriodicAccountBalanceEndpoints(PeriodicAccountBalanceService(pacRepository))
      customer_endpoints = pets.CustomerEndpoints(CustomerService(customerRepository))
      supplier_endpoints = pets.SupplierEndpoints(SupplierService(supplierRepository))
      routes_endpoints = pets.RoutesEndpoints(RoutesService(routesRepository))
      financials_endpoints = pets.FinancialsEndpoints(
        FinancialsTransactionService(financialsRepository),
        FinancialsTransactionDetailsService(financialsDetailsRepository)
      )
      journal_endpoints = pets.JournalEndpoints(JournalService(journalRepository))
      endpoints = mtf_endpoints <+> acc_endpoints <+> art_endpoints <+>
        routes_endpoints <+> cc_endpoints <+> customer_endpoints <+> supplier_endpoints <+>
        financials_endpoints <+> pac_endpoints <+> journal_endpoints
    } yield Router("/pets" -> endpoints).orNotFound //yield Router("/mtf" -> mtf_endpoints, "/pets" -> endpoints, "/acc" -> acc_endpoints).orNotFound //yield Router("/" -> endpoints).orNotFound

}
