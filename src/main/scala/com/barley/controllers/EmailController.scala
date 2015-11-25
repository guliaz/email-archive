package com.barley.controllers

import java.lang.Iterable

import com.barley.model.Email
import com.barley.respository.{RecipientRepository, FileRepository, AttachmentRepository, EmailRepository}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{RequestMapping, RestController}

/**
  * Created by vikram.gulia on 11/25/15.
  */
@RestController
@RequestMapping(value = Array("/emails"))
class EmailController @Autowired()(
                                    private val emailRepository: EmailRepository,
                                    private val attachmentRepository: AttachmentRepository,
                                    private val fileRepository: FileRepository,
                                    private val recipientRepository: RecipientRepository
                                  ) {

  @RequestMapping(value = Array("/list"))
  def list(): Iterable[Email] = {
    val emails = emailRepository.findAll()
    emails
  }

}
