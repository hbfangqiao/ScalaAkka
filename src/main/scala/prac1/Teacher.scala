package prac1

import akka.actor.{Actor, ActorLogging, Props}

/**
  * Created by FQ on 2017/10/28.
  */
class Teacher extends Actor with ActorLogging{


  override def preStart(): Unit = {
    val student = context.actorOf(Student.props(),"student")
    student.tell(Greeting("hello xiaoming"),self)
  }

  override def receive: Receive = {
    case Greeting(word) =>
      log.info("receive:{} form {}",word,sender().path)
    case _ => log.info("can't understand")

  }
}

object Teacher {
  def props() : Props = Props[Teacher]
}
