package com.barley.repository;

import com.barley.model.Email;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface EmailRepository extends CrudRepository<Email, Long> {
}
