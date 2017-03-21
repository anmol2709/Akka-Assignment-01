package com.knoldus
import akka.actor.{Props, ActorSystem}
import akka.testkit.TestKit
import akka.util.Timeout
import org.scalatest.BeforeAndAfterAll
import org.scalatest.MustMatchers
import org.scalatest.WordSpecLike
import akka.pattern.ask
import scala.concurrent.duration._
import scala.concurrent.Await
class FileHandlingSpec extends TestKit(ActorSystem("test-system")) with WordSpecLike
with BeforeAndAfterAll with MustMatchers {


  override protected def afterAll(): Unit = {
    system.terminate()
  }


  "A ChildHandling Actor " must {
    "respond with Number of Words to another actor when it finished processing" in {
    val ref=system.actorOf(Props[ChildFileHandlingActor])


      ref tell ("hello world",testActor)

      expectMsgPF() {
        case numberOfWords: Int =>
          numberOfWords must be (2)
      }
    }
  }



  "A Parent File Handler" must{


    "respond with total number of words in the file to the calling actor" in{
      val ref=system.actorOf(Props[FileHandlingActor])
      implicit val timeout = Timeout(1000 seconds)
     val result= ref ask ("./src/main/resources/DemoFile.txt")
      Await.result(result,5 seconds) must be (11)


    }
    "respond with error message in the file to the calling actor if type not string" in{
      val ref=system.actorOf(Props[FileHandlingActor])
      implicit val timeout = Timeout(1000 seconds)
      val result= ref ask (60)
      Await.result(result,5 seconds) must be ("Not a File Name")


    }
    }


}
