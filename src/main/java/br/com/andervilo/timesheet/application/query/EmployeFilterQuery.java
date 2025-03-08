package br.com.andervilo.timesheet.application.query;

import java.time.LocalDate;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeFilterQuery extends BaseFilterQuery {
    private String name;
    private String email;
    private LocalDate birthDateStart;
    private LocalDate birthDateEnd;

    public Query toQuery() {
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (name != null && !name.isEmpty()) {
            criteria.and("name").regex(name, "i");
        }

        if (email != null && !email.isEmpty()) {
            criteria.and("email").regex(email, "i");
        }

        if (birthDateStart != null && birthDateEnd != null) {
            criteria.and("birthDate").gte(birthDateStart).lte(birthDateEnd);
        } else if (birthDateStart != null) {
            criteria.and("birthDate").gte(birthDateStart);
        } else if (birthDateEnd != null) {
            criteria.and("birthDate").lte(birthDateEnd);
        }

        query.addCriteria(criteria);
        return query;
    }
} 