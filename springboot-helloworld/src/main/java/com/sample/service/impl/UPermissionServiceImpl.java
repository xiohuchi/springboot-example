package com.sample.service.impl;

import com.sample.domain.UPermission;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import com.sample.dao.UPermissionDao;

@Service
public class UPermissionServiceImpl {

    @Resource
    private UPermissionDao uPermissionDao;

}
