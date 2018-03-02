package com.dianmi.service;

import com.dianmi.utils.json.ResultJson;

/**
 * @author zhiwei loong
 * @Date 2017年11月23日 下午4:31:24
 * @Description 
 */
public interface ExceptionService {
	
	public ResultJson allException(String yearMonth, Integer userId, Integer type, Integer currPage, Integer pageSize,String importDate);
	
	public String createExceptionFile(String yearMonth, int userId,String importDate);
	
	public ResultJson deleteException(Integer type, Integer id);
	
	public ResultJson batchDeleteException(Integer type, String ids);
	
	public ResultJson importDate(String yearMonth,Integer type,Integer userId);

}
