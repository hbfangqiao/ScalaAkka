package prac8

import akka.actor.Actor
import akka.routing.Routees

/**
  * Created by FQ on 2017/11/6.
  */
class ManagerActor extends Actor{
  override def receive = {
    case r: Routees => {
      println("收到"+r)
    }
    case s => println(s)
  }
}
