package prac6

import akka.actor.{ActorSystem, Props}

/**
  * Created by FQ on 2017/11/4.
  *
  * 自定义Actor的Dispatcher
  */
object Client extends App{
  val system = ActorSystem("fang")

  //1.在resources中的application.conf中定义my-dispatcher的相关信息
  //2.在代码中创建Actor的时候使用自定义的dispathcer
  def test1(): Unit ={
    val selfDefineDispatcherActor =
      system.actorOf(Props[SelfDefineDispatcherActor].withDispatcher("my-dispatcher"),"myActor")
    print("self define dispathcer success")
  }

//-------------------------------*阻塞操作的问题*-------------------------------------
  //需要小心得管理阻塞，这个例子演示了给blockingFutureActor发送消息，这是个阻塞消息，
  // 最终会导致非阻塞的printerActor饿死，影响系统正常运行
  def test2(): Unit = {
    val system = ActorSystem("block")
    val blockingFutureActor = system.actorOf(Props(new BlockingFutureActor))
    val printerActor = system.actorOf(Props(new PrinterActor))

    for (i <- 1 to 10000) {
      blockingFutureActor ! i
      printerActor ! i
    }
  }
//  test2()

  //需要为带有阻塞操作的Actor单独配置一个，Dispather
  def test3(): Unit = {
    val system = ActorSystem("block")
    val separateDispatcherFutureActor = system.actorOf(Props(new SeparateDispatcherFutureActor))
    val printerActor = system.actorOf(Props(new PrinterActor))

    for (i <- 1 to 10000) {
      separateDispatcherFutureActor ! i
      printerActor ! i
    }
  }

  test3()
}
