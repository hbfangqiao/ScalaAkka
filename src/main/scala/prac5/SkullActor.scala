package prac5

import akka.actor.{Actor, Props}
import prac5.SkullActor.{AtackMsg, ExcepMsg, KillMsg}

/**
  * Created by FQ on 2017/11/3.
  *
  * 骷髅可以被杀死，但会被被女巫召唤
  */
object SkullActor {
  def props = Props[SkullActor]

  case object KillMsg
  case object AtackMsg
  case object ExcepMsg
}

class SkullActor extends Actor{


  override def preStart() = {
    println("preStart 骷髅"+this.hashCode()+"出现了")
  }

  override def postStop() = {
    println("postStop 骷髅"+this.hashCode()+"被打散了")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    super.preRestart(reason, message)
    //这个是在老的Actor上调用的
    println("preRestart 新的骷髅就要重生，这个骷髅"+this.hashCode()+"死掉了")
  }

  override def postRestart(reason: Throwable): Unit = {
    super.postRestart(reason)
    //这个是在新的Actor上调用的
    println("postRestart 新的骷髅"+this.hashCode()+"重生了")

  }

  override def receive = {
    case KillMsg => {
      println("被干掉了")
      context.stop(self)
    }
    case ExcepMsg => 1 / 0
    case AtackMsg => println("骷髅"+this.hashCode()+"发起攻击")
  }
}
