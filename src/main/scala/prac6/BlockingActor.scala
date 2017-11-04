package prac6

import akka.actor.Actor

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by FQ on 2017/11/4.
  */
class BlockingActor extends Actor{
  def receive = {
    case i: Int =>
      Thread.sleep(5000) //block for 5 seconds, representing blocking I/O, etc
      println(s"Blocking operation finished: ${i}")
  }
}

class BlockingFutureActor extends Actor {
  implicit val executionContext: ExecutionContext = context.dispatcher

  def receive = {
    case i: Int =>
      println(s"Calling blocking Future: ${i}")
      Future {
        Thread.sleep(5000) //block for 5 seconds
        println(s"Blocking future finished ${i}")
      }
  }
}

class SeparateDispatcherFutureActor extends Actor {
  implicit val executionContext: ExecutionContext = context.system.dispatchers.lookup("my-blocking-dispatcher")

  def receive = {
    case i: Int =>
      println(s"Calling blocking Future: ${i}")
      Future {
        Thread.sleep(5000) //block for 5 seconds
        println(s"Blocking future finished ${i}")
      }
  }
}