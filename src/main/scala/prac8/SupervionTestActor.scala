package prac8

import akka.actor.Actor
import prac8.SupervionTestActor.ExceptionMsg

/**
  * Created by FQ on 2017/11/6.
  *
  * 用于测试路由器的监管功能
  */
object SupervionTestActor {
  case object ExceptionMsg
}

class SupervionTestActor extends Actor{
  override def receive = {
    case ExceptionMsg => {
      println("子Acoter的HashCode："+self)
      1 / 0
    }
  }
}
