package br.com.andervilo.timesheet.application;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.andervilo.timesheet.application.command.EmployeCreateCommand;
import br.com.andervilo.timesheet.application.command.EmployeUpdateCommand;
import br.com.andervilo.timesheet.application.dto.EmployeDTO;
import br.com.andervilo.timesheet.application.dto.PageDTO;
import br.com.andervilo.timesheet.application.query.EmployeFilterQuery;
import br.com.andervilo.timesheet.domain.Employe;
import br.com.andervilo.timesheet.domain.repository.EmployeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class EmployeService {

    private final EmployeRepository employeRepository;

    public EmployeDTO create(EmployeCreateCommand command) {
        var employe = Employe.of(command.name(), command.email(), command.birthDate()); 
        employeRepository.save(employe);
        return EmployeDTO.from(employe);
    }

    public EmployeDTO update(String id, EmployeUpdateCommand command) {
        var employeExisting = employeRepository.findById(id).orElseThrow();
        employeExisting.update(command.name(), command.email(), command.birthDate());
        employeRepository.save(employeExisting);
        return EmployeDTO.from(employeExisting);
    }

    public void delete(String id) {
        employeRepository.deleteById(id);
    }

    public EmployeDTO findById(String id) {
        return employeRepository
        .findById(id)
        .map(EmployeDTO::from)
        .orElseThrow();
    }

    public List<EmployeDTO> findAll() {
        return employeRepository
        .findAll()
        .stream()
        .map(EmployeDTO::from)
        .toList();
    }

    public PageDTO<EmployeDTO> findWithFilters(EmployeFilterQuery filterQuery) {
        var pageable = filterQuery.toPageable();
        var page = employeRepository.findWithFilters(filterQuery, pageable);
        
        return PageDTO.from(page.map(EmployeDTO::from));
    }

}
