package br.com.andervilo.timesheet.infrastructure.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.andervilo.timesheet.domain.Employe;

public interface EmployeRepository extends MongoRepository<Employe, String>, CustomEmployeRepository {
} 