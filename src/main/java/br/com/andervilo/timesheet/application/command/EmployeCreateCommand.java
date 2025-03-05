package br.com.andervilo.timesheet.application.command;

import java.time.LocalDate;

public record EmployeCreateCommand(
    String name,
    String email,
    LocalDate birthDate
) {


}
