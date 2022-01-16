package com.kabasoft.iws.repository.doobie
import java.io.File
import java.nio.charset.CharsetDecoder
import java.time.{Instant, LocalDate, ZoneId}
import java.time.format.DateTimeFormatter

import com.kabasoft.iws.domain._

trait ImportFunction[A <: IWS] {

  //val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
  val formatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(ZoneId.of("Europe/Berlin"))
  //  .withZone(ZoneId.systemDefault());
  //val dateFormat1 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
  //val dateFormat = new SimpleDateFormat("dd.MM.yyyy'T'HH:mm:ss'Z'")

  def transform(s: String) =
    if (s.indexOf("\"") >= 0) {
      val r = s.substring(s.indexOf("\""), s.lastIndexOf("\"") + 1)
      val r2 = r.replaceAll("\"", "").replaceAll(",", " ")
      val ret = s.replace(r, r2)
      ret
    } else s

  def getPF: ((List[String], String, String)) => List[Either[String, A]]
  def getLines(
    file: File,
    company: String,
    iban: String,
    decoder: CharsetDecoder,
    FS: String
  ): List[Either[String, A]] =
    scala.io.Source
      .fromFile(file)(decoder)
      .getLines()
      .toList
      .map(_.replace("\"", ""))
      .map(_.split(FS).toList)
      .flatMap(l => getPF((l, company, iban)))
  def getObjects(
    path: String,
    extension: List[String],
    FS: String,
    company: String,
    iban: String,
    decoder: CharsetDecoder
  ): List[Either[String, A]] =
    getListOfFiles(new File(path), extension).flatMap(getLines(_, company, iban, decoder, FS))

  def getListOfFiles(dir: File, extensions: List[String]): List[File] =
    dir.listFiles.filter(_.isFile).toList.filter(file => extensions.exists(file.getName.endsWith(_)))

  def applyN(
    rawdata: (List[String], String, String),
    f: ((List[String], String, String)) => List[Either[String, A]]
  ): List[Either[String, A]] =
    f(rawdata)
  trait getPF1 extends PartialFunction[(List[String], String, String), List[Either[String, A]]] {
    def isDefinedAt(x: (List[String], String, String)) = !x._1.isEmpty
  }
}
object ImportFunction {

  type importFuncType[A <: IWS] =
    (String, List[String], String, String, String, CharsetDecoder) => List[Either[String, A]]

  def getObjectList[A <: IWS](
    func: importFuncType[A],
    path: String,
    extension: List[String],
    FS: String,
    iban: String,
    company: String,
    decoder: CharsetDecoder
  ): List[A] =
    func(path, extension, FS, iban, company, decoder).collect { case Right(x) => x }

