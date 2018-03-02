package com.dianmi.service;

import com.dianmi.model.User;

/**
 * created by www
 * 2017/9/26 13:46
 */
public interface UserService {
    public boolean isUsernameExists(String username);
    
    public User login(String username, String password);

    public String selectPasswordById(Integer id);

    public String updateUserById(String name, String department, Integer id);

    public int updatePasswordById(String password, Integer id);

	public String findUserName(Integer userId);
}