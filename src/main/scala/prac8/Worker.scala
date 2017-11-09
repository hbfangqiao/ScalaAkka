package prac8

import akka.actor.Actor
import akka.io.Udp.SO.Broadcast
import prac8.Worker.WorkMsg

/**
  * Created by FQ on 2017/11/6.
  *
  * 作为Routee的Actor，接受路由消息
  */
object Worker{
  case object WorkMsg
}

class Worker extends Actor{
  override def receive = {
    case WorkMsg => {
      println("["+Thread.currentThread().getName+"]" +self.path+" is Working now")
      //将路由器设置为发件人
      sender().tell("i am working",context.parent)
    }
    case s => println(s)
  }
}
