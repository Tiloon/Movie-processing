import java.io.FileWriter
import java.util.{Calendar, Collections, Properties}

import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}

import scala.collection.JavaConverters._

object Persister extends App {
  val TOPIC = "AnalyzedData"
  val outputFile = "output.txt"

  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("group.id", "persister")
//  props.put("auto.offset.reset", "latest")
  props.put("auto.offset.reset", "earliest")

  val consumer = new KafkaConsumer[String, String](props)

  consumer.subscribe(Collections.singletonList(TOPIC))
  while (true) {
    print(Calendar.getInstance.getTime + ": Polling data...")
    val records : ConsumerRecords[String, String] = consumer.poll(5000)
    println(" Found: " + records.count() + " lines")
    if (!records.isEmpty) {
      val fw = new FileWriter(outputFile, true)
      for (record <- records.asScala) {
        val value: String = record.value()
        fw.write(value + "\n")
      }
      fw.flush()
      fw.close()
    }
  }

//  def persist() = {
//    def getStream() = {
//      val ssc = new StreamingContext(conf, Seconds(2))
//      ssc.checkpoint("checkpoint")
//
//      val topics = Array("test")
//      val stream = KafkaUtils.createDirectStream[String, String](
//        ssc,
//        PreferConsistent,
//        Subscribe[String, String](topics, kafkaParams)
//      )
//      stream.map(record => (record.key, record.value))
//      stream.foreachRDD { rdd =>
//         Calling the python script to analize the data
//        val pipeRDD = rdd.map(x => x.value()).pipe(SparkFiles.get(scriptFile))
        // Sending the data to kafka
//        pipeRDD.foreachPartition(partition => {
//           Initializing Kafka producer for the partition
//          val props = new java.util.HashMap[String, Object]()
//          props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
//          props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
//            "org.apache.kafka.common.serialization.StringSerializer")
//          props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
//            "org.apache.kafka.common.serialization.StringSerializer")
//          val producer = new KafkaProducer[String, String](props)
//          partition.foreach {
//             Handling every record
//            case movieConsumerRecord: String =>
//              println(movieConsumerRecord)
//              sendMessage(movieConsumerRecord, producer)
//          }
//          producer.flush()
//          producer.close()
//        })
//      }

//      println("starting")
//      ssc.start()
//      println("awaiting")
//      ssc.awaitTermination()
//      println("terminated")
//    }
//  }
}