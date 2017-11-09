package prac9

import akka.actor.Actor

/**
  * Created by FQ on 2017/11/9.
  */
class EventHandlerActor extends Actor{
  override def receive = {
    case s => println("handler"+s)
  }
}
