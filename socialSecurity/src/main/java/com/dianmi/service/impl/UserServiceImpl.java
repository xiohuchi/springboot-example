package com.dianmi.service.impl;

import com.dianmi.mapper.UserMapper;
import com.dianmi.model.User;
import com.dianmi.service.UserService;
import com.dianmi.utils.json.JsonResult;
import com.dianmi.utils.json.RestEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * created by www
 * 2017/9/26 13:46
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * @param username
     * @return boolean
     * @description 用户手机号和邮箱是否存在
     */
    public boolean isUsernameExists(String username) {
        List<Integer> list = userMapper.selectByUsername(username);
        if (list.size() > 0)
            return true;
        else
            return false;
    }

    /**
     * @param username password
     * @return User
     * @description 用户登录
     */
    public User login(String username, String password) {
        List<User> userList = userMapper.selectByUsernameAndPassword(username, password);
        return userList.size() == 0 ? null : userList.get(0);
    }


    /**
     * @param id
     * @return password
     * @description 根据id获取密码
     */
    public String selectPasswordById(Integer id) {
        return userMapper.selectPasswordById(id);
    }

    /**
     * @param name,department,id
     * @return
     * @description修改用户信息
     */
    public String updateUserById(String name, String department, Integer id) {
        String result;
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(department) || StringUtils.isEmpty(id)) {
            result = JsonResult.result(RestEnum.PARAMETER_ERROR);
        } else {
            if (userMapper.updateUserById(name, department, id) > 0)
                result = JsonResult.result(RestEnum.SUCCESS);
            else
                result = JsonResult.result(RestEnum.FAILD);
        }
        return result;
    }

    /**
     * @param
     * @return
     * @description 修改用户密码
     */
    public int updatePasswordById(String password, Integer id) {
        return userMapper.updatePasswrodById(password, id);
    }

	@Override
	public String findUserName(Integer userId) {
		return userMapper.findUserName(userId);
	}

}