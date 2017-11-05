package prac7

import akka.actor.Actor
import akka.dispatch.ControlMessage

/**
  * Created by FQ on 2017/11/5.
  */
object PrinterActor{
  sealed trait PrinterMsg
  //用于优先级邮箱
  case object HighPrioMsg extends PrinterMsg
  case object LowPrioMsg extends PrinterMsg
  case object NormalMsg extends PrinterMsg
  //用于控制邮箱
  case object ControlMsg extends ControlMessage
}

class PrinterActor extends Actor{
  def receive = {
    case x => println(x.toString)
  }
}
