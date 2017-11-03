package prac1

import akka.actor.{ActorSystem}
import com.typesafe.config.ConfigFactory

/**
  * Created by FQ on 2017/10/28.
  *
  * 演示akka的基本使用，以及日志记录
  */
object ClientApp extends App{
  //创建ActorSystem
  val system : ActorSystem = ActorSystem("hello",ConfigFactory.load("application.conf"))
  val teacher = system.actorOf(Teacher.props(),"teacher")
  println(teacher.path)

}
