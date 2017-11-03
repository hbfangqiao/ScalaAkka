package prac4

import akka.actor.ActorSystem
import prac4.SteamedBunActor.Eat

/**
  * Created by FQ on 2017/11/3.
  *
  * 监控另外一个Actor的被终止（不是重启）
  */
object Client extends App{
  val system: ActorSystem = ActorSystem("food")
  val steamedBun = system.actorOf(SteamedBunActor.props,"steamedBun")
  val children = system actorOf(ChildrenActor.props(steamedBun),"children")
  steamedBun ! Eat
}
