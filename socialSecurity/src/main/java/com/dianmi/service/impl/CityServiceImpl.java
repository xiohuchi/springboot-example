package com.dianmi.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dianmi.mapper.CityMapper;
import com.dianmi.model.City;
import com.dianmi.service.CityService;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CityServiceImpl implements CityService {


    private final Logger logger = Logger.getLogger(this.getClass());
    
    @Autowired
    private CityMapper cityMapper;


    @Override
    public ResultJson pageList(Integer pageSize, Integer currPage) {
        PageHelper.startPage(currPage, pageSize);
        PageInfo<City> pageInfo = new PageInfo<City>(cityMapper.pageList());
        return ResultUtil.success(RestEnum.SUCCESS, pageInfo);
    }

    @Override
    public ResultJson saveEntity(String cityStr) {
        if (cityStr == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        cityStr = cityStr.substring(cityStr.indexOf("{"), cityStr.lastIndexOf("}") + 1);
        int size = 0;
        try {
            // JSON 字符串转 Java对象
            City city = JSONObject.parseObject(cityStr, City.class);
            // 判断是保存还是更新
            size = city.getCId() != null ? cityMapper.updateByPrimaryKey(city) : cityMapper.insertSelective(city);
        } catch (Exception e) {
            logger.error("fulitaoServiceImpl @ saveEntity", e);
            throw new RuntimeException(e);
        }
        return size == 1 ? ResultUtil.success(RestEnum.SUCCESS) : ResultUtil.error(RestEnum.SAVE_FALL);
    }

    @Override
    public ResultJson getByIdEntity(Integer id) {
        if (id == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        City city = cityMapper.selectByPrimaryKey(id);
        if (city == null) {
            return ResultUtil.error(RestEnum.ENTITY_IS_EMPTY);
        }
        return ResultUtil.success(RestEnum.SUCCESS, city);
    }


    @Override
    public ResultJson deleteEntity(Integer id) {
        if (id == null) {
            return ResultUtil.error(RestEnum.PARAMETER_ERROR);
        }
        int size = cityMapper.deleteByPrimaryKey(id);
        return size == 1 ? ResultUtil.success(RestEnum.SUCCESS) : ResultUtil.error(RestEnum.DELETE_FAIL);
    }
}
