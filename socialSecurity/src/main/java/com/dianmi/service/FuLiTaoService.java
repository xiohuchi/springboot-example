package com.dianmi.service;

import com.dianmi.utils.json.ResultJson;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface FuLiTaoService {


    ResultJson pageList(Integer pageSize, Integer currPage);

    ResultJson saveEntity(String fuLiTaoString);

    ResultJson updateEntity(String fuLiTaoString);

    ResultJson deleteEntity(Integer id);

    ResultJson exportEntity();

    ResultJson importEntity(MultipartFile file, HttpServletRequest request);

    ResultJson getByIdEntity(Integer id);
}
