package com.sample.dao;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import com.sample.domain.UPermission;
import org.springframework.stereotype.Service;

@Service
public interface UPermissionDao {

    int insert(@Param("pojo") UPermission pojo);

    int insertList(@Param("pojos") List< UPermission> pojo);

    List<UPermission> select(@Param("pojo") UPermission pojo);

    int update(@Param("pojo") UPermission pojo);

    List<UPermission> selectPermissionByUid(Long id);
}
