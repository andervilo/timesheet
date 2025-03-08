package br.com.andervilo.timesheet.application.command;

public record EmployerUpdateCommand(
    String name,
    String cnpj,
    String address,
    String phone,
    String email
) {} 