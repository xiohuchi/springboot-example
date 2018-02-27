package com.sample.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import com.sample.domain.City;

public interface CityDao {

    int insert(@Param("pojo") City pojo);

    int insertList(@Param("pojos") List<City> pojo);

    List<City> select(@Param("pojo") City pojo);

    int update(@Param("pojo") City pojo);

    City selectById(@Param("pojo") Long pojo);

    int deleteById(@Param("pojo") Long pojo);
}
