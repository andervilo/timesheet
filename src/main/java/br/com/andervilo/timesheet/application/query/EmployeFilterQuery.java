package br.com.andervilo.timesheet.application.query;

import java.time.LocalDate;
import java.time.Month;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeFilterQuery extends BaseFilterQuery {
    private String name;
    private String email;
    private LocalDate birthDateStart;
    private LocalDate birthDateEnd;
    private Integer birthMonth; // 1-12 representing January-December
}