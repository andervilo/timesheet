package br.com.andervilo.timesheet.application.dto;

import br.com.andervilo.timesheet.domain.Employer;
import lombok.Builder;

@Builder
public record EmployerDTO(
    String id,
    String name,
    String cnpj,
    String address,
    String phone,
    String email
) {
    public static EmployerDTO from(Employer employer) {
        return EmployerDTO.builder()
            .id(employer.getId())
            .name(employer.getName())
            .cnpj(employer.getCnpj())
            .address(employer.getAddress())
            .phone(employer.getPhone())
            .email(employer.getEmail())
            .build();
    }
} 