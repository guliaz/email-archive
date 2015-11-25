package com.barley.model

import java.util.Date
import javax.persistence.{Id, Entity}

import scala.beans.BeanProperty

/**
  * Created by vikram.gulia on 11/24/15.
  */
@Entity
class File {

  @Id
  @BeanProperty
  var file_id: Long = _
  @BeanProperty
  var file_name: String = _
  @BeanProperty
  var long_file_name: String = _
  @BeanProperty
  var mime_tag: String = _
  @BeanProperty
  var extension: String = _
  @BeanProperty
  var data: Array[Byte] = _
  @BeanProperty
  var date_created: Date = _

  override def toString = s"File(file_id=$file_id, file_name=$file_name, long_file_name=$long_file_name, mime_tag=$mime_tag, extension=$extension, date_created=$date_created)"
}