  /*
  def decodeBank(x: (String, String, String, String, String, String, String, String, String)): Unit =
    List(Right(BankAccount(x._1.trim, x._2.trim, x._3.trim, x._8.trim)))

  object ImportBankAccount extends ImportFunction[BankAccount] {
    def getPF = new getPF1 {
      def apply(rawdata: List[String]): List[Either[String, BankAccount]] = applyN(rawdata, decode)
      def decode(rawdata: List[String]) =
        //rawdata collect {
        //case List(a, b, c, d, e, f, g, h, i) =>  Right(BankAccount(a.toString.trim, b.toString.trim, c.toString.trim, h.toString.trim))
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i) =>
            try {
              val ba = BankAccount(a.toString.trim, b.toString.trim, c.toString.trim, h.toString.trim)
              List(Right(ba))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println("%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s %s %s".format(a, b, c, d, e, f, g, h, i));
                List(Left(ex.getMessage))
              }
            }
          case y => {
            List(Left(y.toString))
          }

        }
    }
  }

  object ImportAccount extends ImportFunction [Account] {
    def getPF= new getPF1 {
      def apply(rawdata: List[String]):List[Either[String, Account]] =applyN(rawdata, decode)
      def decode(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p) =>
            try {
              val ax=Account(a.trim, b.trim, c.trim, d.trim, e.trim, f.trim, g.trim, h.trim,
                i.trim, j.trim, k.trim,l.trim, m.trim, n.trim, o.trim, p.trim)
              List(Right(ax))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println("%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s %s %s".format(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p));
                List(Left(ex.getMessage))
              }
            }
          case y => {
            println("x>>>>>> %s", y)
            List(Left(y.toString))
          }
        }
    }
  }
  object ImportDetailsFinancialsTransaction extends ImportFunction [DetailsFinancialsTransaction] {
    def getPF= new getPF1 {
      def apply(rawdata: List[String]):List[Either[String, DetailsFinancialsTransaction]] =applyN(rawdata, decode)
      def decode(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i, j, _, _) =>
            try {
              val dx = DetailsFinancialsTransaction(a.trim, b.trim, c.trim, d.trim, e.trim, f.trim, g.trim,
                h.trim, i.trim, j.trim)
              List(Right(dx))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println("%s %s %s  %s  %s %s %s %s %s %s".format(a, b, c, d, e, f, g, h, i, j));
                List(Left(ex.getMessage))
              }
            }
          case y => {
            printf("\nx>>>>>> %s", y)
            List(Left(y.toString))
          }
        }
    }


  }
  object ImportFinancialsTransaction extends ImportFunction [FinancialsTransaction] {
    def getPF= new getPF1 {
      def apply(rawdata: List[String]):List[Either[String, FinancialsTransaction]] =applyN(rawdata, decode)
      def decode(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t) =>
            try {
              val tx=FinancialsTransaction(a.trim, b.trim, c.trim, d.trim, f.trim, h.trim, g.trim,
                p.trim, m.trim, t.trim, i.trim, o.trim, e.trim, s.trim)
              List(Right(tx))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println("%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s".format(a, b, c, d, f, h, g, p, m, t, i, o, e, s));
                List(Left(ex.getMessage))
              }
            }
          case y => {
            printf("x>>>>>> %s", y)
            List(Left(y.toString))
          }
        }
    }

  }


  object ImportSupplier extends ImportFunction [Supplier] {
    def getPF= new getPF1 {
      def apply(rawdata: List[String]):List[Either[String, Supplier]] =applyN(rawdata, decode)
      def decode(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r) =>
            try {
              val tx= Supplier(a.trim, b.trim, "", c.trim, d.trim, e.trim, f.trim,  g.trim, h.trim, i.trim,
                j.trim, k.trim,  n.trim, o.trim, p.trim, q.trim, r.trim)
              List(Right(tx))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println("%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s %s %s %s %s".format(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r));
                List(Left(ex.getMessage))
              }
            }
          case y => {
            printf("x>>>>>> %s", y)
            List(Left(y.toString))
          }
        }
    }
  }
  object ImportCustomer extends ImportFunction [Customer] {
    def getPF= new getPF1 {
      def apply(rawdata: List[String]):List[Either[String, Customer]] =applyN(rawdata, decode)
      def decode(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r) =>
            try {
              val tx= Customer(a.trim, b.trim, "", c.trim, d.trim, e.trim, f.trim,  g.trim, h.trim, i.trim,
                j.trim, k.trim,  n.trim, o.trim, p.trim, q.trim, r.trim)
              List(Right(tx))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println("%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s %s %s %s %s".format(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r));
                List(Left(ex.getMessage))
              }
            }
          case y => {
            printf("x>>>>>> %s", y)
            List(Left(y.toString))
          }
        }
    }

  }


  object ImportPeriodicAccountBalance extends ImportFunction[PeriodicAccountBalance] {
    def getPF = new getPF1 {
      def apply(rawdata: List[String]): List[Either[String, PeriodicAccountBalance]] = applyN(rawdata, decode)
      def decode(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q) =>
            try {
              val tx = PeriodicAccountBalance(c.trim, d.trim, m.trim, n.trim, e.trim, f.trim, g.trim, h.trim)
              List(Right(tx))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println(
                  "%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s %s %s %s"
                    .format(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)
                )
                List(Left(ex.getMessage))
              }
            }
          case y => {
            printf("x>>>>>> %s", y)
            List(Left(y.toString))
          }
        }
    }

  }

   */

  object ImportBankStatement extends ImportFunction[BankStatement] {
    def addYear(d: String): String = {
      println("d>>>>>" + d)
      println("dsubstring>>>>>" + d + ">>>>>" + d.substring(0, d.lastIndexOf(".")))
      val year = d.substring(d.lastIndexOf(".") + 1)
      println("year>>>>>" + year)
      val newYear = ".20".concat(year).concat(" 00:00:01")
      println("newYear>>>>>" + newYear)
      val newDate = d.substring(0, d.lastIndexOf(".")).concat(newYear).replaceAll("\"", "")
      println("newDate>>>>>" + newDate)
      newDate
    }

    def toInstant(date: String, formatter: DateTimeFormatter): Instant = {
      println("date>>>>>" + date)
      LocalDate.parse(date, formatter).atStartOfDay(ZoneId.of("Europe/Berlin")).toInstant()
    };

