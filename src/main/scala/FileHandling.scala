package com.knoldus
import akka.actor.{ActorRef, Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.routing.RoundRobinPool
import akka.util.Timeout
import org.apache.log4j.Logger
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.io.Source


class ChildFileHandlingActor extends Actor {

  override def receive: Receive = {
    case msg: String =>
      val sizeOfLine = msg.split("[ ,!.]+").size
      sender() ! sizeOfLine

  }
}

class FileHandlingActor extends Actor {
  val log = Logger.getLogger(this.getClass)
  implicit val timeout = Timeout(1000 seconds)

  val system = ActorSystem("RouterSystem")
    val childRouter = context.actorOf(RoundRobinPool(5).props(Props[ChildFileHandlingActor]), name = "childPoolRouter")
  val system2 = ActorSystem("RouterSystem")
  val router = system2.actorOf(Props[FileHandlingActor])
  var countOfWords = 0
  var totalLines = 0
  var linesProcessed = 0
var ref: Option[ActorRef]=None
  override def receive: Receive = {

      case msg: String => {


  ref=Some(sender())
        Source.fromFile(msg).getLines.foreach{lines=>
          childRouter ! lines
          totalLines+=1
        }

      }
      case msg: Int => {
        linesProcessed += 1
        countOfWords += msg
        if (totalLines == linesProcessed)

        {
          ref.map(x=> x ! countOfWords)
        }
      }

    }

}

object FileHandling extends App {

  implicit val timeout = Timeout(1000 seconds)
  val log = Logger.getLogger(this.getClass)

  val system = ActorSystem("RouterSystem")
  val router = system.actorOf(Props[FileHandlingActor])

   // Taking a demo file from resources
  val fileName = "./src/main/resources/DemoFile.txt"
    val res = router ? fileName
  res map {x=>log.info("Number of Words In File Are : " + x)

  }
}

