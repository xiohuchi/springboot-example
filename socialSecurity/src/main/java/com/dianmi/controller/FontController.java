package com.dianmi.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.dianmi.config.JwtTokenUtil;
import com.dianmi.model.User;
import com.dianmi.model.po.FuwufanganPo;
import com.dianmi.service.UserService;
import com.dianmi.utils.JacksonUtil;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.dianmi.utils.securiry.MD5;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * created by www 2017/9/29 16:31
 */
@RestController
@RequestMapping("/font")
public class FontController extends BaseController {

	@Autowired
	private UserService userService;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@PostMapping("/login")
	public ResultJson login(String username, String password) {
		ResultJson resultJson;
		// 用户名或者密码为空
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			resultJson = ResultUtil.error(RestEnum.USERNAME_OR_PASSWORD_ISEMPTY);
		} else {
			// 用户名是否存在
			boolean result = userService.isUsernameExists(username.trim());
			if (result) {
				User user = userService.login(username, MD5.getInstance().getMD5(password));
				if (user != null) {
					Map<String, Object> resultMap = new HashMap<>();
					// 用户登录成功
					user.setPassword(null);
					resultMap.put("token", jwtTokenUtil.generateToken(JacksonUtil.pojo2Json(user)));
					resultMap.put("user", user);
					resultMap.put("time", new Date());
					resultJson = ResultUtil.success(RestEnum.SUCCESS, resultMap);
					// 清除操作异常记录
					// caozuoyichangService.deleteUserCaozuoyichang(user.getUId());
				} else {
					// 密码错误
					resultJson = ResultUtil.error(RestEnum.PASSWORD_ERROR);
				}
			} else {
				resultJson = ResultUtil.error(RestEnum.USERNAME_NOT_EXISTS);
			}
		}
		return resultJson;
	}
}