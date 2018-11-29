import java.nio.charset.StandardCharsets
import scala.collection.mutable.ListBuffer

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext, Time}

import org.apache.nifi.remote.client.SiteToSiteClient
import org.apache.nifi.remote.client.SiteToSiteClientConfig
import org.apache.nifi.spark.NiFiReceiver
import org.apache.nifi.spark.NiFiDataPacket

case class TweetRecord(time: Time, candidate: String, count: Integer)


object NifiSpark {
    def apply() = {
        val clientConfig = new SiteToSiteClient.Builder()
                                .url("http://127.0.0.1:9090/nifi")
                                .portName("toSpark")
                                .buildConfig();

        val ssc = new StreamingContext(sc, Seconds(60))

        val tweetStream = ssc.receiverStream(new NiFiReceiver(clientConfig, StorageLevel.MEMORY_ONLY))
                                .map( (packet: NiFiDataPacket) => new String(packet.getContent(), StandardCharsets.UTF_8) )
                                .map {  (tweet: String) =>  var myList = new ListBuffer[(String, Int)]()
                                                            if (tweet.contains("@tedcruz"))
                                                                myList += (("@tedcruz", 1))
                                                            if (tweet.contains("@realDonaldTrump"))
                                                                myList += (("@realDonaldTrump", 1))
                                                            if (tweet.contains("@HillaryClinton"))
                                                                myList += (("@HillaryClinton", 1))
                                                            if (tweet.contains("@BernieSanders"))
                                                                myList += (("@BernieSanders", 1))
                                                            myList.toList
                                }
                                .flatMap( identity )
                                .reduceByKey ( (x,y) => x + y )
                                
        tweetStream.foreachRDD { (rdd: RDD[(String, Int)], time: Time) => rdd.map( t => TweetRecord(time, t._1, t._2) )
                                                                                .toDF()
                                                                                .write
                                                                                .insertInto("tweets") 
                                }

        ssc.start()

    }
}