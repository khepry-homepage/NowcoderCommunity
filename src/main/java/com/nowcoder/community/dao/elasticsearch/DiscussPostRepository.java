package com.nowcoder.community.dao.elasticsearch;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.HighlightParameters;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
    @Highlight(
        fields = {
            @HighlightField(name = "title"),
            @HighlightField(name = "content")
        },
        parameters = @HighlightParameters(preTags = {"<em>"}, postTags = {"</em>"},numberOfFragments = 0)
    )
    SearchHits<DiscussPost> findByTitleOrContent(String title, String content);
}
