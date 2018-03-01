package com.sample.controller;

import com.sample.contsant.rest.RestEnum;
import com.sample.contsant.rest.RestJson;
import com.sample.contsant.rest.RestUtils;
import com.sample.domain.UUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Api(tags = "01、用户模块", description = "/api/user", position = 1)
@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @ApiOperation(value = "登录")
    @PostMapping("/login")
    public RestJson submitLogin(String account, String password) {
//        Map<String, Object> resultMap = new LinkedHashMap<>();
        UsernamePasswordToken token = new UsernamePasswordToken(account, password);
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);
            return RestUtils.success(subject.getSession().getId());
        } catch (IncorrectCredentialsException e) {
            return error(RestEnum.LOGIN_INVALID, "密码错误");
        } catch (LockedAccountException e) {
            return error(RestEnum.LOGIN_INVALID, "登录失败，该用户已被冻结");
        } catch (AuthenticationException e) {
            return error(RestEnum.LOGIN_INVALID, "该用户不存在");
        } catch (Exception e) {
            e.printStackTrace();
            return error(RestEnum.LOGIN_INVALID, "登录异常");
        }
    }

//    //登陆验证，这里方便url测试，正式上线需要使用POST方式提交
//    @ApiOperation(value = "登陆验证")
//    @GetMapping("/loginCheck")
//    public String index(UUser user) {
//        if (user.getNickname() != null && user.getPswd() != null) {
//            UsernamePasswordToken token = new UsernamePasswordToken(user.getNickname(), user.getPswd(), "login");
//            Subject currentUser = SecurityUtils.getSubject();
//            logger.info("对用户[" + user.getNickname() + "]进行登录验证..验证开始");
//            try {
//                //调用subject.login(token)之后就交给shiro去认证了
//                currentUser.login(token);
//                //验证是否登录成功
//                if (currentUser.isAuthenticated()) {
//                    logger.info("用户[" + user.getNickname() + "]登录认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)");
//                    System.out.println("用户[" + user.getNickname() + "]登录认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)");
//                    return "redirect:/";
//                } else {
//                    token.clear();
//                    System.out.println("用户[" + user.getNickname() + "]登录认证失败,重新登陆");
//                    return "redirect:/login";
//                }
//            } catch (UnknownAccountException uae) {
//                logger.info("对用户[" + user.getNickname() + "]进行登录验证..验证失败-username wasn't in the system");
//            } catch (IncorrectCredentialsException ice) {
//                logger.info("对用户[" + user.getNickname() + "]进行登录验证..验证失败-password didn't match");
//            } catch (LockedAccountException lae) {
//                logger.info("对用户[" + user.getNickname() + "]进行登录验证..验证失败-account is locked in the system");
//            } catch (AuthenticationException ae) {
//                logger.error(ae.getMessage());
//            }
//        }
//        return "login";
//    }
}

