package com.barley

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Application

object Application {
  def main(args: Array[String]): Unit = {
    println("######## Starting the application now ########")
    SpringApplication.run(classOf[Application])
    println("######## Application load finished ########")
  }
}