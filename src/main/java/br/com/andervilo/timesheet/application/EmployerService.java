package br.com.andervilo.timesheet.application;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import br.com.andervilo.timesheet.application.command.EmployerCreateCommand;
import br.com.andervilo.timesheet.application.command.EmployerUpdateCommand;
import br.com.andervilo.timesheet.application.dto.EmployerDTO;
import br.com.andervilo.timesheet.application.dto.PageDTO;
import br.com.andervilo.timesheet.application.query.EmployerFilterQuery;
import br.com.andervilo.timesheet.domain.Employer;
import br.com.andervilo.timesheet.domain.repository.EmployerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final EmployerRepository employerRepository;

    public EmployerDTO create(EmployerCreateCommand command) {
        var employer = Employer.of(command.name(), command.cnpj(), command.address(), command.phone(), command.email());
        employerRepository.save(employer);
        return EmployerDTO.from(employer);
    }

    public EmployerDTO update(String id, EmployerUpdateCommand command) {
        var employerExisting = employerRepository.findById(id).orElseThrow();
        employerExisting.update(command.name(), command.cnpj(), command.address(), command.phone(), command.email());
        employerRepository.save(employerExisting);
        return EmployerDTO.from(employerExisting);
    }

    public void delete(String id) {
        employerRepository.deleteById(id);
    }

    public EmployerDTO findById(String id) {
        return employerRepository
            .findById(id)
            .map(EmployerDTO::from)
            .orElseThrow();
    }

    public List<EmployerDTO> findAll() {
        return employerRepository
            .findAll()
            .stream()
            .map(EmployerDTO::from)
            .toList();
    }

    public PageDTO<EmployerDTO> findWithFilters(EmployerFilterQuery filterQuery) {
        var pageable = filterQuery.toPageable();
        var page = employerRepository.findWithFilters(filterQuery, pageable);
        
        return PageDTO.from(page.map(EmployerDTO::from));
    }
}