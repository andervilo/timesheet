package br.com.andervilo.timesheet.infrastructure.repository.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import br.com.andervilo.timesheet.application.query.EmployeFilterQuery;
import br.com.andervilo.timesheet.domain.Employe;
import br.com.andervilo.timesheet.infrastructure.repository.CustomEmployeRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CustomEmployeRepositoryImpl implements CustomEmployeRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Employe> findWithFilters(EmployeFilterQuery filterQuery, Pageable pageable) {
        Query query = filterQuery.toQuery();
        
        // Get total count
        long total = mongoTemplate.count(query, Employe.class);
        
        // Apply pagination
        query.with(pageable);
        
        // Execute query
        List<Employe> employees = mongoTemplate.find(query, Employe.class);
        
        return new PageImpl<>(employees, pageable, total);
    }
} 