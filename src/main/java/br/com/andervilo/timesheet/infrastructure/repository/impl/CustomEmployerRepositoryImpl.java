package br.com.andervilo.timesheet.infrastructure.repository.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import br.com.andervilo.timesheet.application.query.EmployerFilterQuery;
import br.com.andervilo.timesheet.domain.Employer;
import br.com.andervilo.timesheet.infrastructure.repository.CustomEmployerRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CustomEmployerRepositoryImpl implements CustomEmployerRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Employer> findWithFilters(EmployerFilterQuery filterQuery, Pageable pageable) {
        Query query = toQuery(filterQuery);
        
        // Get total count
        long total = mongoTemplate.count(query, Employer.class);
        
        // Apply pagination
        query.with(pageable);
        
        // Execute query
        List<Employer> employers = mongoTemplate.find(query, Employer.class);
        
        return new PageImpl<>(employers, pageable, total);
    }
    
    private Query toQuery(EmployerFilterQuery filterQuery) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (filterQuery.getName() != null && !filterQuery.getName().isEmpty()) {
            criteria.and("name").regex(filterQuery.getName(), "i");
        }

        if (filterQuery.getCnpj() != null && !filterQuery.getCnpj().isEmpty()) {
            criteria.and("cnpj").regex(filterQuery.getCnpj(), "i");
        }

        if (filterQuery.getEmail() != null && !filterQuery.getEmail().isEmpty()) {
            criteria.and("email").regex(filterQuery.getEmail(), "i");
        }

        if (filterQuery.getPhone() != null && !filterQuery.getPhone().isEmpty()) {
            criteria.and("phone").regex(filterQuery.getPhone(), "i");
        }

        if (filterQuery.getAddress() != null && !filterQuery.getAddress().isEmpty()) {
            criteria.and("address").regex(filterQuery.getAddress(), "i");
        }

        query.addCriteria(criteria);
        return query;
    }
} 