package com.barley.respository

import java.lang.{Iterable, Long}

import com.barley.model.Attachment
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component

/**
  * Created by vikram.gulia on 11/25/15.
  */
@Component
trait AttachmentRepository extends CrudRepository[Attachment, Long] {
  def findByMessageId(messageId: Long): Iterable[Attachment]
}
