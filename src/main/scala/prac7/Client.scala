package prac7

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import com.typesafe.config.Config
import prac7.PrinterActor.{ControlMsg, HighPrioMsg, LowPrioMsg, NormalMsg}

/**
  * Created by FQ on 2017/11/5.
  *
  * 配置邮箱
  */
object Client extends App{

  //没有使用优先级邮箱,消息按顺序一个一个打印
  def test1: Unit ={
    val system = ActorSystem("mail")
    val printer = system.actorOf(Props[PrinterActor],"printer")
    sendPrioMsg(printer)
  }
//  test1

//--------------------------*演示优先级邮箱的使用*--------------------------
  //使用方式一配置邮箱
  //使用了优先级邮箱，高优先级的消息会考虑优先处理
  def test2: Unit = {
    val system = ActorSystem("mail")
    val printer = system.actorOf(Props[PrinterActor].withDispatcher("prio-dispatcher"),"printer")
    sendPrioMsg(printer)
  }
//  test2

  //使用方式二配置邮箱，并通过Actor名字的部署配置，配置邮箱
  def test3: Unit = {
    val system = ActorSystem("mail")
    val printer = system.actorOf(Props[PrinterActor],"priomailboxactor")
    sendPrioMsg(printer)
  }
//  test3

  //使用方式二配置邮箱，直接通过邮箱配置，配置邮箱
  def test4: Unit = {
    val system = ActorSystem("mail")
    val printer = system.actorOf(Props[PrinterActor].withMailbox("prio-mailbox"))
    sendPrioMsg(printer)
  }
//  test4

  //发送优先级消息
  private def sendPrioMsg(printer: ActorRef) = {
    printer ! LowPrioMsg
    printer ! LowPrioMsg
    printer ! LowPrioMsg
    printer ! HighPrioMsg
    printer ! HighPrioMsg
    printer ! HighPrioMsg
    printer ! NormalMsg
    printer ! NormalMsg
    printer ! NormalMsg
    printer ! HighPrioMsg
    printer ! PoisonPill
  }
//----------------------------*演示控制邮箱的使用*--------------------------
//控制邮箱是指，Actor如果收到了控制消息，则无论邮箱中还有多少其他消息，优先处理控制消息

  //使用控制邮箱，ControlMsg会被最优先处理
  def test5: Unit = {
    val system = ActorSystem("mail")
    val printer = system.actorOf(Props[PrinterActor].withDispatcher("control-aware-dispatcher"),"printer")
    printer ! NormalMsg
    printer ! NormalMsg
    printer ! NormalMsg
    printer ! NormalMsg
    printer ! NormalMsg
    printer ! NormalMsg
    printer ! ControlMsg
  }

  test5

  //todo 自定义邮箱
}
