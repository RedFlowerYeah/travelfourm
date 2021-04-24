package com.travelfourm.dao;

import com.travelfourm.entity.Province;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author 34612
 * @CreateTime 2021/4/16 13:09
 */
@Mapper
@Repository
public interface ProvinceMapper {

    List<Province> selectProvince();

    int insertProvince(Province province);
}
