package prac4

import akka.actor.{Actor, ActorRef, Props, Terminated}

/**
  * Created by FQ on 2017/11/3.
  *
  * 小孩监控包子，当包子被吃了之后，获得通知
  */
object ChildrenActor{
  def props(actor: ActorRef) = Props.create(classOf[ChildrenActor],actor: ActorRef)
}

class ChildrenActor(val steamedBun: ActorRef) extends Actor{

  context.watch(steamedBun)

  override def receive = {

    case Terminated(baba) => {
      println("Children发现包子"+baba.path+ "被吃掉啦")
      //关闭系统
      context.system.terminate()
    }
  }
}
