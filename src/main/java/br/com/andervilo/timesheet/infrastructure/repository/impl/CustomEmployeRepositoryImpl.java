package br.com.andervilo.timesheet.infrastructure.repository.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import br.com.andervilo.timesheet.application.query.EmployeFilterQuery;
import br.com.andervilo.timesheet.domain.Employe;
import br.com.andervilo.timesheet.infrastructure.repository.CustomEmployeRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CustomEmployeRepositoryImpl implements CustomEmployeRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Employe> findWithFilters(EmployeFilterQuery filterQuery, Pageable pageable) {
        Query query = toQuery(filterQuery);
        
        // Special handling for birth month filter
        if (filterQuery.getBirthMonth() != null && filterQuery.getBirthMonth() >= 1 && filterQuery.getBirthMonth() <= 12) {
            return findWithBirthMonthFilter(filterQuery, pageable);
        }
        
        // Standard query for other filters
        long total = mongoTemplate.count(query, Employe.class);
        query.with(pageable);
        List<Employe> employees = mongoTemplate.find(query, Employe.class);
        
        return new PageImpl<>(employees, pageable, total);
    }
    
    private Query toQuery(EmployeFilterQuery filterQuery) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (filterQuery.getName() != null && !filterQuery.getName().isEmpty()) {
            criteria.and("name").regex(filterQuery.getName(), "i");
        }

        if (filterQuery.getEmail() != null && !filterQuery.getEmail().isEmpty()) {
            criteria.and("email").regex(filterQuery.getEmail(), "i");
        }

        // Handle birth month filter
        if (filterQuery.getBirthMonth() != null && filterQuery.getBirthMonth() >= 1 && filterQuery.getBirthMonth() <= 12) {
            // MongoDB aggregation expression to extract month from birthDate
            criteria.and("birthDate").exists(true);
            criteria.orOperator(
                Criteria.where("birthDate").regex("-" + String.format("%02d", filterQuery.getBirthMonth()) + "-", "i")
            );
        } else {
            // Handle date range if birth month is not specified
            if (filterQuery.getBirthDateStart() != null && filterQuery.getBirthDateEnd() != null) {
                criteria.and("birthDate").gte(filterQuery.getBirthDateStart()).lte(filterQuery.getBirthDateEnd());
            } else if (filterQuery.getBirthDateStart() != null) {
                criteria.and("birthDate").gte(filterQuery.getBirthDateStart());
            } else if (filterQuery.getBirthDateEnd() != null) {
                criteria.and("birthDate").lte(filterQuery.getBirthDateEnd());
            }
        }

        query.addCriteria(criteria);
        return query;
    }
    
    private Page<Employe> findWithBirthMonthFilter(EmployeFilterQuery filterQuery, Pageable pageable) {
        List<AggregationOperation> operations = new ArrayList<>();
        
        // Add projection to extract month from birthDate
        ProjectionOperation projectOperation = Aggregation.project()
            .and("name").as("name")
            .and("email").as("email")
            .and("birthDate").as("birthDate")
            .and("id").as("id")
            .and("birthDate").extractMonth().as("birthMonth");
        operations.add(projectOperation);
        
        // Add match operations for other filters
        if (filterQuery.getName() != null && !filterQuery.getName().isEmpty()) {
            operations.add(Aggregation.match(Criteria.where("name").regex(filterQuery.getName(), "i")));
        }
        
        if (filterQuery.getEmail() != null && !filterQuery.getEmail().isEmpty()) {
            operations.add(Aggregation.match(Criteria.where("email").regex(filterQuery.getEmail(), "i")));
        }
        
        // Add match operation for birth month
        operations.add(Aggregation.match(Criteria.where("birthMonth").is(filterQuery.getBirthMonth())));
        
        // Count total before pagination
        Aggregation countAggregation = Aggregation.newAggregation(operations);
        AggregationResults<Employe> countResults = mongoTemplate.aggregate(countAggregation, "employes", Employe.class);
        long total = countResults.getMappedResults().size();
        
        // Add pagination
        operations.add(Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize()));
        operations.add(Aggregation.limit(pageable.getPageSize()));
        
        // Add sorting if specified
        if (pageable.getSort().isSorted()) {
            operations.add(Aggregation.sort(pageable.getSort()));
        }
        
        Aggregation aggregation = Aggregation.newAggregation(operations);
        AggregationResults<Employe> results = mongoTemplate.aggregate(aggregation, "employes", Employe.class);
        
        return new PageImpl<>(results.getMappedResults(), pageable, total);
    }
} 