package prac2

import akka.actor.FSM.{CurrentState, Transition}
import akka.actor.{Actor, Props}
import prac2.CakeProtocol.CakeWorkMsg
import prac2.SenderActor.CanCakeMsg

/**
  * Created by FQ on 2017/10/30.
  */
class SenderActor extends Actor{

  override def receive: Receive = {
    case CanCakeMsg => {
      println("又可以继续做蛋糕啦")
      sender ! CakeWorkMsg
    }
    case CurrentState(fsm,state) => println("蛋糕机:"+fsm+" 当前的状态是"+state)
    case Transition(fsm,oldState,newState) => println("蛋糕机:"+fsm+" 之前的状态是"+oldState+"新状态是:"+newState)
    case _ => println("收到未知消息")
  }
}

object SenderActor {
  def prop : Props = Props[SenderActor]
  case object CanCakeMsg
}