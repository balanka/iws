package com.kabasoft.iws.repository.doobie
import java.nio.file.{Files, Paths}

import zio._
import zio.console._
import zio.stream._
//import zio.stream.internal.Utils

object StreamApp extends zio.App {
  val extension = ".csv"
  val file = "/Users/iwsmac/Downloads/import/bankStatement/43719244/2016-2017-43719244-umsatz.csv"
  val path = "/Users/iwsmac/Downloads/import/bankStatement/43719244/"
  val out = "/Users/iwsmac/Downloads/import/bankStatement/test/f.txt"
  val stream = ZStream
    .fromFile(Paths.get(file))
    .transduce(ZTransducer.utf8Decode >>> ZTransducer.splitLines)
    .take(10)
    .tap(putStrLn(_))
    .runDrain
  def transform(s: String) = {
    var ret = s
    println("XXXXX" + s)
    println("XXXXX" + s.indexOf("\""))
    if (s.indexOf("\"") >= 0) {
      val r = s.substring(s.indexOf("\""), s.lastIndexOf("\"") + 1)
      println(s"r><<<<<<<<<<<<<<<<< ${r} ")
      val r2 = r.replaceAll("\"", "").replaceAll(",", " ")
      println(s"r2><<<<<<<<<<<<<<<<< ${r2} ")

      ret = s.replaceFirst(r, r2)
      println(s"><<<<<<<<<<<<<<<<< ${ret} ")
    }
    ret
  }

  val streamOfFileNames = ZStream
    .fromJavaStream(Files.walk(Paths.get(path)))
    .filter(p => !Files.isDirectory(p) && p.toString().endsWith(extension))
    //.filter(p => p.endsWith(extension))
    //.tap(f => putStrLn(s"> ${f} "))
    .flatMap { files =>
      ZStream
        .fromFile(files)
        //.tap(data => putStrLn(s"> ${data} >>> ${files}"))
        .transduce(ZTransducer.utf8Decode >>> ZTransducer.splitLines)
        //.intersperse(("\n"))
        //.mapConcatChunk(identity)
        .map(transform(_))
        .take(2)
        .tap(data => putStrLn(s"> ${data}"))

    //.mapConcatChunk(line=>Chunk.fromArray(line.getBytes("UTF-8")))
    //.toInputStream
    //.use{ is=>
    // blocking.effectBlocking(
    //   Files.newOutputStream(Paths.get(out)))
    //}
    //.take(10)
    //.tap(putStrLn(_))
    }
    .runDrain

  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    streamOfFileNames.exitCode
}
