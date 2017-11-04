package prac6

import akka.actor.Actor

/**
  * Created by FQ on 2017/11/4.
  */
class PrinterActor extends Actor{
  def receive = {
    case i: Int =>
      println(s"PrintActor: ${i}")
  }
}
