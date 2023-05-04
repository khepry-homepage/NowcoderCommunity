package com.nowcoder.community.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;


import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
@Service
public class ElasticSearchService {
    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    public void saveDiscussPost(DiscussPost post) {
        discussPostRepository.save(post);
    }
    public void deleteDiscussPostById(int id) { discussPostRepository.deleteById(id); }
    public int countContainKeyword(String keyword) {
        Query nativeQuery = new NativeQueryBuilder()
                .withQuery(q -> q
                        .multiMatch(m -> m
                                .fields("title", "content")
                                .query(keyword)
                                .operator(Operator.Or)))
                .build();
        return (int) elasticsearchOperations.count(nativeQuery, DiscussPost.class);
    }
    public List<DiscussPost> searchPage(String keyword, int pageOffset, int limit) {
        HighlightParameters highlightParameters = HighlightParameters.builder()
                .withPreTags("<em>")
                .withPostTags("</em>")
                .withRequireFieldMatch(false)
                .withNumberOfFragments(0)
                .build();

        List<HighlightField> highlightFieldsList = new ArrayList<>();
        highlightFieldsList.add(new HighlightField("title"));
        highlightFieldsList.add(new HighlightField("content"));

        Highlight highlight = new Highlight(highlightParameters, highlightFieldsList);
        NativeQuery nativeQuery = new NativeQueryBuilder()
                .withQuery(q -> q
                        .multiMatch(m -> m
                                .fields("title", "content")
                                .query(keyword)
                                .operator(Operator.Or)))
                .withSort(s -> s
                        .field(f -> f
                                .field("type").order(SortOrder.Desc)
                                .field("score").order(SortOrder.Desc)
                                .field("createTime").order(SortOrder.Desc)))
                .withHighlightQuery(new HighlightQuery(highlight, null))
                .withPageable(PageRequest.of(pageOffset, limit))
                .build();
        SearchHits<DiscussPost> hits = elasticsearchTemplate.search(nativeQuery, DiscussPost.class);
        if (hits.getTotalHits() <= 0) {
            return null;
        }
        List<DiscussPost> postList = new ArrayList<>();
        for (SearchHit<DiscussPost> hit : hits) {
            DiscussPost post = hit.getContent();
            if (!hit.getHighlightField("title").isEmpty()) {
                post.setTitle(hit.getHighlightField("title").get(0));
            }
            if (!hit.getHighlightField("content").isEmpty()) {
                post.setContent(hit.getHighlightField("content").get(0));
            }
            postList.add(post);
        }
        return postList;
    }
}
