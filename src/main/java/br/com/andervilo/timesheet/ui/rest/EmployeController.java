package br.com.andervilo.timesheet.ui.rest;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.andervilo.timesheet.application.EmployeService;
import br.com.andervilo.timesheet.application.command.EmployeCreateCommand;
import br.com.andervilo.timesheet.application.command.EmployeUpdateCommand;
import br.com.andervilo.timesheet.application.dto.EmployeDTO;
import br.com.andervilo.timesheet.application.dto.PageDTO;
import br.com.andervilo.timesheet.application.query.EmployeFilterQuery;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "APIs for managing employees")
public class EmployeController {

    private final EmployeService employeService;

    @Operation(summary = "Create a new employee", description = "Creates a new employee with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<EmployeDTO> create(@RequestBody EmployeCreateCommand command) {
        return ResponseEntity.ok(employeService.create(command));
    }

    @Operation(summary = "Update an existing employee", description = "Updates the information of an existing employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
        @ApiResponse(responseCode = "404", description = "Employee not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmployeDTO> update(
        @Parameter(description = "ID of the employee to update") @PathVariable String id,
        @RequestBody EmployeUpdateCommand command) {
        return ResponseEntity.ok(employeService.update(id, command));
    }

    @Operation(summary = "Delete an employee", description = "Deletes an employee by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @Parameter(description = "ID of the employee to delete") @PathVariable String id) {
        employeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get employee by ID", description = "Retrieves an employee's information by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee found"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmployeDTO> findById(
        @Parameter(description = "ID of the employee to retrieve") @PathVariable String id) {
        return ResponseEntity.ok(employeService.findById(id));
    }

    @Operation(summary = "Get all employees", description = "Retrieves a list of all employees")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of employees retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<EmployeDTO>> findAll() {
        return ResponseEntity.ok(employeService.findAll());
    }

    @Operation(summary = "Filter employees", description = "Retrieves a paginated list of employees based on filter criteria. " +
            "You can filter by name, email, birth date range, or birth month (1-12 for January-December).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Filtered list of employees retrieved successfully")
    })
    @PostMapping("/filter")
    public ResponseEntity<PageDTO<EmployeDTO>> filter(@RequestBody EmployeFilterQuery filterQuery) {
        return ResponseEntity.ok(employeService.findWithFilters(filterQuery));
    }
} 