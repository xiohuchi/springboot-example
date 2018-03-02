package com.dianmi.interceptor;

import com.dianmi.config.JwtTokenUtil;
import com.dianmi.utils.json.JsonResult;
import com.dianmi.utils.json.RestEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.dianmi.common.Common.JWT_HEADER;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    private final static Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);

    @Resource
    JwtTokenUtil tokenUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String requestURI = request.getRequestURI();
        logger.info(requestURI);
        if (requestURI.contains("/api/")) {
            String token = request.getHeader(JWT_HEADER);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/json; charset=utf-8");
            if (tokenUtil.getUserFromToken(token) == null) {
                response.getWriter().print(JsonResult.result(RestEnum.USER_TOKEN_ERROR));
                response.getWriter().close();
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
    }
}