package com.dianmi.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.druid.util.StringUtils;
import com.dianmi.service.ExceptionService;
import com.dianmi.utils.file.DeleteFile;
import com.dianmi.utils.file.DownloadFile;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;

/**
 * @author zhiwei loong
 * @Date 2017年11月23日 下午4:30:02
 * @Description
 */
@RestController
@RequestMapping("/api/exception")
public class ExceptionController extends BaseController {

	@Autowired
	private ExceptionService exceptionService;

	/**
	 * @param yearMonth
	 * @return 导出异常增减员
	 * @throws IOException
	 */
	@RequestMapping("/export")
	public ResultJson export(String yearMonth,String importDate) throws IOException {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth)) {
			resultJson = ResultUtil.success(RestEnum.PARAMETER_ERROR);
		} else {
			String filePath = exceptionService.createExceptionFile(yearMonth, user().getUId(),importDate);
			DownloadFile.downloadFile(filePath, "异常增减员名单" + yearMonth + ".xlsx");
			DeleteFile.DeleteFolder(filePath);
			resultJson = ResultUtil.success(RestEnum.SUCCESS);
		}
		return resultJson;
	}

	@PostMapping("/import")
	public ResultJson importData(String yearMonth, @RequestParam("file") MultipartFile file) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth)) {
			resultJson = ResultUtil.success(RestEnum.PARAMETER_ERROR);
		} else {

			resultJson = ResultUtil.success(RestEnum.SUCCESS);
		}
		return resultJson;
	}

	/**
	 * @param yearMonth
	 * @param currPage
	 * @param pageSize
	 * @param type
	 * @return 增减员异常
	 */
	@PostMapping("/all")
	public ResultJson all(String yearMonth, Integer currPage, Integer pageSize, Integer type,String importDate) {
		return exceptionService.allException(yearMonth, user().getUId(), type, currPage, pageSize,importDate);
	}

	/**
	 * @param type
	 * @param id
	 * @return
	 */
	@DeleteMapping("/delete")
	public ResultJson delete(Integer type, Integer id) {
		return exceptionService.deleteException(type, id);
	}

	/**
	 * @param type
	 * @param ids
	 * @return
	 */
	@DeleteMapping("/batchDelete")
	public ResultJson batchDelete(Integer type, String ids) {
		return exceptionService.batchDeleteException(type, ids);
	}
	
	
	/**
	 * @param type
	 * @return
	 */
	@PostMapping("/importDate")
	public ResultJson importDate(String yearMonth,Integer type){
		return exceptionService.importDate(yearMonth,type,user().getUId());
	}

}
