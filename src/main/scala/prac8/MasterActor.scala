package prac8

import akka.actor.{Actor, Props, Terminated}
import akka.routing._
import prac8.Worker.WorkMsg

/**
  * Created by FQ on 2017/11/6.
  */
class MasterActor extends Actor{
  var router = {
    val routees = Vector.fill(5){
      val worker = context.actorOf(Props[Worker])
      //监视子Actor可以在子Actor被Terminated的时候替换子Actor
      context watch worker
      ActorRefRoutee(worker)
    }
    //RoundRobinRoutingLogic 轮询：子Actor依次接收消息  好比25个消息，会让Actor排队领取
    //RandomRoutingLogic 随机：消息随机发送给子Actor
    //SmallestMailboxRoutingLogic 空闲：消息将发给邮箱中消息数量最小的Actor
    //BroadcastRoutingLogic 广播：每个消息将发给每一个子Actor

    //注意：勿在将BalancingPool用于路由时使用BroadcastRoutingLogic
    Router(BroadcastRoutingLogic(),routees)
  }


  def receive = {
    case WorkMsg =>
      router.route(WorkMsg, sender())
    case Terminated(a) =>
      //在子Actor被终止时，因为watch了 父Acotr会收到Teminated的消息，从路由器中移除这个Actor，并新建一个替换掉
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[Worker])
      context watch r
      router = router.addRoutee(r)
  }
}
