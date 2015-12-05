package com.barley.respository

import java.lang.Long

import com.barley.model.File
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component

/**
  * Created by vikram.gulia on 11/25/15.
  */
@Component
trait FileRepository extends CrudRepository[File, Long]
