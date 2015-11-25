package com.barley.spring

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.{ComponentScan, ImportResource}

@SpringBootApplication
@ComponentScan(value = Array("com.barley"))
@ImportResource(value = Array("context.xml"))
class Application

object Application {
  def main(args: Array[String]): Unit = {
    println("######## Starting the application now ########")
    SpringApplication.run(classOf[Application])
    println("######## Application load finished ########")
  }
}