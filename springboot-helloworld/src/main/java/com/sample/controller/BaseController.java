package com.sample.controller;

import com.sample.contsant.rest.RestEnum;
import com.sample.contsant.rest.RestJson;
import com.sample.contsant.rest.RestUtils;
import com.sample.domain.UUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

public class BaseController {
    //    @Resource
//    private RedisService redisService;
//    @Resource
//    private LogRecordService logBaseRecordService;
//
    private Logger logger = LoggerFactory.getLogger(BaseController.class);

//    protected UUser getCurrentUser() {
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String authHeader = request.getHeader(JwtUtil.HEADER);
//        final String authToken = authHeader.substring(JwtUtil.TOKEN_HEAD.length()); // The part after "Bearer "
//        Long userId = JwtUtil.getUserId(authToken);
//        return redisService.get(userId, UUser.class);
//        return null;
//    }

    public RestJson success() {
        return RestUtils.success();
    }

    protected RestJson error(RestEnum restEnum) {
        return RestUtils.error(restEnum);
    }

    protected RestJson error(RestEnum restEnum, String message) {
        return RestUtils.error(restEnum, message);
    }
}
