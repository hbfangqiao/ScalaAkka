package prac3

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorSystem, Inbox, Props}
import prac3.StudentActor.Greeting

import scala.concurrent.duration.Duration

/**
  * Created by FQ on 2017/11/3.
  *
  *当需要在Actor系统之外与Actor通信的时候，可以使用Inbox与Actor通信
  */
object StudentActor {
  def props = Props[StudentActor]
  case class Greeting(word:String)
}

class StudentActor extends Actor {
  override def receive = {
    case Greeting(word) =>{
      Thread.sleep(3000)
      println("receive "+word)
      if (word.equals("good morning")){

        println("发送了hello")
        sender ! Greeting("hello")
      } else {
        println("发送了apple")
        sender ! Greeting("apple")
      }
    }
  }
}

//Actor 和 inbox 都有一个邮箱，例子中 inbox 先向 Actor发送了两个消息，Actor需要一个一个处理，处理好了是发送到inbox的邮箱
//inbox 的receive方法是从自己邮箱中去读取消息。当里面一有邮件了就读取出来
object Client extends App{
  val system : ActorSystem = ActorSystem("inbox")
  val student = system.actorOf(StudentActor.props)
  val inbox = Inbox.create(system)
  inbox watch student

  inbox send(student,Greeting("good morning"))
  inbox send(student,Greeting("good afternoon"))

  //这个方法会阻塞，如果在指定的时间内没有收到消息，会抛出java.util.concurrent.TimeoutException
  val recieveMsg = inbox receive(Duration(4,TimeUnit.SECONDS))
  println("执行到这里了")
  val recieveMsg2 = inbox receive(Duration(4,TimeUnit.SECONDS))
  println("recieve: "+recieveMsg+recieveMsg2)
}
