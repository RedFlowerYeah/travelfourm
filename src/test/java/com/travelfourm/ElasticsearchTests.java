package com.travelfourm;

import com.travelfourm.dao.DiscussPostMapper;
import com.travelfourm.dao.elasticsearch.DiscussPostRepository;

import com.travelfourm.entity.DiscussPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = TravelfourmApplication.class)
public class ElasticsearchTests {

    @Autowired
    private DiscussPostMapper discussMapper;

    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    private ElasticsearchTemplate elasticTemplate;

    @Test
    public void testInsert() {
        discussRepository.save(discussMapper.selectDiscussPostById(1));
        discussRepository.save(discussMapper.selectDiscussPostById(2));
        discussRepository.save(discussMapper.selectDiscussPostById(3));
    }

    @Test
    public void testUpdate(){
        DiscussPost post = discussMapper.selectDiscussPostById(1);
        post.setContent("我是宝贝!");
        discussRepository.save(post);
    }

    @Test
    public void testDelete(){
        discussRepository.deleteById(3);
    }
}
