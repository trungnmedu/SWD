package com.swd.backend.utils;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Lazy(value = true)
public class QueryBuilderHelper {
    public String addWhereClause(String previousQuery) {
        return previousQuery.concat(" WHERE");
    }

    public String addAndClause(String previousQuery) {
        return previousQuery.concat(" ADD");
    }

    public String addOrderByClause(String previousQuery, List<String> sortBy, String sort) {
        if (sort == null || sortBy == null || sortBy.size() == 0) {
            return previousQuery;
        }
        String order = " ORDER BY (" + String.join(", ", sortBy);
        return previousQuery.concat(order);
    }

    public String addEqualCondition(String previousQuery, String column, Object value) {
        if (value instanceof Integer) {
            return previousQuery.concat(" (" + "column=" + value + ")");
        }
        if (value instanceof String) {
            return previousQuery.concat(" (" + "column='" + value + "')");
        }
        return null;
    }
}
