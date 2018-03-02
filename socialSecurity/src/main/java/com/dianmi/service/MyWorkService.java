package com.dianmi.service;

import com.dianmi.model.User;
import com.dianmi.utils.json.ResultJson;

public interface MyWorkService {


    ResultJson addAndDelStaff(User user, String yearMonth);
    
    ResultJson zaiceMsg(String yearMonth,User user);
}
