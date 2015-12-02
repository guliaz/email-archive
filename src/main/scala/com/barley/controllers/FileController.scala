package com.barley.controllers

import java.io.{BufferedOutputStream, File, FileOutputStream}

import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RequestParam, RestController}
import org.springframework.web.multipart.MultipartFile

@RestController
class FileController {

  @RequestMapping(value = Array("/upload"), method = Array(RequestMethod.GET))
  def info() = {
    "You can upload a file by posting to this same URL."
  }

  @RequestMapping(value = Array("/upload"), method = Array(RequestMethod.POST))
  def upload(@RequestParam("file") file: MultipartFile) = {
    if (!file.isEmpty) {
      val name = file.getOriginalFilename
      try {
        val bytes = file.getBytes
        val stream: BufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(name)))
        stream.write(bytes)
        stream.close()
        "You successfully uploaded " + name + "!"
      } catch {
        case e: Exception => {
          e.printStackTrace()
          "You failed to upload " + name + " => " + e.getMessage()
        }
      }
    } else {
      "You failed to upload because the file was empty."
    }
  }

}
