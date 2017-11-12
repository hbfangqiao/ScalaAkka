package prac11.server2

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, MemberRemoved, MemberUp, UnreachableMember}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import prac11.{BackendRegistration, JobFailed, TransformationJob}

import scala.concurrent.duration._

/**
  * Created by fangqiao on 2017/11/12.
  */
class TransformationFrontend extends Actor{

  var backends = IndexedSeq.empty[ActorRef]
  var jobCounter = 0

  val cluster = Cluster(context.system)

  override def preStart(): Unit = cluster.subscribe(self,classOf[MemberUp],classOf[MemberEvent], classOf[UnreachableMember],classOf[MemberRemoved])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive = {
    case UnreachableMember(member) =>
      println("Member detected as unreachable: {}", member)
      backends = backends.filterNot(_.path.toString.contains(member.address.toString))
    case job: TransformationJob if backends.isEmpty =>
      sender ! JobFailed("Service unavailable, try again later",job)
    case job: TransformationJob =>
      jobCounter += 1
      println(backends)
      backends(jobCounter % backends.size) forward job
    case BackendRegistration if !backends.contains(sender) =>
      context watch sender
      backends = backends :+ sender
    case Terminated(a) =>
      //当集群中的节点关闭，或者移除时Death watch 生成这个消息，并向 watch的Actor发送这个消息
      backends = backends.filterNot(_ == a)
    case other => println(other)
  }
}

object TransformationFrontend {
  def main(args: Array[String]): Unit = {
    // Override the configuration of the port when specified as program argument
//    val port = if (args.isEmpty) "0" else args(0)
//    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
//      withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]")).
//      withFallback(ConfigFactory.load("server1_prac11_frontend.conf"))

    println("start")
    val system = ActorSystem("ClusterTest",ConfigFactory.load("server2_prac11_frontend.conf"))
    val frontend = system.actorOf(Props[TransformationFrontend],"frontend")
    println("running")
    val counter = new AtomicInteger
    import system.dispatcher
    system.scheduler.schedule(2 seconds,2 seconds){
      implicit val timeout = Timeout(5 seconds)
      (frontend ? TransformationJob("hello-" + counter.incrementAndGet())) onComplete  {
        case result => println(result)
      }
    }
  }
}