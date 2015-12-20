package com.barley.repository;

import com.barley.model.Attachment;
import com.barley.model.File;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface FileRepository extends CrudRepository<File, Long> {
    List<File> findByLongFileName(String longFileName);
}
