package com.kabasoft.iws.repository.doobie
import com.kabasoft.iws.domain.BankStatement

import java.nio.file.{Files, Paths}
import zio._
import zio.stream._

object StreamApp extends zio.App {
  val HEADER= "\"Auftragskonto\""
  val CHAR = "\""
  val extension = ".CSV"
  val file = "/Users/iwsmac/Downloads/import/bankStatement/43006329/20191028-43006329-umsatz.CSV"
  val path1 = "/Users/iwsmac/Downloads/import/bankStatement/43719244/"
  val path = "/Users/iwsmac/Downloads/import/bankStatement/43006329/"
  val out = "/Users/iwsmac/Downloads/import/bankStatement/test/f.txt"
  val stream = ZStream
    .fromFile(Paths.get(file))
    .transduce(ZTransducer.utf8Decode >>> ZTransducer.splitLines)
    .filterNot(p => p.startsWith(HEADER))
    .take(10)
    //.tap(putStrLn(_))
    .runDrain
  def transform(s: String) = BankStatement.from(s)


  val streamOfFileNames = ZStream
    .fromJavaStream(Files.walk(Paths.get(path)))
    .filter(p => !Files.isDirectory(p) && p.toString().endsWith(extension))
    .flatMap { files =>
      ZStream
        .fromFile(files)
        .transduce(ZTransducer.utf8Decode >>> ZTransducer.splitLines)
        .filterNot(p => p.startsWith(HEADER))
        .map(x=> x.replaceAll(CHAR, ""))
        .map(x=>transform( x))
    }
    .runDrain

  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    streamOfFileNames.exitCode
}
