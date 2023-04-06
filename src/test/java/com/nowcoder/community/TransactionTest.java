package com.nowcoder.community;

import com.nowcoder.community.service.AlphaServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionTest {
    @Autowired
    private AlphaServiceImpl alphaService;
    @Test
    public void testTransactionRollback() {
        alphaService.saveAlpha();           //  测试声明式事务是否能回滚
    }
    @Test
    public void testTransactionpProgramRollback() {
        alphaService.saveAlphaProgram();    //  测试编程式事务是否能回滚
    }
}
