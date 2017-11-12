package prac11

/**
  * Created by fangqiao on 2017/11/12.
  */
final case class TransformationJob(text: String)
final case class TransformationResult(text: String)
final case class JobFailed(reason: String,job: TransformationJob)
case object BackendRegistration

