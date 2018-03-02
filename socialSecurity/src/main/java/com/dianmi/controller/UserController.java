package com.dianmi.controller;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.dianmi.config.JwtTokenUtil;
import com.dianmi.service.UserService;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.dianmi.utils.securiry.MD5;

/**
 * created by www 2017/9/26 13:42
 */
@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController {
	@Resource
	JwtTokenUtil jwtTokenUtil;
	@Autowired
	private UserService userService;

	/**
	 * @param oldPassword
	 *            newPassword
	 * @return ResultJson
	 * @description 修改用户密码
	 */
	@PutMapping("/updatePassword")
	public ResultJson updatePassword(String oldPassword, String newPassword) {
		ResultJson resultJson;
		// 原密码与新密码是否为空
		if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword)) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
			// 修改密码
		} else {
			int id = user().getUId();
			String password = userService.selectPasswordById(user().getUId());// 获取密码
			// 验证原密码通过
			if (password.equals(MD5.getInstance().getMD5(oldPassword.trim()))) {
				// 修改密码
				int result = userService.updatePasswordById(MD5.getInstance().getMD5(newPassword.trim()), id);
				if (result > 0)
					resultJson = ResultUtil.success(RestEnum.SUCCESS);
				else
					resultJson = ResultUtil.error(RestEnum.FAILD);
			} else {
				resultJson = ResultUtil.error(RestEnum.OLDPASSWORD_ISERROR);
			}
		}
		return resultJson;
	}

	/**
	 * @description 修改用户信息
	 * @param
	 * @return
	 *
	 */
	@PutMapping("/updateUserInfo")
	public ResultJson updateUserInfo(String name, String department) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(name) && StringUtils.isEmpty(department)) {
			resultJson = ResultUtil.success(RestEnum.PARAMETER_ERROR);
		} else {
			userService.updateUserById(name, department, user().getUId());
			resultJson = ResultUtil.success(RestEnum.SUCCESS);
		}
		return resultJson;
	}
}