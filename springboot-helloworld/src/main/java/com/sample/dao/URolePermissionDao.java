package com.sample.dao;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import com.sample.domain.URolePermission;

public interface URolePermissionDao {

    int insert(@Param("pojo") URolePermission pojo);

    int insertList(@Param("pojos") List< URolePermission> pojo);

    List<URolePermission> select(@Param("pojo") URolePermission pojo);

    int update(@Param("pojo") URolePermission pojo);

}
