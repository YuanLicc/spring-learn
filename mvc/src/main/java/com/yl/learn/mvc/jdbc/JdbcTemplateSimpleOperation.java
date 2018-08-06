package com.yl.learn.mvc.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcTemplateSimpleOperation {

    JdbcTemplate template = new JdbcTemplate();

    public void query() {
        template.query("select * from cc", rs -> {
            // ...
        });
    }

}
