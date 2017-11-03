package prac2

import akka.actor.{ActorSystem, FSM, Props, Timers}
import prac2.CakeMachine._
import prac2.CakeProtocol._
import prac2.SenderActor.CanCakeMsg

import scala.concurrent.duration._

/**
  * Created by FQ on 2017/10/30.
  */
object CakeMachine {
  def prop : Props = Props[CakeMachine]
  //蛋糕机的所有状态
  sealed trait CakeState
  case object On extends CakeState
  case object Off extends CakeState
  case object Caking extends CakeState
  //共享数据区，蛋糕机的电量，以及当前蛋糕的完成程度
  case class CMData(var energy: Int,var complation: Int)
  //定时器的Key
  private case object CakingKey
}

object CakeProtocol {
  // 蛋糕机所接受的消息
  sealed trait CakeMsg //外部的消息
  sealed trait CakeInnerMsg //蛋糕机内部运转的消息
  sealed trait OtherMsg //其他消息

  case object CakeOffMsg extends CakeMsg //关闭蛋糕
  case object CakeOnMsg extends CakeMsg //打开蛋糕机
  case object CakeWorkMsg extends CakeMsg //制作蛋糕

  case object InnerStartWorkMsg extends CakeInnerMsg //开始做蛋糕
  case object InnerStopWorkMsg extends CakeInnerMsg //停止做蛋糕
  case object InnerCakingMsg extends CakeInnerMsg //进行制作蛋糕每次增加完成度20%

  case object HitMsg extends OtherMsg
  case object TransFormMsg extends OtherMsg

}

class CakeMachine(val actorSystem: ActorSystem) extends FSM[CakeState,CMData] with Timers{
  startWith(Off,CMData(100,0))
  when(Off){
    case Event(CakeOnMsg,CMData(energy,_)) if (energy > 0) => {
      println("蛋糕机启动，电量还有："+energy)
      goto(On)
    }
    case Event(CakeOffMsg,CMData(energy,_)) => println("蛋糕机本来就是关着的，不要再按啦");stay()
    case Event(CakeWorkMsg | StateTimeout,CMData(energy,_)) => println("请先打开蛋糕机");stay()
  }

  //stateTimeout 声明状态超时，如果处于On状态，并且3秒内没有收到任何消息，则会生成一个消息发送给这个状态机
  when(On,stateTimeout = 5 second){
    case Event(CakeOnMsg,CMData(energy,_)) => {
      println("蛋糕机已经打开，请制作蛋糕吧")
      //可以在任何时候更改超时设置,将原本5秒的超时发送时间，设置为了1秒
      setStateTimeout(On,Option(3 second))
      stay
    }
    //处于On状态，在规定时间内没有收到任何消息，则会给自身发送一个CakeOffMsg
    case Event(CakeOffMsg | StateTimeout,CMData(energy,_)) => {
      println("蛋糕机关闭了")
      //forMax()为下一个状态设置，状态超时。这里为Off状态设置了一个5秒的状态超时，但在这5秒内收到了任何其他的消息，
      // 这个状态超时将被取消
      //replying(msg:Any) 给处理CakeOffMsg 发送一个回复，就是切换状态后，马上给自己发送一个消息
      goto(Off).forMax(5 seconds) replying(CakeOffMsg)
    }
    case Event(CakeWorkMsg,CMData(energy,_)) => {
      if (energy >= 20) {
        println("正在制作蛋糕消耗电量20点")
        goto(Caking)
      }else{
        println("电量不够，当前电量"+energy+"点")
        stay()
      }
    }
  }



  when(Caking){
    //data是当前状态机的共享数据，如果30秒内没有收到消息，则会生成一个CakeStopWorkMsg
    case Event(InnerStopWorkMsg,data :CMData ) => println("正在工作无法关闭 data:"+data);goto(On)
    case Event(InnerStartWorkMsg,CMData(energy,complation)) => {
      println("开始做蛋糕了")
      //每隔一秒钟向自身发送InnerReduceEneryMsg
      timers.startPeriodicTimer(CakingKey,InnerCakingMsg,1 second)
//      setStateTimeout(Caking,Option(1 second))
      stay
    }
    case Event(InnerCakingMsg,CMData(energy,complation)) => {
      val remaindEnergy = energy - 2
      val cakeComplation = complation + 20
      if (cakeComplation >= 100){
        timers.cancel(CakingKey)
        println("制作中完成度:"+cakeComplation+"% 当前电量:"+remaindEnergy+"%")
        goto(On).using(CMData(remaindEnergy,0))
      }else{
        println("制作中完成度:"+cakeComplation+"% 当前电量:"+remaindEnergy+"%")
        stay().using(CMData(remaindEnergy,cakeComplation))
      }

    }

  }



  onTransition {
    case On -> Caking => {
      println("从On转换到Caking状态啦")
      self ! InnerStartWorkMsg
    }
    case Caking -> On => {
      println("从Caking转换到On状态了")
      //告诉使用者可以继续做蛋糕了————通过路径获取其他ActorSelection
      val sender  = actorSystem.actorSelection("akka://cake/user/sender")
      sender ! CanCakeMsg
    }
    case On -> Off => {
      println("从On到Off状态了")
    }
    case _ -> Off => {
      println("在startWith方法后调用initialize会到 初始状态了")
    }
      //转换之前的数据，goto.use 和 中的数据在转换之后才生效
      stateData match {
        case CMData(energy,_) => println("转换前电量"+energy+"%")
      }
      //转换之后的数据
      nextStateData match {
        case CMData(energy,_) => println("剩余电量"+energy+"%")
      }
  }

  whenUnhandled{
    //所有状态共同需要处理的消息
    case Event(HitMsg,data)=> {
      println("蛋糕机损坏了")
      goto(Off)
    }
    //状态机无法处理的消息
    case Event(event,stateData) => {
      println("收到不能处理的消息:"+event+" 当前状态:"+stateName+" 当前数据:"+stateData)
      stay
    }
  }

  //执行转换到初始状态，如果有需要，可以在转换中设置定时器
  initialize()

}
