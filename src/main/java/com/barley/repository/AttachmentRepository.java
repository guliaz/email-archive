package com.barley.repository;

import com.barley.model.Attachment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface AttachmentRepository extends CrudRepository<Attachment, Long> {
    Iterable<Attachment> findByMessageId(Long messageId);
}