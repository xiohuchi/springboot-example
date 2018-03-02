package com.dianmi.service;

import com.dianmi.utils.json.ResultJson;

public interface CityService {

    ResultJson pageList(Integer pageSize, Integer currPage);

    ResultJson saveEntity(String fuLiTaoString);

    ResultJson getByIdEntity(Integer id);

    ResultJson deleteEntity(Integer id);
}
