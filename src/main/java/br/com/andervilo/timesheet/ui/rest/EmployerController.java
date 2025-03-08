package br.com.andervilo.timesheet.ui.rest;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.andervilo.timesheet.application.EmployerService;
import br.com.andervilo.timesheet.application.command.EmployerCreateCommand;
import br.com.andervilo.timesheet.application.command.EmployerUpdateCommand;
import br.com.andervilo.timesheet.application.dto.EmployerDTO;
import br.com.andervilo.timesheet.application.dto.PageDTO;
import br.com.andervilo.timesheet.application.query.EmployerFilterQuery;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/employers")
@RequiredArgsConstructor
@Tag(name = "Employer Management", description = "APIs for managing employers")
public class EmployerController {

    private final EmployerService employerService;

    @Operation(summary = "Create a new employer", description = "Creates a new employer with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employer created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<EmployerDTO> create(@RequestBody EmployerCreateCommand command) {
        return ResponseEntity.ok(employerService.create(command));
    }

    @Operation(summary = "Update an existing employer", description = "Updates the information of an existing employer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employer updated successfully"),
        @ApiResponse(responseCode = "404", description = "Employer not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmployerDTO> update(
        @Parameter(description = "ID of the employer to update") @PathVariable String id,
        @RequestBody EmployerUpdateCommand command) {
        return ResponseEntity.ok(employerService.update(id, command));
    }

    @Operation(summary = "Delete an employer", description = "Deletes an employer by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Employer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Employer not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @Parameter(description = "ID of the employer to delete") @PathVariable String id) {
        employerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get employer by ID", description = "Retrieves an employer's information by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employer found"),
        @ApiResponse(responseCode = "404", description = "Employer not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmployerDTO> findById(
        @Parameter(description = "ID of the employer to retrieve") @PathVariable String id) {
        return ResponseEntity.ok(employerService.findById(id));
    }

    @Operation(summary = "Get all employers", description = "Retrieves a list of all employers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of employers retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<EmployerDTO>> findAll() {
        return ResponseEntity.ok(employerService.findAll());
    }

    @Operation(summary = "Filter employers", description = "Retrieves a paginated list of employers based on filter criteria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Filtered list of employers retrieved successfully")
    })
    @PostMapping("/filter")
    public ResponseEntity<PageDTO<EmployerDTO>> filter(@RequestBody EmployerFilterQuery filterQuery) {
        return ResponseEntity.ok(employerService.findWithFilters(filterQuery));
    }
}