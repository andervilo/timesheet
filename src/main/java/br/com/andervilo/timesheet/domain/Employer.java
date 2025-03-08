package br.com.andervilo.timesheet.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employers")
public class Employer {
    @Id
    private String id;

    private String name;
    private String cnpj;
    private String address;
    private String phone;
    private String email;

    public static Employer of(String name, String cnpj, String address, String phone, String email) {
        return Employer.builder()
            .name(name)
            .cnpj(cnpj)
            .address(address)
            .phone(phone)
            .email(email)
            .build();
    }

    public void update(String name, String cnpj, String address, String phone, String email) {
        this.name = name;
        this.cnpj = cnpj;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }
} 