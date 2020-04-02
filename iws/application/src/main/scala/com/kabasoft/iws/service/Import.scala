package com.kabasoft.iws.service

import java.io.File
import java.nio.charset.CharsetDecoder

import com.kabasoft.iws.domain._

trait ImportFunction[A <: IWS] {

  def getPF: (List[String]) => List[Either[String, A]]
  def getLines(file: File, decoder: CharsetDecoder, FS: String): List[Either[String, A]] =
    scala.io.Source.fromFile(file)(decoder).getLines.toList.map(_.split(FS).toList).flatMap(getPF)
  def getObjects(
    path: String,
    extension: List[String],
    filter: String,
    FS: String,
    decoder: CharsetDecoder
  ): List[Either[String, A]] =
    getListOfFiles(new File(path), extension).flatMap(getLines(_, decoder, FS))

  def getListOfFiles(dir: File, extensions: List[String]): List[File] =
    dir.listFiles.filter(_.isFile).toList.filter { file =>
      extensions.exists(file.getName.endsWith(_))
    }

  def applyN(rawdata: List[String], f: (List[String] => List[Either[String, A]])): List[Either[String, A]] = f(rawdata)
  trait getPF1 extends PartialFunction[List[String], List[Either[String, A]]] {
    def isDefinedAt(x: List[String]) = !x.isEmpty
  }
}
object ImportFunction {

  type importFuncType[A <: IWS] = (String, List[String], String, String, CharsetDecoder) => List[Either[String, A]]

  def getObjectList[A <: IWS](
    importFunc: importFuncType[A],
    path: String,
    extension: List[String],
    filter: String,
    FS: String,
    decoder: CharsetDecoder
  ): List[A] = {
    val files = importFunc(path, extension, filter, FS, decoder)
    files.collect(
      x =>
        x match {
          case Right(x) => x
        }
    )
  }

  object ImportBankAccount extends ImportFunction[BankAccount] {
    def getPF = new getPF1 {
      def apply(rawdata: List[String]): List[Either[String, BankAccount]] = applyN(rawdata, decode)
      def decode(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i) =>
            try {
              val ba = BankAccount(a.trim, b.trim, c.trim, h.trim)
              List(Right(ba))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println("%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s %s %s".format(a, b, c, d, e, f, g, h, i));
                List(Left(ex.getMessage))
              }
            }
          case y => {
            // println("x>>>>>> %s", y)
            List(Left(y.toString))
          }
        }
    }
  }
  object ImportAccount extends ImportFunction[Account] {
    def getPF = new getPF1 {
      def apply(rawdata: List[String]): List[Either[String, Account]] = applyN(rawdata, decode)
      def decode(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p) =>
            try {
              val ax = Account(
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
                l.trim,
                m.trim,
                n.trim,
                o.trim,
                p.trim
              )
              List(Right(ax))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println(
                  "%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s %s %s"
                    .format(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)
                );
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
  object ImportDetailsFinancialsTransaction extends ImportFunction[DetailsFinancialsTransaction] {
    def getPF = new getPF1 {
      def apply(rawdata: List[String]): List[Either[String, DetailsFinancialsTransaction]] = applyN(rawdata, decode)
      def decode(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i, j, _, _) =>
            try {
              val dx = DetailsFinancialsTransaction(
                a.trim,
                b.trim,
                c.trim,
                d.trim,
                e.trim,
                f.trim,
                g.trim,
                h.trim,
                i.trim,
                j.trim
              )
              List(Right(dx))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println("%s %s %s  %s  %s %s %s %s %s %s".format(a, b, c, d, e, f, g, h, i, j));
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
  object ImportFinancialsTransaction extends ImportFunction[FinancialsTransaction] {
    def getPF = new getPF1 {
      def apply(rawdata: List[String]): List[Either[String, FinancialsTransaction]] = applyN(rawdata, decode)
      def decode(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t) =>
            try {
              val tx = FinancialsTransaction(
                a.trim,
                b.trim,
                c.trim,
                d.trim,
                f.trim,
                h.trim,
                g.trim,
                p.trim,
                m.trim,
                t.trim,
                i.trim,
                o.trim,
                e.trim,
                s.trim
              )
              List(Right(tx))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println(
                  "%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s".format(a, b, c, d, f, h, g, p, m, t, i, o, e, s)
                );
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
  object ImportSupplier extends ImportFunction[Supplier] {
    def getPF = new getPF1 {
      def apply(rawdata: List[String]): List[Either[String, Supplier]] = applyN(rawdata, decode)
      def decode(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r) =>
            try {
              val tx = Supplier(
                a.trim,
                b.trim,
                "",
                c.trim,
                d.trim,
                e.trim,
                f.trim,
                g.trim,
                h.trim,
                i.trim,
                j.trim,
                k.trim,
                n.trim,
                o.trim,
                p.trim,
                q.trim,
                r.trim
              )
              List(Right(tx))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println(
                  "%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s %s %s %s %s"
                    .format(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)
                );
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
  object ImportCustomer extends ImportFunction[Customer] {
    def getPF = new getPF1 {
      def apply(rawdata: List[String]): List[Either[String, Customer]] = applyN(rawdata, decode)
      def decode(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r) =>
            try {
              val tx = Customer(
                a.trim,
                b.trim,
                "",
                c.trim,
                d.trim,
                e.trim,
                f.trim,
                g.trim,
                h.trim,
                i.trim,
                j.trim,
                k.trim,
                n.trim,
                o.trim,
                p.trim,
                q.trim,
                r.trim
              )
              List(Right(tx))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println(
                  "%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s %s %s %s %s"
                    .format(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)
                );
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
            println("x>>>>>> %s", y)
            List(Left(y.toString))
          }
        }
    }

  }
  object ImportBankStatement extends ImportFunction[BankStatement] {
    def getPF = new getPF1 {
      def apply(rawdata: List[String]): List[Either[String, BankStatement]] = applyN(rawdata, decode)
      def decode(rawdata: List[String]) =
        rawdata match {
          case List(a, b, c, d, e, f, g, h, i, j, k) =>
            try {
              val bx =
                BankStatement(a.trim, b.trim, c.trim, d.trim, e.trim, f.trim, g.trim, h.trim, i.trim, j.trim, k.trim)
              List(Right(bx))
            } catch {
              case ex: Exception => {
                ex.printStackTrace();
                println("%s %s %s  %s  %s %s %s %s %s %s  %s %s %s %s".format(a, b, c, d, f, g, h, i, j, k));
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
}
