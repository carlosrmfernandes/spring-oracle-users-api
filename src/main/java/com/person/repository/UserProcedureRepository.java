package com.person.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserProcedureRepository {

    private final SimpleJdbcCall createUserCall;

    public UserProcedureRepository(JdbcTemplate jdbcTemplate) {

        this.createUserCall = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("pkg_user")
                .withProcedureName("create_user")
                .declareParameters(
                        new SqlParameter("p_name", Types.VARCHAR),
                        new SqlParameter("p_email", Types.VARCHAR),
                        new SqlOutParameter("p_id", Types.NUMERIC)
                );
    }

    public Long createUser(String name, String email) {
        Map<String, Object> params = new HashMap<>();
        params.put("p_name", name);
        params.put("p_email", email);

        Map<String, Object> result = createUserCall.execute(params);
        return ((Number) result.get("p_id")).longValue();
    }

}
