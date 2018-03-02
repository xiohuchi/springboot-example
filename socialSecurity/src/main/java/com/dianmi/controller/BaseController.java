package com.dianmi.controller;

import com.dianmi.config.JwtTokenUtil;
import com.dianmi.model.User;
import com.dianmi.utils.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static com.dianmi.common.Common.JWT_HEADER;

/**
 * created by www
 * 2017/9/27 17:00
 */
public class BaseController extends Common{
    public Logger logger =  LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public User user(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader(JWT_HEADER);
        User user = jwtTokenUtil.getUserFromToken(token);
        return user;
    }
}