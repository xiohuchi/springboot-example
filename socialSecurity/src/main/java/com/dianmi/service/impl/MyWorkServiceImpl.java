package com.dianmi.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.dianmi.mapper.MyWorkMapper;
import com.dianmi.model.User;
import com.dianmi.service.MyWorkService;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MyWorkServiceImpl implements MyWorkService {

    @NonNull
    private MyWorkMapper myWorkMapper;

    @Override
    public ResultJson addAndDelStaff(User user, String yearMonth) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.putAll(myWorkMapper.addAndDelStaff(user.getUId(), user.getRoleType(), yearMonth));// 增减员

        return ResultUtil.success(RestEnum.SUCCESS,  resultMap);
    }

	@Override
	public ResultJson zaiceMsg(String yearMonth, User user) {
		if (StringUtils.isEmpty(yearMonth))
			return ResultUtil.error(RestEnum.PARAMETER_ERROR);
		Map<String, Object> map = myWorkMapper.zaiceMsg(yearMonth, user.getUId(), user.getRoleType());
		return ResultUtil.success(RestEnum.SUCCESS, map);
	}

}