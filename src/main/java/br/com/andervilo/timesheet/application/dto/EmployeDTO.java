package br.com.andervilo.timesheet.application.dto;

import java.time.LocalDate;

import br.com.andervilo.timesheet.domain.Employe;
import lombok.Builder;

@Builder
public record EmployeDTO(
    String id,
    String name,
    String email,
    LocalDate birthDate
) {
    public static EmployeDTO from(Employe employe) {
        return EmployeDTO.builder()
            .id(employe.getId())
            .name(employe.getName())
            .email(employe.getEmail())
            .birthDate(employe.getBirthDate())
            .build();
    }
}
