package com.travelfourm.service;

import com.travelfourm.dao.DiscussPostMapper;
import com.travelfourm.dao.elasticsearch.DiscussPostRepository;
import com.travelfourm.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 34612
 * 存的是_doc（文档类型，实际上是json形式）
 */

@Service
public class ElasticsearchService {

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    /**
     * 保存帖子*/
    public void saveDiscussPost(DiscussPost post){
        discussPostRepository.save(post);
    }

    /**
     * 删除帖子，调用此函数时，会直接删除es索引里面所存储的帖子，所以当搜索时，不会出现已删除的帖子*/
    public void deleteDiscussPost(int id){
        discussPostRepository.deleteById(id);
    }

    /**
     * 更新帖子模块*/
    public void updateDiscussPostModular(int id,String modular){
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(id);
        discussPost.setModular(modular);
    }

    /**
     * 更新帖子*/
    public void updateDiscussPost(int id,String title,String content,String modular){
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(id);
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setModular(modular);
        discussPostRepository.save(discussPost);
    }

    /**
     * 更新帖子状态（此处为将被拉黑的帖子反馈通过后，更新为普通状态）*/
    public void updateDiscussPostStatus(int id,int userId,String title,String content,int type,int status,Date createTime,int commentCount,double score,String modular){
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(id);
        discussPost.setUserId(userId);
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setType(type);
        discussPostMapper.updateStatus(id,0);
        discussPost.setStatus(status);
        discussPost.setCreateTime(createTime);
        discussPost.setCommentCount(commentCount);
        discussPost.setScore(score);
        discussPost.setModular(modular);
        discussPostRepository.save(discussPost);
    }

    /**
     * 分页查找帖子
     * 先通过new一个NativeSearchQueryBuilder来构建一个查询,是springdata中的一个查询
     * QueryBuilders.multiMatchQuery表示对多个字段查询
     * .withSort()构建基本的排序机制
     * .withPageable()分页查询
     * .withHighlightFields()表示高亮文本显示*/
    public Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit){
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword,"title","content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current,limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        /**
         * 这里的代码可以优化，需要抛出一个与数据库中查询的数据不一致的异常
         * 分页查询
         * 并通过重写AggregatedPage来执行搜索
         * SearchHits获取命中次数，如果命中了则开始遍历所命中的结果*/
        return elasticsearchTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                SearchHits hits = searchResponse.getHits();
                if (hits.getTotalHits() <= 0){
                    return null;
                }

                List<DiscussPost> list = new ArrayList<>();
                for (SearchHit hit : hits){
                    DiscussPost post = new DiscussPost();

                    String id = hit.getSourceAsMap().get("id").toString();
                    post.setId(Integer.valueOf(id));

                    String userId = hit.getSourceAsMap().get("userId").toString();
                    post.setUserId(Integer.valueOf(userId));

                    String title = hit.getSourceAsMap().get("title").toString();
                    post.setTitle(title);

                    String content = hit.getSourceAsMap().get("content").toString();
                    post.setContent(content);

                    /**
                     * es搜索引擎这里会从哪里拿？*/
                    String status = hit.getSourceAsMap().get("status").toString();
                    post.setStatus(Integer.valueOf(status));

                    String createTime = hit.getSourceAsMap().get("createTime").toString();
                    post.setCreateTime(new Date(Long.valueOf(createTime)));

                    String commentCount = hit.getSourceAsMap().get("commentCount").toString();
                    post.setCommentCount(Integer.valueOf(commentCount));

                    /**
                     * 因为这里可能会存在板块为空的可能，所以es可以不命中这个板块*/
//                    String modular = hit.getSourceAsMap().get("modular").toString();
//                    post.setModular(modular);

                    /**
                     * 处理高亮文本显示结果*/
                    HighlightField titleField = hit.getHighlightFields().get("title");
                    if (titleField != null){
                        post.setTitle(titleField.getFragments()[0].toString());
                    }

                    HighlightField contentField = hit.getHighlightFields().get("content");
                    if (contentField != null){
                        post.setContent(contentField.getFragments()[0].toString());
                    }

                    list.add(post);
                }
                return new AggregatedPageImpl(list,pageable,
                        hits.getTotalHits(),searchResponse.getAggregations(),searchResponse.getScrollId(),hits.getMaxScore());
            }
        });
    }
}
