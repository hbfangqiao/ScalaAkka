package prac2

import akka.actor.FSM.SubscribeTransitionCallBack
import akka.actor.{ActorSystem, Props}
import prac2.CakeProtocol.{CakeOnMsg}

/**
  * Created by FQ on 2017/10/30.
  *
  * 演示状态机的使用
  */
object Client extends App{

    val system : ActorSystem = ActorSystem("cake")
    val sender = system.actorOf(SenderActor.prop,"sender")
    //创建Actor并传参
    val cake = system.actorOf(Props(classOf[CakeMachine],system),"cake")
    println(sender.path)
    cake.tell(CakeOnMsg,sender)

    //在外部监听cake的状态，会立即发送一个CurrentState消息给sender，之后的每次状态转换都会
    // 给sender发送一个Transition消息这些实现是在Trait FSM中实现的
    cake ! SubscribeTransitionCallBack(sender)

//    cake.tell(CakeWorkMsg,sender)
//    Thread.sleep(4000)
//    cake.tell(CakeOnMsg,sender)

}
