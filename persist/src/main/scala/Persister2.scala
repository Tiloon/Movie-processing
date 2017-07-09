import java.util.{Calendar, UUID}
import java.util.concurrent.atomic.AtomicInteger

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._

object Persister2 extends App {
  def persist() = {
    val UID = UUID.randomUUID().toString()
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      //    "auto.offset.reset" -> "latest",
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val conf = new SparkConf()
      .setAppName("Wordcount")
      .setMaster ("local[*]")

    val ssc = new StreamingContext(conf, Seconds(2))
    ssc.checkpoint("checkpoint")

    val topics = Array("AnalyzedData")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    val i: AtomicInteger = new AtomicInteger(0)
    stream.foreachRDD { rdd =>
      val json: DataFrame = SparkSession.builder().getOrCreate().read.json(rdd.map(x => x.value()))
      json.rdd.saveAsTextFile("data/" + UID + "/" + i.incrementAndGet())
      println(UID + ": " + Calendar.getInstance.getTime + ": Found: " + rdd.count() + " lines")
    }


    println("starting")
    ssc.start()
    println("awaiting")
    ssc.awaitTermination()
    println("terminated")
  }
  persist()
}
