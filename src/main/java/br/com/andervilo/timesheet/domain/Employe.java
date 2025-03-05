package br.com.andervilo.timesheet.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employes")
public class Employe {
    @Id
    private String  id;

    private String name;
    private String email;
    private LocalDate birthDate;

    public static Employe of(String name, String email, LocalDate birthDate) {
        return Employe.builder()
            .name(name)
            .email(email)
            .birthDate(birthDate)
            .build();
    }

    public void update(String nameUpdate, String emailUpdate, LocalDate birthDateUpDate) {
        this.name = nameUpdate;
        this.email = emailUpdate;
        this.birthDate = birthDateUpDate;
    }


}
