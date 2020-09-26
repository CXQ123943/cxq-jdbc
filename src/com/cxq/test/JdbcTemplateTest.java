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
        String sql = "CREATE TABLE `JDBC` (" +
                "`ID` INT AUTO_INCREMENT PRIMARY KEY ," +
                "`NAME` VARCHAR(50) NOT NULL) " +
                "ENGINE = INNODB DEFAULT CHARSET UTF8MB4";
        jdbcTemplate.execute(sql);
    }

    @Test
    public void insert() {
        String sql = "INSERT INTO `JDBC` (`NAME`) VALUE (?)";
        System.out.println(jdbcTemplate.update(sql,"zds"));
    }

    @Test
    public void update() {
        String sql = "UPDATE `JDBC` SET `NAME` = ? WHERE `ID` = ?";
        System.out.println(jdbcTemplate.update(sql, "aiyowei", 1));
    }

    @Test
    public void delete() {
        String sql = "DELETE FROM `JDBC` WHERE `ID` = ?";
        System.out.println(jdbcTemplate.update(sql,  1));
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
