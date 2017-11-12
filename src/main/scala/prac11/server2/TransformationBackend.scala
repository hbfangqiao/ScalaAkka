package prac11.server2

import akka.actor.{Actor, ActorSystem, Props, RootActorPath}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member, MemberStatus}
import com.typesafe.config.ConfigFactory
import prac11.{BackendRegistration, TransformationJob, TransformationResult}

/**
  * Created by fangqiao on 2017/11/12.
  */
class TransformationBackend extends Actor{

  val cluster = Cluster(context.system)

  override def preStart(): Unit = cluster.subscribe(self,classOf[MemberUp],classOf[MemberEvent], classOf[UnreachableMember],classOf[MemberRemoved])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case TransformationJob(text) =>  {
      println(text)
      sender ! TransformationResult(text.toUpperCase)
    }
    case MemberUp(m) => {
      //当有一个Actor正常运作（UP状态后） 就发送注册信息
      register(m)
      println("Member is Up: {}", m.address)
    }
    case MemberRemoved(member, previousStatus) =>
      println("Member is Removed: {} after {}", member.address, previousStatus)
    case UnreachableMember(member) =>
      println("Member detected as unreachable: {}", member)
    case state: CurrentClusterState =>
      state.members.filter(_.status == MemberStatus.Up) foreach register
  }

  def register(member: Member):Unit =
    if (member.hasRole("frontend"))
      context.actorSelection(member.address.toString + "/user" + "/frontend") ! BackendRegistration
}

object TransformationBackend {
  def main(args: Array[String]): Unit = {
    // Override the configuration of the port when specified as program argument
//    val port = if (args.isEmpty) "0" else args(0)
//    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
//      withFallback(ConfigFactory.parseString("akka.cluster.roles = [frontend]")).
//      withFallback(ConfigFactory.load("server1_prac11_backend.conf"))
    println("start")
    val system = ActorSystem("ClusterTest",ConfigFactory.load("server2_prac11_backend.conf"))
    system.actorOf(Props[TransformationBackend],"backend1")
    println("running")

  }
}