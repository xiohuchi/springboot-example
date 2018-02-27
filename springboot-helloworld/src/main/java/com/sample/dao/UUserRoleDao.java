package com.sample.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import com.sample.domain.UUserRole;
import org.springframework.stereotype.Service;

@Service
public interface UUserRoleDao {

    int insert(@Param("pojo") UUserRole pojo);

    int insertList(@Param("pojos") List<UUserRole> pojo);

    List<UUserRole> select(@Param("pojo") UUserRole pojo);

    int update(@Param("pojo") UUserRole pojo);

}
