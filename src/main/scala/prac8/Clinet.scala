package prac8

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{ActorSystem, Kill, OneForOneStrategy, Props}
import akka.routing._
import com.typesafe.config.ConfigFactory
import prac8.SupervionTestActor.ExceptionMsg
import prac8.Worker.WorkMsg

import scala.concurrent.duration._


/**
  * Created by FQ on 2017/11/5.
  */
object Clinet extends App{

  //在Actor内部配置路由————在MasterActor内配置路由功能
  def test1: Unit = {
    val system = ActorSystem("router")
    val master = system.actorOf(Props[MasterActor],"master")
    for(i <- 1 to 10){
      master ! WorkMsg
    }
  }

  //在Actor外部使用路由————方式一：通过Code
  def test2: Unit = {
    val system = ActorSystem("router")

    //RoundRobinPool() 轮询
    //RandomPool() 随机
    //SmallestMailboxPool() 空闲 远程Actor的优先级最低
    //BroadcastPool() 广播

    val master = system.actorOf(RoundRobinPool(5).props(Props[Worker]),"master")

    //ScatterGatherFirstCompletedPool() 分散消息收集第一个结果

//    val router18 = system.actorOf(ScatterGatherFirstCompletedPool(5, within = 10.seconds).
//        props(Props[Worker]), "router18")

    //TailChoppingPool()
    // TailChoppingRouter首先将消息发送给一个随机挑选的routee，然后在一小段延迟之后发送给第二个routee（从剩下的路由中随机挑选）
    // 等等。它等待第一个回复它回来并转发回原始发件人。其他答复被丢弃。
    //该路由器的目标是通过对多个路由执行冗余查询来减少延迟，假定其他参与者之一可能比初始响应更快。
//    val router22 = system.actorOf(TailChoppingPool(5, within = 10.seconds, interval = 20.millis).
//        props(Props[Worker]), "router22")

    for(i <- 1 to 10){
      master ! WorkMsg
    }
  }
//  test2

  //在Actor外部使用路由————方式二：通过配置
  def test3: Unit = {
    val system = ActorSystem("router")
    val master = system.actorOf(FromConfig.props(Props[Worker]),"master")

  }
//  test3

  //路由器的监督策略：
  //默认情况下，如果不配置路由器的监督策略，子Actor会将异常逐级递升，抛送给Router的父Actor，父Actor会restart Router
  //这意味着Router的所有Routee也会被restart
  //注意：路由器的子Acoter终止后，路由器不会新建一个Actor去补充，如果所有的Routee都终止了，那么Router也会被终止
  def test4: Unit = {
    val system = ActorSystem("router")
    val s_stratagy = OneForOneStrategy(){
      case _: ArithmeticException => Restart
      case _: NullPointerException => Resume
      case _: IllegalArgumentException => Stop
      case _: Exception => Escalate
    }

    val master = system.actorOf(RoundRobinPool(1,supervisorStrategy = s_stratagy)
                        .props(Props[SupervionTestActor]))
    for(i <- 1 to 5){
      master ! ExceptionMsg
    }
  }
//  test4

  //在配置中使用Group来创建路由————有时候不希望在创建Router时创建Routee，而是需要分别创建路由
  def test5: Unit = {
    val system = ActorSystem("workers")
    val worker1 = system.actorOf(Props[Worker],"w1")
    val worker2 = system.actorOf(Props[Worker],"w2")
    val worker3 = system.actorOf(Props[Worker],"w3")

    val master = system.actorOf(FromConfig.props(),"master2")
    for(i <- 1 to 10){
      master ! WorkMsg
    }
  }
//  test5

  //在代码中使用Group来创建路由
  def test6: Unit = {
    val system = ActorSystem("workers")
    //从路由器外部创建Actor
    val worker1 = system.actorOf(Props[Worker],"w1")
    val worker2 = system.actorOf(Props[Worker],"w2")
    val worker3 = system.actorOf(Props[Worker],"w3")

    val paths = List("/user/w1","/user/w2","/user/w3")

    //RoundRobinGroup() 轮询
    //RandomGroup() 随机
    //BroadcastGroup 广播
    val master = system.actorOf(RoundRobinGroup(paths).props(), "master3")

    //使用 分散消息收集第一个结果
//    val router20 = system.actorOf(ScatterGatherFirstCompletedGroup(
//        paths,
//        within = 10.seconds).props(), "router20")

    //使用 TailChoppingGroup————解释见test2
//    val router24 =
//      system.actorOf(TailChoppingGroup(
//        paths,
//        within = 10.seconds, interval = 20.millis).props(), "router24")
    for(i <- 1 to 10){
      master ! WorkMsg
    }
  }
//  test6

