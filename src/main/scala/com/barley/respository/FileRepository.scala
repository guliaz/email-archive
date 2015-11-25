package com.barley.respository

import java.lang.Long

import com.barley.model.File
import org.springframework.data.repository.CrudRepository

/**
  * Created by vikram.gulia on 11/25/15.
  */
trait FileRepository extends CrudRepository[File, Long]
