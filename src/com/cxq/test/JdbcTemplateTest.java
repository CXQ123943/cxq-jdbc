package com.cxq.test;

import com.cxq.jdbc.JdbcTemplate;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @author CXQ
 * @version 1.0
 */
public class JdbcTemplateTest {
    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Test
    public void execute() {
        String sql = "CREATE TABLE `EXECUTE_TEST` (" +
                "`ID` INT AUTO_INCREMENT PRIMARY KEY ," +
                "`NAME` VARCHAR(50) NOT NULL) " +
                "ENGINE = INNODB DEFAULT CHARSET UTF8MB4";
        jdbcTemplate.execute(sql);
    }

    @Test
    public void queryForList() {
        String sql = "SELECT * FROM EMP WHERE ENAME != ? AND SAL > ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, "SMITH", 1500);
        for (Map<String, Object> map : list) {
            System.out.println(map);
        }
    }

    @Test
    public void queryForMap() {
        String sql = "SELECT * FROM EMP WHERE EMPNO = ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, "7654");
        for (Map<String, Object> map : list) {
            System.out.println(map);
        }
    }
}
