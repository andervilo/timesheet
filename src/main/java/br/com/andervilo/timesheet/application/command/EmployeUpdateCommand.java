package br.com.andervilo.timesheet.application.command;

import java.time.LocalDate;

public record EmployeUpdateCommand(
    String id,
    String name,
    String email,
    LocalDate birthDate
) {
    
} 
