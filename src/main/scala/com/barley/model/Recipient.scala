package com.barley.model

import java.util.Date
import javax.persistence.{Entity, Id}

import scala.beans.BeanProperty

/**
  * Created by vikram.gulia on 11/24/15.
  */
@Entity
class Recipient {

  @Id
  @BeanProperty
  var recipient_id: Long = _
  @BeanProperty
  var message_id: Long = _
  @BeanProperty
  var recipient_email: String = _
  @BeanProperty
  var recipient_name: String = _
  @BeanProperty
  var date_created: Date = _
}