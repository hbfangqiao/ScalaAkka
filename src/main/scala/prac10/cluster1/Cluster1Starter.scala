package prac10.cluster1

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * Created by fangqiao on 2017/11/11.
  */
object Cluster1Starter extends App{
  println("start")
  val system = ActorSystem("ClusterTest",ConfigFactory.load("cluster.conf"))
  system.actorOf(Props[SimpleClusterListener],"simpleClusterListener")
  println("running")
}
