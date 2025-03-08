package br.com.andervilo.timesheet.application.query;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployerFilterQuery extends BaseFilterQuery {
    private String name;
    private String cnpj;
    private String email;
    private String phone;
    private String address;
}