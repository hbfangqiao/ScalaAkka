package prac9

import akka.actor.{ActorSystem, Props}

/**
  * Created by FQ on 2017/11/9.
  */
object Client extends App{
  val system = ActorSystem("actor-server")
  val eventHandler = system.actorOf(Props[EventHandlerActor])
  system.eventStream.subscribe(eventHandler,classOf[Evt])

  Thread.sleep(2000)
  val persistentActor = system.actorOf(Props[ExamplePersistentActor],"persistent")

  persistentActor ! Cmd("Hello")
  persistentActor ! Cmd("Jack")
  persistentActor ! Cmd("Rose")
  persistentActor ! "snap"
  persistentActor ! Cmd("Re.M")
  persistentActor ! Cmd("Ali")
  persistentActor ! "print"

  Thread.sleep(5000)
  system.terminate()

}
