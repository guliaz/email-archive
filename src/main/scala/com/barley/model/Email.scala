package com.barley.model

import java.util.Date
import javax.persistence.Entity
import javax.persistence.Id

import scala.beans.BeanProperty

/**
  * Created by vikram.gulia on 11/24/15.
  */
@Entity
class Email {

  @Id
  @BeanProperty
  val message_id: Long = _
  @BeanProperty
  var outlook_id: String = _
  // varchar 200 not null
  @BeanProperty
  var subject: String = _
  // TEXT
  @BeanProperty
  var body_text: String = _
  // TEXT
  @BeanProperty
  var body_html: String = _
  // varchar  200 null
  @BeanProperty
  var from_email: String = _
  // varchar  200 null
  @BeanProperty
  var from_name: String = _
  // date null
  @BeanProperty
  var message_creation_date: Date = _
  // date null
  @BeanProperty
  var message_date: Date = _
  // varchar  200 null
  @BeanProperty
  var date_created: Date = _
  @BeanProperty
  var to_email: String = _
  // varchar  200 null
  @BeanProperty
  var to_name: String = _
  // varchar  200 null
  @BeanProperty
  var display_to: String = _
  // varchar  200 null
  @BeanProperty
  var display_cc: String = _
  // varchar  200 null
  @BeanProperty
  var display_bcc: String = _

}
