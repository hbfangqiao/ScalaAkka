package prac11.server1

/**
  * Created by fangqiao on 2017/11/12.
  */
object TransformtionApp {
  def main(args: Array[String]): Unit = {
    // starting 2 frontend nodes and 3 backend nodes
    TransformationBackend.main(Seq("2551").toArray)
    TransformationBackend.main(Seq("2552").toArray)
//    TransformationBackend.main(Array.empty)
//    TransformationBackend.main(Array.empty)
//    TransformationFrontend.main(Array.empty)
  }
}
