package prac4

import akka.actor.{Actor, Props}
import prac4.SteamedBunActor.Eat

/**
  * Created by FQ on 2017/11/3.
  *
  * 包子Acoter，被吃掉后，终止，并通知监控者，自己被吃掉的信息
  */
object SteamedBunActor {
  def props = Props[SteamedBunActor]

  case object Eat
}

class SteamedBunActor extends Actor{

  override def receive = {
    case Eat => {
      println("包子被吃掉了")
      context.stop(self)
    }
  }
}
