package br.com.andervilo.timesheet.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.andervilo.timesheet.application.query.EmployeFilterQuery;
import br.com.andervilo.timesheet.domain.Employe;

public interface CustomEmployeRepository {
    Page<Employe> findWithFilters(EmployeFilterQuery filterQuery, Pageable pageable);
} 