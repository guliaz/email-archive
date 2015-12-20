package com.barley.repository;

import com.barley.model.Email;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
public interface EmailRepository extends PagingAndSortingRepository<Email, Long> {
}
