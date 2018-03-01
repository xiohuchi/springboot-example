package com.sample.dao;

import com.sample.domain.UUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UUserDao {

    int insert(@Param("pojo") UUser pojo);

    int insertList(@Param("pojos") List<UUser> pojo);

    List<UUser> select(@Param("pojo") UUser pojo);

    int update(@Param("pojo") UUser pojo);

    UUser selectByAcconut(@Param("pojo") String acconut);
}
