package com.cxq.jpa.test;

import com.cxq.pojo.User;
import com.cxq.tool.CreateTableTool;
import org.junit.Test;

/**
 * @author CXQ
 * @version 1.0
 */
public class MyJPATest {

    @Test
    public void myJpaTest() {
        new CreateTableTool(User.class).build();
    }
}
