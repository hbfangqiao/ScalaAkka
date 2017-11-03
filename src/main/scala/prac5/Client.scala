package prac5

import akka.actor.ActorSystem
import prac5.SkullActor.{AtackMsg, ExcepMsg, KillMsg}
import prac5.WitchActor.CallSkull

/**
  * Created by FQ on 2017/11/3.
  */
object Client extends App{

  /*
  如果没有配置监督，执行默认的监督策略
  ActorInitializationException will stop the failing child actor
  ActorKilledException will stop the failing child actor
  DeathPactException will stop the failing child actor
  Exception will restart the failing child actor
  Other types of Throwable will be escalated to parent actor
   */
  def test1 = {
    val system : ActorSystem = ActorSystem("grave")
    val skull = system.actorOf(SkullActor.props,"skull")
    skull ! ExcepMsg
    skull ! KillMsg
  }

  /*自己制定重启策略*/
  def test2 = {
    val system : ActorSystem = ActorSystem("grave")
    val witch  = system.actorOf(WitchActor.props,"witch")
    witch ! CallSkull
    val skullSelection = system.actorSelection("akka://grave/user/witch/skull")
    skullSelection ! ExcepMsg
    skullSelection ! AtackMsg
    skullSelection ! ExcepMsg
    skullSelection ! AtackMsg
    skullSelection ! ExcepMsg
    skullSelection ! AtackMsg

  }

  test2
}
