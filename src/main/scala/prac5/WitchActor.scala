package prac5

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, OneForOneStrategy, Props, SupervisorStrategy}
import prac5.WitchActor.CallSkull

import scala.concurrent.duration._

/**
  * Created by FQ on 2017/11/3.
  */
object WitchActor {
  def props = Props[WitchActor]

  case object CallSkull
}

class WitchActor extends Actor{




  override def preStart(): Unit = {
    println("女巫诞生")
  }

  override def postStop(): Unit = {
    println("女巫被干掉了")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println("女巫"+this.hashCode()+"彻底被干掉了")
  }

  override def postRestart(reason: Throwable): Unit = {
    println("新的女巫"+this.hashCode()+"将要重生")
  }

  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10,withinTimeRange = 1 minute){
      case _: NullPointerException => Resume //Resume是将当前的Actor恢复，不重新启动一个新Actor
      case _: ArithmeticException => Restart //
      case _: IllegalArgumentException => Stop //停掉发生异常的Actor
      case _: Exception => Escalate //交给上层的Actor，抛出异常
    }

  override def receive = {
    case CallSkull => {
      val skull = context.actorOf(SkullActor.props,"skull")
      println(skull.path)
    }
    case _ => println("unhandled")
  }
}
