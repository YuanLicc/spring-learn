package com.yl.learn.mvc.jdbc;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class RdbmsSubclass {

    class SqlQuerySubclass extends SqlQuery {
        @Override
        protected RowMapper newRowMapper(Object[] parameters, Map context) {
            return null;
        }
    }

    class MappingSqlQuerySubclass extends MappingSqlQuery {

        @Override
        protected Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return null;
        }
    }

}