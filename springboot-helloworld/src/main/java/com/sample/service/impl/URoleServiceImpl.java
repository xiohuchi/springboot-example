package com.sample.service.impl;

import com.sample.domain.URole;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

import com.sample.dao.URoleDao;

@Service
public class URoleServiceImpl {

    @Resource
    private URoleDao uRoleDao;
}
