import AnalyzedMovie.AnalyzedMovie
import Movie.Movie
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import play.api.libs.json._

/**
  * Created by tilon on 7/3/17.
  */

object Proj {
  def stringToMovie(str: String) : Option[Movie] = Json.parse(str).validate[Movie] match {
    case JsError(e) =>
      println(e);
      None
    case JsSuccess(t, _) => Some(t)
  }

  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> "localhost:9092",
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "use_a_separate_group_id_for_each_stream",
    "auto.offset.reset" -> "latest",
//    "auto.offset.reset" -> "earliest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )
  val conf = new SparkConf()
    .setAppName("Wordcount")
    .setMaster ("local[*]")

  def analyzeMovie(movie: Movie): AnalyzedMovie = {
    new AnalyzedMovie(movie)
  }

  def sendMessage(x: AnalyzedMovie) = {
    val props = new java.util.HashMap[String, Object]()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String,String](props)
    val message = new ProducerRecord[String, String]("AnalyzedData", null, Json.toJson(x).toString())
    producer.send(message)
  }

  def getStream() = {
    val ssc = new StreamingContext(conf, Seconds(2))
    ssc.checkpoint("checkpoint")

    val topics = Array("test")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    stream.map(record => (record.key, record.value))
    stream.foreachRDD { rdd =>
      println("Printing everything:")
      val analizedMovies: Array[AnalyzedMovie] = rdd.map(x => x.value())
        .flatMap(x => try {
          stringToMovie(x)
        } catch {
          case e : Exception =>
            println(e);
            None
        })
        .map(x => analyzeMovie(x))
        .collect()
      analizedMovies.foreach(x => {
        if (x.commentWords < 10)
          x.comments.foreach(y => println(y))
        println("title: " + x.title + " / words: " + x.commentWords)
        sendMessage(x)
      })
    }

    println("starting")
    ssc.start()
    println("awaiting")
    ssc.awaitTermination()
    println("terminated")
  }
}
