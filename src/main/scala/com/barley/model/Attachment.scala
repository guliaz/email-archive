package com.barley.model

import java.util.Date
import javax.persistence.{Entity, Id}

import scala.beans.BeanProperty

/**
  * Created by vikram.gulia on 11/24/15.
  */
@Entity
class Attachment {

  @Id
  @BeanProperty
  var attachment_id: Long = _
  @BeanProperty
  var message_id: Long = _
  @BeanProperty
  var attachment_type: String = _
  @BeanProperty
  var file_id: Long = _
  @BeanProperty
  var date_created: Date = _

}