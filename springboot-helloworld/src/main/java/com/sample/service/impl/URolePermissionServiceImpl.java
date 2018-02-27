package com.sample.service.impl;

import com.sample.domain.URolePermission;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

import com.sample.dao.URolePermissionDao;

@Service
public class URolePermissionServiceImpl {

    @Resource
    private URolePermissionDao uRolePermissionDao;

}
