package com.dianmi.mapper;

import com.dianmi.model.Fulitao;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

public interface FulitaoMapper extends Mapper {
    int deleteByPrimaryKey(Integer fId);

    int insert(Fulitao record);

    int insertSelective(Fulitao record);

    Fulitao selectByPrimaryKey(Integer fId);

    int updateByPrimaryKeySelective(Fulitao record);

    int updateByPrimaryKey(Fulitao record);

    List<Fulitao> pageList();
}