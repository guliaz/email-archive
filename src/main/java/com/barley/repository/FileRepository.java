package com.barley.repository;

import com.barley.model.File;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface FileRepository extends CrudRepository<File, Long> {
}
