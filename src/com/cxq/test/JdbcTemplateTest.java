package com.cxq.test;

import com.cxq.jdbc.JdbcTemplate;
import org.junit.Test;

/**
 * @author CXQ
 * @version 1.0
 */
public class JdbcTemplateTest {
    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Test
    public void queryForList() {
        String sql = "SELECT * FROM EMP WHERE ENAME != ? AND SAL > ?";
        jdbcTemplate.queryForList(sql,"SMITH",1500);
    }
}
