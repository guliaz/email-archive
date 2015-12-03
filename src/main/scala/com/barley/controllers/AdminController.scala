package com.barley.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

/**
  * Created by vikram.gulia on 12/2/15.
  */
@Controller
class AdminController {

  @RequestMapping(value = Array("/login"))
  def login(model: ModelAndView): ModelAndView = {
    model.setViewName("login")
    model
  }
}
