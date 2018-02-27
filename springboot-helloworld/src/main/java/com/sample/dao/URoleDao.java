package com.sample.dao;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import com.sample.domain.URole;
import org.springframework.stereotype.Service;

@Service
public interface URoleDao {

    int insert(@Param("pojo") URole pojo);

    int insertList(@Param("pojos") List< URole> pojo);

    List<URole> select(@Param("pojo") URole pojo);

    int update(@Param("pojo") URole pojo);

    List<URole> selectRoleByUid(Long id);
}
