package com.dianmi.mapper;

import com.dianmi.model.City;

import java.util.List;

public interface CityMapper {
    int deleteByPrimaryKey(Integer cId);

    int insert(City record);

    int insertSelective(City record);

    City selectByPrimaryKey(Integer cId);

    int updateByPrimaryKeySelective(City record);

    int updateByPrimaryKey(City record);

    List<City> pageList();

}