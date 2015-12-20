package com.barley.repository;

import com.barley.model.Attachment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AttachmentRepository extends CrudRepository<Attachment, Long> {
    List<Attachment> findByMessageId(Long messageId);
}