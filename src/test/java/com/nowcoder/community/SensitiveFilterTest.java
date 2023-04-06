package com.nowcoder.community;

import com.nowcoder.community.utils.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SensitiveFilterTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Test
    public void testFilter() {
        String text = "我要去银行抢劫，跟我一起去抢劫吧";
        assert ("我要去银行***，跟我一起去***吧".equals(sensitiveFilter.filterMatch(text)));
        text = "连环杀人魔杰克";
        assert ("连环***魔杰克".equals(sensitiveFilter.filterMatch(text)));
        text = "bca";
        assert ("bc***".equals(sensitiveFilter.filterMatch(text)));
    }
}
