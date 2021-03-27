package com.travelfourm.dao;

import com.travelfourm.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {

    /**
     * 查询全部用户
     * return List*/
    List<User> selectAllUser();

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id,int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id,String password);
}
