package prac2

import akka.actor.FSM.{Failure, Normal,Shutdown}
import akka.actor.{ActorSystem, FSM, Props}
import prac2.SwitchMachine._
import prac2.SwitchProtocol._

/**
  * Created by FQ on 2017/11/1.
  */
object SwitchMachine {
  sealed trait SwitchState
  case object On extends SwitchState
  case object Off extends SwitchState
  case object Broken extends SwitchState

  case class SDate(var pushTime:Int)
}
object SwitchProtocol {
  sealed trait AllMsg
  case object PressMsg extends AllMsg
  case object CloseMsg extends AllMsg
  case object HitMsg extends AllMsg
}
class SwitchMachine extends FSM[SwitchState,SDate]{

  //当When的case需要做通用的逻辑时，可以定义一个如下的PartialFunction
  val brokenTrigger: PartialFunction[State, State] = {
    //当按压次数大于5之后，开关坏掉
    case FSM.State(state, data, timeout, stopReason, replies) if data.pushTime > 5 =>
      goto(Broken)
    //无论在什么状态，什么case都会进入这里，做记录
    case FSM.State(state, date, timeout, stopReason, replies) => {
      println("已经使用"+date.pushTime+"次")
      goto(state).using(date)
    }
  }

  startWith(Off,SDate(0))

  when(Off)(transform{
    case Event(PressMsg,SDate(time)) => {
      println("打开开关")
      goto(On).using(SDate(time + 1))
    }
  } using(brokenTrigger) )


  when(On)(transform{
    case Event(PressMsg,SDate(time)) => {
      println("关闭开关")
      goto(Off).using(SDate(time+1))
    }
  } using(brokenTrigger))

  when(Broken){
    case Event(PressMsg,date) => println("已经坏了");stay()
    case Event(CloseMsg,_) => {
      //以Normal关闭
//      stop()
      //以Shutdown关闭
      stop(Shutdown)
      //会记录一次ERROR的信息
//      stop(Failure("坏掉了"))
    }
  }

  onTermination{
    case StopEvent(FSM.Normal,state,date) => println("寿命已到")
    case StopEvent(FSM.Shutdown,state,date) => println("被关闭了")
    case StopEvent(FSM.Failure(cause),state,date) => println("异常关闭原因"+cause)
  }

  override def postStop(): Unit = {
    super.postStop()
    println("Post被关闭了")
  }
}

object Starter extends App {
  val system : ActorSystem = ActorSystem("room")
  val switch = system.actorOf(Props[SwitchMachine],"switch")

  switch ! PressMsg
  switch ! PressMsg
  switch ! PressMsg
  switch ! PressMsg
  switch ! PressMsg
  switch ! PressMsg
  switch ! PressMsg
  switch ! CloseMsg
}