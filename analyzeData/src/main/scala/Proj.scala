import java.util.Calendar

import AnalyzedMovie.AnalyzedMovie
import Movie.Movie
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.spark.{SparkConf, SparkFiles}
import play.api.libs.json._

import scala.collection.Map

object Proj extends App {
  def stringToMovie(str: String) : Option[Movie] = Json.parse(str).validate[Movie] match {
    case JsError(e) =>
      println(e);
      None
    case JsSuccess(t, _) => Some(t)
  }

  def analyzeMovie(movie: Movie): AnalyzedMovie = {
    new AnalyzedMovie(movie)
  }

  def                                                                                       sendMessage(x: String, producer: KafkaProducer[String, String]) = {
    val message = new ProducerRecord[String, String]("AnalyzedData", null, x)
    producer.send(message)
  }

  def getStream() = {
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream2",
      //    "auto.offset.reset" -> "latest",
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val conf = new SparkConf()
      .setAppName("Wordcount")
      .setMaster ("local[*]")

    val scriptFile = "script.py"
    val ssc = new StreamingContext(conf, Seconds(2))
    ssc.checkpoint("checkpoint")
    ssc.sparkContext.addFile(scriptFile)
    ssc.sparkContext.addFile("payload.pickle")

    val topics = Array("test")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    stream.map(record => (record.key, record.value))
    stream.foreachRDD { rdd =>
//      println(Calendar.getInstance().getTime + " : Analyzed : " + rdd.count() + " elemts")
      // Calling the python script to analize the data
      val pipeRDD = rdd.map(x => x.value()).pipe(SparkFiles.get(scriptFile))
      pipeRDD.foreachPartition(partition => {
        // Initializing Kafka producer for the partition
        val props = new java.util.HashMap[String, Object]()
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
          "org.apache.kafka.common.serialization.StringSerializer")
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
          "org.apache.kafka.common.serialization.StringSerializer")
        val producer = new KafkaProducer[String, String](props)
        print(Calendar.getInstance.getTime + " Analyzing: ")
        partition.foreach {
          // Handling every record
          case movieConsumerRecord: String =>
            print(".")
            sendMessage(movieConsumerRecord, producer)
        }
        println()
        producer.flush()
        producer.close()
      })
      //      val sqlContext = new org.apache.spark.sql.SQLContext(ssc.sparkContext)
      //      val rec = sqlContext.read.json(rdd.map(x => x.value()))
      //      rec.rdd.saveAsTextFile("record")
    }


    println("starting")
    ssc.start()
    println("awaiting")
    ssc.awaitTermination()
    println("terminated")
  }

  getStream()
}