  //使用BalancePool
  //BalancePool的路由器，将尝试将工作繁忙的Routee中的消息，重新分配给空闲的Routee，所有的Routee共用一个邮箱
  //不要在路由器使用BalancePool时，使用广播消息
  def test7: Unit = {
    val system = ActorSystem("workers")
    //使用BalancePool时，无论身为Routee在Props时使用任何的Dispatcher都将被无视，BalancePool会自动为他们分
    //配一个BalancingDispatcher，虽然无法更改Routee的调度器，但是却可以对他的executor进行调整
    val master = system.actorOf(FromConfig.props(Props[Worker]),"master3")

    //通过代码使用BalancePool————推荐使用配置创建BalancingPool
    val router10 = system.actorOf(BalancingPool(5).props(Props[Worker]), "router10")
    for(i <- 1 to 10){
      master ! WorkMsg
    }
  }
//  test7

  //特殊消息处理————Broadcast消息 再强调一次 注意：使用BalancingPool 不要使用Broadcast消息
  def test8: Unit = {
    //注意：Broadcast消息不经过路由器，直接发送给每个Routee
    val system = ActorSystem("broadcast")
    val master = system.actorOf(FromConfig.props(Props[Worker]),"master")
    //Broadcast将消息包装(这里是"Watch out for Davy Jones' locker"字符串)，然后Router会提取Broadcast包装的
    //消息，发送给每个Routee
    master ! Broadcast("Watch out for Davy Jones' locker")
  }
//  test8

  //特殊消息处理————PoisonPill消息
  def test9: Unit ={
    //所有的Actor收到了PoisonPill都会被立即停止
    //Router收到了PoisonPill不会将他们发送到Routee，但是Router被杀死，他的Routee也将被终止，这个过程中，Routee会处理
    //当前正在处理的消息，然后停止，这会导致一部分消息未被处理，所以应该使用Broadcast消息包裹PoisonPill消息发送给Router

  }

  //Kill消息
  def test10: Unit = {
    //当Kill消息发送到路由器时，路由器会在内部处理消息，而不会将消息发送到路由器。
    //路由器会抛出ActorKilledException并被终止，如何处理这个异常取决于Router的父Actor的监督策略。
    // 当ActorKilledException被抛到ActorSystem，会默认停止这个Actor
    //作为子Actoer的Routee将被暂停，并且会受到Router的父Actor的监督策略的影响，在外部创建的Actor作为Routee时不会受到影响
    val system = ActorSystem("broadcast")
    val master = system.actorOf(FromConfig.props(Props[Worker]),"master")
    master ! Kill
    master ! Kill

    //给路由器发送Kill 间接地杀死了Routee，如果想直接杀死Routee，应该使用BroadCast包裹Kill发送给Router
    master ! Broadcast(Kill)
  }
//  test10

  //管理消息————动态得
  def test11: Unit = {
    val system = ActorSystem("broadcast")
    val master = system.actorOf(FromConfig.props(Props[Worker]),"master")
    val manager = system.actorOf(Props[ManagerActor],"manager")
    //发送GetRoutees消息，Router会回复一条类型为Routees的消息。包含现在Router中所有的Routee
    master.tell(GetRoutees,manager)
    //发送AddRoutee消息，Router会将AddRoutee消息中携带的Routee添加到Router的Routee集合中
    val assist = system.actorOf(Props[Worker],"assist")

    master ! AddRoutee(ActorRefRoutee(assist))
    Thread.sleep(1000)
    master.tell(GetRoutees,manager)

    for(i <- 1 to 10){
      master ! WorkMsg
    }
    //发送RemoveRoutee消息，Router会将携带的Routee从Router的Routee集合中移除
    master ! RemoveRoutee(ActorRefRoutee(assist))
    Thread.sleep(1000)
    master.tell(GetRoutees,manager)
    //发送AdjustPoolSize消息，Router会根据Pool的大小自动调整
    master ! AdjustPoolSize
  }
//  test11

  //定义可以自动调整大小的Pool————基础调整器
  def test12: Unit = {
    val system = ActorSystem("router")
    //配置中定义
    val master = system.actorOf(FromConfig.props(Props[Worker]), "master4")
    //代码中定义
    val resizer = DefaultResizer(lowerBound = 2, upperBound = 15)
    //5个Routee 配置中的配置会默认覆盖代码中的定义
    val master2 = system.actorOf(RoundRobinPool(5,Some(resizer)).props(Props[Worker]));

  }

  //定义可以自动调整大小的Pool————最佳数量探索调整器————提供最大吞吐量
  def test13: Unit = {
    val system = ActorSystem("router")
    //在配置中定义
    val master = system.actorOf(FromConfig.props(Props[Worker]),"master5")
  }

  //在池当中内联定义调度器
  def test14: Unit = {
    val system = ActorSystem("hi",ConfigFactory.load("application.conf"))
    //router 使用priomailboxactor dispatcher
    //routee 使用poolWithDispatcher dispatcher
    val router = system.actorOf(RandomPool(5,routerDispatcher = "priomailboxactor").props(Props[Worker]),name = "poolWithDispatcher")

    println("hello")
  }
//  test14
}
