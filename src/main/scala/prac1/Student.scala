package prac1

import akka.actor.{Actor, ActorLogging, Props}

/**
  * Created by FQ on 2017/10/28.
  */
class Student extends Actor with ActorLogging{

  override def receive: Receive = {
    case Greeting(word) => {
      log.info("student receive: {} form {}",word,sender().path)
      //等同于
      //sender().tell(Greeting("hello teacher"),self)
      sender() ! Greeting("hello teacher")
    }
  }
}

object Student {
  def props(): Props = Props[Student]
}