package prac9

import akka.persistence.{PersistentActor, SnapshotOffer}

/**
  * Created by FQ on 2017/11/9.
  */
case class Cmd(data: String)//命令
case class Evt(data: String)//事件

case class ExampleState(events: List[String] = Nil){
  def updated(evt: Evt): ExampleState = copy(evt.data :: events)
  def size: Int = events.length

  override def toString: String = events.reverse.toString
}

//persistentActor 的状态通过一系列 记录在ExampleState中的List 中的Evt来表示
class ExamplePersistentActor extends PersistentActor{
  //数据恢复时会通过这个Id去查找保存自身状态的文件
  override def persistenceId: String = "Simple_Id_3"

  var state = ExampleState()

  def updateState(event: Evt): Unit = state = state.updated(event)

  def numEvents = state.size

  //定义了 通过 Evt 和 SnapshotOffer 的恢复方式
  override def receiveRecover: Receive = {
    case evt: Evt => updateState(evt)
    case SnapshotOffer(_,snapshot: ExampleState) => state = snapshot
  }

  val snapShotInterval = 1000

  override def receiveCommand: Receive = {
    case Cmd(data) =>
//      根据Cmd生成一个 Evt在这里被持久化，persist第二个参数是处理event的Handler
//      persist方法异步执行，event handler只会在Evt被成功持久后执行，Evt成功持久后会回复给PersistentActor一个内部消息来触发
//      eventHandler。eventHandler可能会覆盖PersistentActor的状态，并改变它。Cmd的sender和eventHandler中的sender是同一个。
//      所以可以在eventHandler中给Cmd的sender回复消息
      persist(Evt(s"${data}-${numEvents}")){ event =>
        //这里是eventHandler，主要职责是通过event来 1.更新actor的内部状态
        updateState(event)
        //2.通过publish 通知其他人自己状态改变
        context.system.eventStream.publish(event)
        if(lastSequenceNr % snapShotInterval == 0 && lastSequenceNr != 0)
          saveSnapshot(state)
      }
    case "print" => println(state)
    case "snap" => saveSnapshot(state)
  }


}





















