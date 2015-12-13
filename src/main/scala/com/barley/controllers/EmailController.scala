package com.barley.controllers

import java.lang.{Iterable, Long}
import java.util

import com.barley.model.{Attachment, Email, File}
import com.barley.respository.{AttachmentRepository, EmailRepository, FileRepository, RecipientRepository}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RequestParam, RestController}

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

  val msg_query1 = "select message_id, outlook_id, subject, from_email, from_name, to_email, body_text, message_date, rank from " +
    "(select message_id, outlook_id, subject, from_email, from_name, to_email, body_text, message_date, @curRank := @curRank + 1 AS rank " +
    "from message, (SELECT @curRank := 0) r order by message_id) as messages where rank >"
  val msg_query2 = " and rank <= "

  @RequestMapping(value = Array("/list"), produces = Array("application/json"))
  def list(): Iterable[Email] = {
    emailRepository.findAll()
  }

  @RequestMapping(value = Array("/count"), method = Array(RequestMethod.GET))
  def count(): String = {
    "{ \"count\":" + emailRepository.count() + "}"
  }

  @RequestMapping(value = Array("/list"), method = Array(RequestMethod.GET), produces = Array("application/json"))
  def list(@RequestParam("page") page: Long, @RequestParam("number") numPerPage: Long): Iterable[Email] = {
    getEmails(page, numPerPage)
  }

  def getEmails(page: Long, numPerPage: Long): Iterable[Email] = {
    val ids: util.List[Long] = new util.ArrayList[Long]
    var start: Long = (page - 1) * numPerPage + 1
    val end: Long = ((page - 1) * numPerPage) + numPerPage
    while (end > start) {
      ids.add(start)
      start += 1
    }
    val emails: Iterable[Email] = emailRepository.findAll(ids)
    import scala.collection.JavaConversions._
    for (email <- emails) {
      val attachments: Iterable[Attachment] = attachmentRepository.findByMessageId(email.message_id)
      println(attachments)
      val fileIds: util.List[Long] = new util.ArrayList[Long]
      for (attachment <- attachments) {
        fileIds.add(attachment.file_id)
      }
      val fileIterable: Iterable[File] = fileRepository.findAll(fileIds)
      println(fileIterable)
      val files: util.List[File] = new util.ArrayList[File]
      for (file <- fileIterable) {
        files.add(file)
      }
      println(files)
      email.setFileArray(files)
    }
    emails
  }

}
