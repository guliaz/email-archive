package com.barley.repository;

import com.barley.model.Recipient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface RecipientRepository extends CrudRepository<Recipient, Long> {
}
