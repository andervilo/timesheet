package br.com.andervilo.timesheet.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.andervilo.timesheet.application.query.EmployerFilterQuery;
import br.com.andervilo.timesheet.domain.Employer;

public interface CustomEmployerRepository {
    Page<Employer> findWithFilters(EmployerFilterQuery filterQuery, Pageable pageable);
} 