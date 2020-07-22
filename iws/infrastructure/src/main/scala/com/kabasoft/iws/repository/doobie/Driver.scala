package com.kabasoft.iws.repository.doobie

import java.nio.charset.CodingErrorAction

import cats.effect.IO

import scala.io.Codec
import com.kabasoft.iws.domain._
import com.kabasoft.iws.repository.doobie.ImportFunction.ImportBankStatement
import doobie.util.transactor.Transactor

import scala.concurrent.ExecutionContext

object Driver {

  def loadData(m: List[String]) = {
    //val filter = "1000"
    val List(a, b, c, d, e, f, g, h, i, j, k) = m
    println("%s %s %s  %s  %s %s %s %s %s %s  %s ".format(a, b, c, d, e, f, g, h, i, j, k))
    implicit val cs = IO.contextShift(ExecutionContext.global)
    val xa = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      "jdbc:postgresql://localhost/petstore",
      "postgres",
      "iws123"
    )
    val FS = ","
    val decoder = Codec.UTF8.decoder.onMalformedInput(CodingErrorAction.IGNORE)
    val extension = List("CSV", "csv", "tsv")
    val pathBS = "/Users/iwsmac/Downloads/import/bankStatement/43719244"
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

  def main(args: Array[String]): Unit = {
    val m = List(
      "DE22480501610043719244",
      "12.29.17",
      "12.29.17",
      "ONLINE-UEBERWEISUNG",
      "SVWZ+DATUM 29.12.2017 23.14 UHR  1.TAN 367776",
      "Stadt Bielefeld",
      "DE09480501610000000026",
      "SPBIDE3BXXX",
      "-117",
      "EUR",
      "Umsatz gebucht"
    );
    val List(a, b, c, d, e, f, g, h, i, j, k) = m
    println("%s %s %s  %s  %s %s %s %s %s %s ".format(a, b, c, d, e, f, g, h, i, j, k))
    loadData(m)
  }

}
