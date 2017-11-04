package prac6

import akka.actor.Actor

/**
  * Created by FQ on 2017/11/4.
  */
class SelfDefineDispatcherActor extends Actor{
  override def receive = {
    case "hello" => println("world")
  }
}
