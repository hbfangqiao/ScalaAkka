package prac7

import akka.actor.{ActorSystem, PoisonPill}
import akka.dispatch.{PriorityGenerator, UnboundedStablePriorityMailbox}
import com.typesafe.config.Config
import prac7.PrinterActor.{HighPrioMsg, LowPrioMsg}


/**
  * Created by FQ on 2017/11/5.
  */

class PrinterPrioMailBox(settings: ActorSystem.Settings, config: Config) extends UnboundedStablePriorityMailbox(
  PriorityGenerator {
    // 'highpriority messages should be treated first if possible
    case HighPrioMsg => 0

    // 'lowpriority messages should be treated last if possible
    case LowPrioMsg => 2

    // PoisonPill when no other left
    case PoisonPill => 3

    // We default to 1, which is in between high and low
    case _ => 1
  }
)
