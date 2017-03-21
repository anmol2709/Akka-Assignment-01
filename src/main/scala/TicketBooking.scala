package com.knoldus


import akka.actor.{Actor, ActorSystem, Props}
import akka.util.Timeout
import org.apache.log4j.Logger

import scala.concurrent.duration._


/**
  * Created by ANMOL on 19-03-2017.
  */

class Person extends Actor {


  override def receive: Receive = {
    case num: Any => print("Person")
  }
}

class BookingReception extends Actor {
  var remainingSeats = 6
  val log = Logger.getLogger(this.getClass)
  override def receive: Receive = {
    case num: Int =>
      log.info(s"* REQUEST FROM : ${sender().path.name.toUpperCase}")
      implicit val timeout = Timeout(10 seconds)
      if (num <= remainingSeats) {
        log.info(s"-- Seats Available :  $remainingSeats")
        remainingSeats -= num
        log.info(s"-- Seats Booked: $num ")
        log.info(s"-- Seats Available Now :  $remainingSeats \n")
      }
      else {
        log.info(s"-- Seats Available :  $remainingSeats")
        log.info(s"-- Sorry !! $num Seats Are Not Available!!  ")
        log.info(s"-- Seats Available Now :  $remainingSeats \n")
      }
  }
}


class BookingQueue extends Actor {

  override def receive: Receive = {
    case request: Int =>
        val bookingReception = context.actorSelection("../Reception")
      bookingReception forward request
  }
}


object TicketBooking extends App {
  implicit val timeout = Timeout(10 seconds)

  val system = ActorSystem("TicketBooking")

  val propsPerson = Props[Person]

  val reception = system.actorOf(Props[BookingReception], "Reception")
  val bookingQueue1 = system.actorOf(Props[BookingQueue])
  val bookingQueue2 = system.actorOf(Props[BookingQueue])
  val bookingQueue3 = system.actorOf(Props[BookingQueue])

  val person1 = system.actorOf(propsPerson, "Person1")
  val person2 = system.actorOf(propsPerson, "Person2")
  val person3 = system.actorOf(propsPerson, "Person3")
  val person4 = system.actorOf(propsPerson, "Person4")
  val person5 = system.actorOf(propsPerson, "Person5")
  val person6 = system.actorOf(propsPerson, "Person6")

  bookingQueue1.tell(1, person1)
  bookingQueue2.tell(2, person2)
  bookingQueue1.tell(3, person3)
  bookingQueue3.tell(4, person4)
  bookingQueue1.tell(1, person5)
  bookingQueue3.tell(2, person6)

}


