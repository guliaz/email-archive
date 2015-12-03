package com.barley.controllers

import java.lang.{Long, Iterable}
import java.util
import javax.sql.DataSource

import com.barley.model.Email
import com.barley.respository.{AttachmentRepository, EmailRepository, FileRepository, RecipientRepository}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{RequestParam, RequestMethod, RequestMapping, RestController}

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
    val listOfIds: util.ArrayList[Long] = new util.ArrayList[Long]()
    for (i <- ((page - 1) * numPerPage + 1) to ((page - 1) * numPerPage + numPerPage)) {
      listOfIds.add(i)
    }
    emailRepository.findAll(listOfIds)

  }
}
