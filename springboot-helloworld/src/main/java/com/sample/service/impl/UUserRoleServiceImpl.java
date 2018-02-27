package com.sample.service.impl;

import com.sample.domain.UUserRole;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

import com.sample.dao.UUserRoleDao;

@Service
public class UUserRoleServiceImpl {

    @Resource
    private UUserRoleDao uUserRoleDao;

    public int insert(UUserRole pojo){
        return uUserRoleDao.insert(pojo);
    }

    public int insertList(List< UUserRole> pojos){
        return uUserRoleDao.insertList(pojos);
    }

    public List<UUserRole> select(UUserRole pojo){
        return uUserRoleDao.select(pojo);
    }

    public int update(UUserRole pojo){
        return uUserRoleDao.update(pojo);
    }

}
