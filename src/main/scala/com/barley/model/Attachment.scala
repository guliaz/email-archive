package com.barley.model

import java.lang
import java.util.Date
import javax.persistence.{Entity, GeneratedValue, Id}

import scala.beans.BeanProperty

/**
  * Created by vikram.gulia on 11/24/15.
  */
@Entity
class Attachment {

  @Id
  @GeneratedValue
  @BeanProperty
  var attachment_id: lang.Long = _
  @BeanProperty
  var message_id: lang.Long = _
  @BeanProperty
  var attachment_type: String = _
  @BeanProperty
  var file_id: lang.Long = _
  @BeanProperty
  var date_created: Date = _

}