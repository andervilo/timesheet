package br.com.andervilo.timesheet.application.query;

import java.time.LocalDate;
import java.time.Month;

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
    private Integer birthMonth; // 1-12 representing January-December

    public Query toQuery() {
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (name != null && !name.isEmpty()) {
            criteria.and("name").regex(name, "i");
        }

        if (email != null && !email.isEmpty()) {
            criteria.and("email").regex(email, "i");
        }

        // Handle birth month filter
        if (birthMonth != null && birthMonth >= 1 && birthMonth <= 12) {
            // MongoDB aggregation expression to extract month from birthDate
            criteria.and("birthDate").exists(true);
            criteria.orOperator(
                Criteria.where("birthDate").regex("-" + String.format("%02d", birthMonth) + "-", "i")
            );
        } else {
            // Handle date range if birth month is not specified
            if (birthDateStart != null && birthDateEnd != null) {
                criteria.and("birthDate").gte(birthDateStart).lte(birthDateEnd);
            } else if (birthDateStart != null) {
                criteria.and("birthDate").gte(birthDateStart);
            } else if (birthDateEnd != null) {
                criteria.and("birthDate").lte(birthDateEnd);
            }
        }

        query.addCriteria(criteria);
        return query;
    }
} 