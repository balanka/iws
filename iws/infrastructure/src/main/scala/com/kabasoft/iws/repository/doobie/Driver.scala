package com.kabasoft.iws.repository.doobie

import java.nio.charset.CodingErrorAction

import cats.effect.IO

import scala.io.Codec
import com.kabasoft.iws.domain._
import com.kabasoft.iws.repository.doobie.ImportFunction.ImportBankStatement
import doobie.util.transactor.Transactor

import scala.concurrent.ExecutionContext

object Driver {

  def loadData() = {
    implicit val cs = IO.contextShift(ExecutionContext.global)
    val xa = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      "jdbc:postgresql://localhost/petstore",
      "postgres",
      "iws123"
    )
    val FS = ";"
    val decoder = Codec.UTF8.decoder.onMalformedInput(CodingErrorAction.IGNORE)
    //val extension = List("CSV", "csv", "tsv")
    val extension = List("CSV")
    val pathBS = "/Users/iwsmac/Downloads/import/bankStatement/43719244/"
    val iban = "DE22480501610043719244"
    val company = "2000"

    /*
    val pathSup = "/Users/iwsmac/Downloads/import/Supplier"
    val pathCust = "/Users/iwsmac/Downloads/import/Customer"
    //val pathBS= "/Users/iwsmac/Downloads/import/bankStatement/43719244"

    val pathPAB = "/Users/iwsmac/Downloads/import/periodicAccountBalance"
    val pathAcc = "/Users/iwsmac/Downloads/import/account"
    val pathBacc = "/Users/iwsmac/Downloads/import/bankAccount"
    val path1 = "/Users/iwsmac/Downloads/import/masterFinancialsTransaction"
    val path2 = "/Users/iwsmac/Downloads/import/detailsFinancialsTransaction"

     */

    val l6: List[BankStatement] =
      ImportFunction.getObjectList(ImportBankStatement.getObjects, pathBS, extension, FS, company, iban, decoder)
    val x = BankStatementService(xa).insert(l6).unsafeRunSync()

    //l6.foreach(bs.create(_))
    // println(("xx " + l6.filter(_.depositor.equalsIgnoreCase("Auftragskonto"))).foreach(println))
    println("Data loaded!!!!" + x)

  }

  def main(args: Array[String]): Unit =
    loadData()

}
