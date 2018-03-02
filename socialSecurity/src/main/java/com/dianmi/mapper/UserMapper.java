package com.dianmi.mapper;

import com.dianmi.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Integer uId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer uId);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //根据手机号或邮箱获取用户信息
    List<Integer> selectByUsername(@Param("username")String username);

    //根据手机号或邮箱加密码查询用户信息
    List<User> selectByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    //修改用户信息
    int updateUserById(@Param("name") String name, @Param("department") String department, @Param("id") Integer id);

    //根据用户id查询获取密码
    String selectPasswordById(@Param("id")Integer id);

    //修改用户密码
    int updatePasswrodById(@Param("password") String password, @Param("id") Integer id);

	String findUserName(@Param("uId") Integer userId);
}