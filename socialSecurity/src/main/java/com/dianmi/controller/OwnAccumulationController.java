package com.dianmi.controller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.druid.util.StringUtils;
import com.dianmi.common.OwnHomeUtils;
import com.dianmi.service.CostDgService;
import com.dianmi.service.CostGzService;
import com.dianmi.service.CostSzService;
import com.dianmi.service.CostZhService;
import com.dianmi.utils.file.DeleteFile;
import com.dianmi.utils.file.DownloadFile;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/api")
public class OwnAccumulationController extends BaseController{
	@Value("${file.maximum.upload.size}")
	private long maxFileSize;
    @Autowired
    private CostDgService dgHomeService;
    @Autowired
    private CostZhService zhHomeService;
    @Autowired
    private CostGzService gzHomeService;
    @Autowired
    private CostSzService szHomeService;
    
    private static ObjectMapper MAPPER = new ObjectMapper();   
    
    
    @PostMapping("/ownhome/accumulation/importData")
    @ResponseBody
    public ResultJson  readAccumulationInfo(@RequestParam("file") MultipartFile file, Integer supplierId, String yearMonth) {
    	String filePath = null;
    	List result = null;
    	try {
	    	if ((!file.isEmpty()) && (supplierId!=null) && (!StringUtils.isEmpty(yearMonth))){
	    		 String fileName = file.getOriginalFilename();
	    		 if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
	                 //上传文件超出文件大小限制
	                 if (file.getSize() > maxFileSize) {
	                     return ResultUtil.success(RestEnum.FILE_SIZE_EXCEEDS_LIMIT);
	                 } else {
	                         yearMonth = yearMonth.trim().replaceAll("—", "-").replaceAll("/", "-");
	                         filePath = OwnHomeUtils.uploadFile(file);
	                         if ((null == filePath || (filePath.equals("")))) {
	                 			return ResultUtil.success(RestEnum.FILE_NOT_EXISTS);
	                 		 }
	                         if(supplierId==1001){
	                        	 String city="深圳";
	                        	// result = szHomeService.readSZAccumulationFee(filePath, user().getUId(), supplierId, yearMonth,city);
	                         }else if (supplierId==1002) {
	                        	 String city="深圳";
	                        	// result = szHomeService.readSZAccumulationFee(filePath, user().getUId(), supplierId, yearMonth,city);
	                         }else if (supplierId==1003) {
	                        	 String city="东莞";
	                        	 //result = dgHomeService.readDGAccumulationFee(filePath, user().getUId(), supplierId, yearMonth,city);
	                         }else if (supplierId==1004) {
	                        	 String city="珠海";
	                        	 result = zhHomeService.readZHAccumulationFee(filePath, user().getUId(), supplierId, yearMonth,city);
	                         }else if (supplierId==1005) {
	                        	 String city="广州";
	                        	 result = gzHomeService.readGZAccumulationFee(filePath, user().getUId(), supplierId, yearMonth,city);
	                         }else if (supplierId==1006) {
	                        	 String city="深圳";
	                        	// result = szHomeService.readSZAccumulationFee(filePath, user().getUId(), supplierId, yearMonth,city);
	                         }
	                         if(result != null){
	                        	 String dgJson = MAPPER.writeValueAsString(result);
	                        	 return ResultUtil.success(RestEnum.SUCCESS,"上传成功！");
	                         }
	                 }
	             } else {
	                 return ResultUtil.success(RestEnum.FILE_FORMATS_ERROR);
	             }
	             //excel模板不存在
	         } else {
	             return ResultUtil.success(RestEnum.PARAMETER_ERROR);
	         }
    		 return ResultUtil.success(RestEnum.USER_TOKEN_ERROR);
    	} catch (Exception e) {
    		logger.error(e.getMessage(), e);
    		DeleteFile.DeleteFolder(filePath);		//上传后删除Excel模板
    		return ResultUtil.success(RestEnum.FAILD,"上传失败！");
    	}
    }
    
    
    
    
    @PostMapping("/ownhome/ownAccumulation/exportData")
    @ResponseBody
    public ResultJson  writeAccumulationInfo(Integer supplierId, String yearMonth) {
    	String fileName = "编号"+supplierId+"的供应商"+yearMonth+"月公积金费用明细.xls";
    	try {
    		String pageOwnList = null;
    			if (supplierId==null || StringUtils.isEmpty(yearMonth)) {
    				return ResultUtil.success(RestEnum.PARAMETER_ERROR);
    			} else {
	    			yearMonth = yearMonth.trim().replaceAll("—", "-").replaceAll("/", "-");
	    			if(supplierId == 1001){
	    				pageOwnList = szHomeService.writeSZAccumulationInfo(supplierId,yearMonth);
	    			}else if (supplierId == 1002) {
	    				pageOwnList = szHomeService.writeSZAccumulationInfo(supplierId,yearMonth);
	    			}else if (supplierId == 1003) {
	    				//pageOwnList = dgHomeService.writeDGAccumulationFee(supplierId,yearMonth);
	    			}else if (supplierId == 1005) {
						pageOwnList = gzHomeService.writeGZAccumulationFee(supplierId,yearMonth);
					}else if (supplierId == 1004) {
						pageOwnList = zhHomeService.writeZHAccumulationFee(supplierId,yearMonth);
					}else if (supplierId == 1006) {
	    				pageOwnList = szHomeService.writeSZAccumulationInfo(supplierId,yearMonth);
	    			}
    			}
    			if(pageOwnList!=null){
    				DownloadFile.downloadFile(pageOwnList, fileName);
    				DeleteFile.DeleteFolder(pageOwnList);
    				return ResultUtil.success(RestEnum.SUCCESS);
    			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			DeleteFile.DeleteFolder(fileName);
			return ResultUtil.success(RestEnum.FAILD,"哎呀！出错了！");
		}
    	return ResultUtil.success(RestEnum.FAILD,"哎呀！出错了！");
    }
}    