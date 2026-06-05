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
public class AddressProcedureRepository {

    private final SimpleJdbcCall addAddressCall;

    public AddressProcedureRepository(JdbcTemplate jdbcTemplate) {
        this.addAddressCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("prc_add_address")
                .declareParameters(
                        new SqlParameter("p_user_id", Types.NUMERIC),
                        new SqlParameter("p_street", Types.VARCHAR),
                        new SqlParameter("p_number", Types.VARCHAR),
                        new SqlParameter("p_neighborhood", Types.VARCHAR),
                        new SqlParameter("p_city", Types.VARCHAR),
                        new SqlParameter("p_state", Types.VARCHAR),
                        new SqlParameter("p_zip_code", Types.VARCHAR),
                        new SqlOutParameter("p_id", Types.NUMERIC)
                );
    }

    public Long addAddress(Long userId, String street, String number, String neighborhood,
                           String city, String state, String zipCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("p_user_id", userId);
        params.put("p_street", street);
        params.put("p_number", number);
        params.put("p_neighborhood", neighborhood);
        params.put("p_city", city);
        params.put("p_state", state);
        params.put("p_zip_code", zipCode);

        Map<String, Object> result = addAddressCall.execute(params);

        return ((Number) result.get("p_id")).longValue();
    }
}