    def parseAmount(amounts: String, s: List[String]) = {
      println("amounts>>>>>" + amounts)
      println("amount>>>>>SSSSSS" + s)
      //BigDecimal(amounts.trim)
      val r = amounts.trim
        .replace("\"", "")
        .replace(".", "")
        .replace(",", ".")
      println("amount>>>>>RRRRRRR" + r)
      val amount = BigDecimal(r)

      println("amountx>>>>>SSSSSS" + amount)
      amount
    }

    def getPF = new getPF1 {
      def apply(data: (List[String], String, String)): List[Either[String, BankStatement]] =
        applyN(data, decode)
      def decode(rawdata: (List[String], String, String)) =
        rawdata._1 match {

          // println("rawdata._1  " + rawdata._1)
          // println(rawdata._1.isInstanceOf[List[String]])
          //val x: String = rawdata._1(0)
          // println("XXXXXX %s" + x)

          //val List(a, b, c, d, e, f, g, h, i, j, k) = x
          /*println("0000 %s" + x.map(_.split(FS).toList))
        println("11111 %s" + rawdata._1(1))
        val a = x._0
        val b = rawdata._1(0)(1)
        val c = rawdata._1(0)(2)
        val d = rawdata._1(0)(3)
        val e = rawdata._1(4)
        val f = rawdata._1(5)
        val g = rawdata._1(6)
        val h = rawdata._1(7)
        val i = rawdata._1(8)
        val j = rawdata._1(9)
        val k = rawdata._1(10)


DE22480501610043719244,
12.15.17,
12.15.17,
FOLGELASTSCHRIFT,
MREF+048owe.1718.ECRED+DE91BAS00000402821SVWZ+Essen 12.17 GS Wellensiek Offene GanztagsgrundschuleABWA+BAS gGmbH,
BAS Betreuung an Schulen gemeinn�tzige GmbH,
DE32480501610000079152,
SPBIDE3BXXX,
-50,EUR,
Umsatz gebucht

        //val List(a, b, c, d, e, f, g, h, i, j, k) = m
        val v: List[String] = rawdata._1
        v.foreach(println(_))
        val List(a, b, c, d, e, f, g, h, i, j, k) = rawdata._1
        println("%s %s %s  %s  %s %s %s %s %s %s  ".format(a, b, c, d, e, f, g, h, i, j, k))
           */
          case List(a, b, c, d, e, f, g, h, i, j, k) =>
            val bx =
              BankStatement(
                0,
                a.trim,
                toInstant(addYear(b), formatter),
                toInstant(addYear(c), formatter),
                d.trim,
                e.trim,
                f.trim,
                g.trim,
                h.trim,
                parseAmount(i.trim, rawdata._1),
                j.trim,
                k.trim,
                rawdata._2,
                rawdata._3
              )
            List(Right(bx))

          case List(a, b, c, d, e, f, g, h, i, j) =>
            //println("%s %s %s  %s  %s %s %s %s %s %s  ".format(a, b, c, d, f, g, h, i, j));
            try {
              val bx =
                BankStatement(
                  0,
                  a.trim,
                  toInstant(addYear(b), formatter),
                  toInstant(addYear(c), formatter),
                  d.trim,
                  e.trim,
                  f.trim,
                  g.trim,
                  h.trim,
                  parseAmount(h.trim, rawdata._1),
                  i.trim,
                  j.trim,
                  rawdata._2,
                  rawdata._3
                )
              List(Right(bx))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println("%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s".format(a, b, c, d, f, g, h, i, j));
                List(Left(ex.getMessage))
              }
            }
          /*
          case List(a, b, c, d, e, f, g, h, i, j, k) =>
            println("%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s %s %s".format(a, b, c, d, f, g, h, i, j, k));
            try {
              val bx =
                BankStatement(
                  0,
                  a.trim,
                  b.trim,
                  c.trim,
                  d.trim,
                  e.trim,
                  f.trim,
                  g.trim,
                  h.trim,
                  i.trim,
                  j.trim,
                  k.trim,
                  rawdata._2,
                  rawdata._3
                )
              List(Right(bx))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println("%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s".format(a, b, c, d, f, g, h, i, j, k));
                List(Left(ex.getMessage))
              }
            }
          //case a :: b :: c :: d :: e :: f :: g :: h :: i :: j :: k :: Nil =>
          //  printf("x >>>>>> %s", rawdata._1)
          case Nil => {
            printf("x default >>>>>> %s")
            List(Left("default".toString))
          }
          /* case _ => {
            printf("x default >>>>>> ")
            List(Left("default".toString))
          }

           */
        }
           */
          case _ => {
            printf("x default >>>>>> ")
            List(Left("default".toString))
          }
        }
    }

  }
}
