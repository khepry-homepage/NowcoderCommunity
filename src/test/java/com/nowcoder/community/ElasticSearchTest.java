package com.nowcoder.community;

import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticSearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticSearchTest {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private ElasticSearchService elasticSearchService;
    @Test
    public void testInsertIndices() {
        DiscussPost post = discussPostService.findDiscussPostById(109);
        discussPostRepository.save(post);
    }
    @Test
    public void testInsertList() {
        discussPostRepository.saveAll(discussPostService.findDiscussPosts(0, 0, 10));
    }
    @Test
    public void testFindById() {
        System.out.println(discussPostRepository.findById(109));
    }
    @Test
    public void testFindAll() {
        Iterable<DiscussPost> list = discussPostRepository.findAll();
        for (DiscussPost post : list) {
            System.out.println(post);
        }
    }
    @Test
    public void testDelById() {
        Iterable<DiscussPost> list = discussPostRepository.findAll();
        for (DiscussPost post : list) {
            System.out.println(post);
        }
        discussPostRepository.deleteById(109);
    }
    @Test
    public void testUpdate() {
        DiscussPost post = discussPostService.findDiscussPostById(275);
        post.setContent("我是测试用户");
        discussPostRepository.save(post);
        System.out.println(discussPostRepository.findById(275));
    }
    @Test
    public void testCountByKeyword() {
        System.out.println(elasticSearchService.countContainKeyword("我"));
    }
    @Test
    public void testSearchByKeyword() {
        SearchHits<DiscussPost> hits = discussPostRepository.findByTitleOrContent("管理员", "管理员");
        for (SearchHit hit : hits) {
            System.out.println("-------------------------");
            System.out.println(hit.getHighlightField("title"));
            System.out.println(hit.getHighlightField("content"));
        }
    }
    @Test
    public void testSearchService() {
        List<DiscussPost> list = elasticSearchService.searchPage("放火", 0, 10);
        for (DiscussPost post : list) {
            System.out.println(post);
        }
    }
}
