package prac10.cluster2

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * Created by fangqiao on 2017/11/11.
  */
object Cluster2Starter extends App{
  println("start")
  val system = ActorSystem("ClusterTest",ConfigFactory.load("cluster2.conf"))
  system.actorOf(Props[SimpleClusterListener2],"simpleClusterListener")
  println("running")
}
