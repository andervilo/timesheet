package br.com.andervilo.timesheet.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import br.com.andervilo.timesheet.domain.Employer;

public interface EmployerRepository extends MongoRepository<Employer, String> {
} 