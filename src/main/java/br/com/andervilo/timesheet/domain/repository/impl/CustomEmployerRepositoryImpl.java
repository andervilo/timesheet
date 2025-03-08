package br.com.andervilo.timesheet.domain.repository.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import br.com.andervilo.timesheet.application.query.EmployerFilterQuery;
import br.com.andervilo.timesheet.domain.Employer;
import br.com.andervilo.timesheet.domain.repository.CustomEmployerRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CustomEmployerRepositoryImpl implements CustomEmployerRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Employer> findWithFilters(EmployerFilterQuery filterQuery, Pageable pageable) {
        Query query = filterQuery.toQuery();
        
        // Get total count
        long total = mongoTemplate.count(query, Employer.class);
        
        // Apply pagination
        query.with(pageable);
        
        // Execute query
        List<Employer> employers = mongoTemplate.find(query, Employer.class);
        
        return new PageImpl<>(employers, pageable, total);
    }
} 