package prac10.cluster1

import akka.actor.Actor
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

/**
  * Created by fangqiao on 2017/11/11.
  */
class SimpleClusterListener extends Actor{

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case MemberUp(member) =>
      println("Member is Up: {}", member.address)
    case UnreachableMember(member) =>
      println("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      println(
        "Member is Removed: {} after {}",
        member.address, previousStatus)
    case _: MemberEvent => // ignore
  }
}
