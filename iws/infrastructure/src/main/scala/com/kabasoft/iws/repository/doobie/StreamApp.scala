package com.kabasoft.iws.repository.doobie
import com.kabasoft.iws.domain.BankStatement

import java.nio.file.{Files, Paths}
import zio._
import zio.blocking.Blocking
import zio.stream._

object StreamApp extends zio.App {
  val HEADER= "Auftragskonto"
  val CHAR = "\""
  val extension = ".CSV"
  val path = "/Users/iwsmac/Downloads/import/bankStatement/43006329/"


   def getStreamFromPath(path:String, build:String =>BankStatement):ZStream[Blocking, Throwable, BankStatement] = ZStream
    .fromJavaStream(Files.walk(Paths.get(path)))
    .filter(p => !Files.isDirectory(p) && p.toString.endsWith(extension))
    .flatMap { files =>
      ZStream
        .fromFile(files)
        .transduce(ZTransducer.utf8Decode >>> ZTransducer.splitLines)
        .filterNot(p => p.replaceAll(CHAR, "").startsWith(HEADER))
        .map(p=>build(p.replaceAll(CHAR, "")))
    }

  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    getStreamFromPath(path, BankStatement.from).runDrain.exitCode
}
