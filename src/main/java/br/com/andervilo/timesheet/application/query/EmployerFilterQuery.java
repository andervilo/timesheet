package br.com.andervilo.timesheet.application.query;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

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

    public Query toQuery() {
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (name != null && !name.isEmpty()) {
            criteria.and("name").regex(name, "i");
        }

        if (cnpj != null && !cnpj.isEmpty()) {
            criteria.and("cnpj").regex(cnpj, "i");
        }

        if (email != null && !email.isEmpty()) {
            criteria.and("email").regex(email, "i");
        }

        if (phone != null && !phone.isEmpty()) {
            criteria.and("phone").regex(phone, "i");
        }

        if (address != null && !address.isEmpty()) {
            criteria.and("address").regex(address, "i");
        }

        query.addCriteria(criteria);
        return query;
    }
} 