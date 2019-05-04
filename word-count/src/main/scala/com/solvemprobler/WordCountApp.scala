package com.solvemprobler

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time

object WordCountApp extends App {

  case class WordWithCount(word: String, count: Long)

  override def main(args: Array[String]): Unit = {
    // the port to connect to
    val params = ParameterTool.fromArgs(args)

    // get the execution environment
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    env.getConfig.setGlobalJobParameters(params)

    val file = try {
      params.get("file")
    } catch {
      case e: Exception => {
        System.err.println("No file specified.")
        return
      }
    }

    // get input data by connecting to the socket
    val text  = env.readTextFile(file)

    // parse the data, group it, window it, and aggregate the counts
    val windowCounts = text
      .flatMap { w => w.split("\\s") }
      .map { w => WordWithCount(w, 1) }
      .keyBy(x => x.word)
      .timeWindow(Time.seconds(2), Time.seconds(1))
      .sum("count")

    // print the results with a single thread, rather than in parallel
    windowCounts.print().setParallelism(1)

    env.execute("Socket Window WordCount")
  }
}
