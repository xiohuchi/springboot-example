package com.sample.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import com.sample.domain.UUser;
import org.springframework.stereotype.Service;

@Service
public interface UUserDao {

    int insert(@Param("pojo") UUser pojo);

    int insertList(@Param("pojos") List<UUser> pojo);

    List<UUser> select(@Param("pojo") UUser pojo);

    int update(@Param("pojo") UUser pojo);

    UUser selectByAcconut(@Param("pojo") String acconut);
}
