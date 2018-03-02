package com.dianmi.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.druid.util.StringUtils;
import com.dianmi.model.Zengyuan;
import com.dianmi.model.po.ZengyuanPo;
import com.dianmi.service.JianyuanService;
import com.dianmi.service.ZengyuanService;
import com.dianmi.utils.file.DeleteFile;
import com.dianmi.utils.file.DownloadFile;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * created by www 2017/10/20 15:51
 */
@RestController
@RequestMapping("/api/zengyuan")
public class ZengyuanController extends BaseController {

	@Value("${file.maximum.upload.size}")
	private long maxFileSize;
	@Autowired
	private ZengyuanService zengyuanService;
	@Autowired
	private JianyuanService jianyuanService;

	/**
	 * @param
	 * @return
	 * @description 导入增减员模板数据
	 */
	@PostMapping("/importData")
	public ResultJson importData(@RequestParam("file") MultipartFile file, Integer deptId, String yearMonth) {
		ResultJson resultJson;
		// 获取增减员数excel模板
		if ((file != null) && (null != deptId) && (!StringUtils.isEmpty(yearMonth))) {
			String fileName = file.getOriginalFilename();
			if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
				// 上传文件超出文件大小限制
				if (file.getSize() > maxFileSize) {
					resultJson = ResultUtil.error(RestEnum.FILE_SIZE_EXCEEDS_LIMIT);
				} else {
					try {
						yearMonth = yearMonth.trim().replaceAll("—", "-").replaceAll("/", "-");
						String result = zengyuanService.importDate(file, user().getUId(), deptId, yearMonth);
						resultJson = ResultUtil.success(RestEnum.SUCCESS, result);
					} catch (Exception e) {
						resultJson = ResultUtil.error(RestEnum.FAILD);
						logger.error("error", e);
					}
				}
			} else {
				resultJson = ResultUtil.error(RestEnum.FILE_FORMATS_ERROR);
			}
			// 增减员excel模板不存在
		} else {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		}
		return resultJson;
	}

	/**
	 * @param
	 * @return
	 * @description 增员分页数据
	 */
	@PostMapping("/zengyuanPaging")
	public ResultJson zengyuanPaging(String yearMonth, String paramName, Integer pageSize, Integer currPage,
			Integer supplierId) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth) || currPage == null || pageSize == null || supplierId == null) {
			resultJson = ResultUtil.success(RestEnum.PARAMETER_ERROR);
		} else {
			PageHelper.startPage(currPage, pageSize);
			Map<String, Object> map = new HashMap<>();
			if (StringUtils.isEmpty(paramName))
				paramName = "";
			List<ZengyuanPo> list = zengyuanService.zengyuanPaging(yearMonth, paramName, supplierId);
			PageInfo<ZengyuanPo> pageInfo = new PageInfo<>(list);
			map.put("zengyaunPaging", pageInfo);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, map);
		}
		return resultJson;
	}

	/**
	 * @param yearMonth
	 * @return
	 */
	@PostMapping("/suppliers")
	public ResultJson suppliers(String yearMonth) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth)) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			List<Map<String, Object>> list = zengyuanService.selectSupplier(yearMonth, user().getUId(),
					user().getRoleType());
			resultJson = ResultUtil.success(RestEnum.SUCCESS, list);
		}
		return resultJson;
	}

	/**
	 * @param yearMonth
	 * @return
	 */
	@PostMapping("/countTotal")
	public ResultJson zengjianyuanTotalCount(String yearMonth, Integer supplierId) {
		ResultJson resultJson;
		if (StringUtils.isEmpty("yearMonth") || supplierId == null) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			Map<String, Object> map = zengyuanService.currMonthTotalZengjianyuan(yearMonth, supplierId, user().getUId(),
					user().getRoleType());
			resultJson = ResultUtil.success(RestEnum.SUCCESS, map);
		}
		return resultJson;
	}

	/**
	 * 发送增减员邮件给供应商
	 * 
	 * @param yearMonth
	 * @param supplierId
	 * @return
	 */
	@PostMapping("/sendEmail")
	public ResultJson sendEmailToSupplier(String yearMonth, Integer supplierId) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth) || supplierId == null) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			String result = zengyuanService.sendEmailToSupplier(yearMonth, supplierId, user());
			resultJson = ResultUtil.success(RestEnum.SUCCESS, result);
		}
		return resultJson;
	}

	/**
	 * @param yearMonth
	 * @param supplierId
	 * @return
	 */
	@PostMapping("/delete")
	public ResultJson delete(Integer zyId) {
		ResultJson resultJson;
		if (null == zyId) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			zengyuanService.deleteById(zyId);
			resultJson = ResultUtil.success(RestEnum.SUCCESS);
		}
		return resultJson;
	}

	/**
	 * @param zyId
	 * @return
	 */
	@PostMapping("/toEdit")
	public ResultJson toEdit(Integer zyId) {
		ResultJson resultJson;
		if (null == zyId) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			Zengyuan zengyuan = zengyuanService.getByZyId(zyId);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, zengyuan);
		}
		return resultJson;
	}

	@PutMapping("/update")
	public ResultJson update(String zengyuanStr) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(zengyuanStr)) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			int result = zengyuanService.update(zengyuanStr);
			if (result == 1)
				resultJson = ResultUtil.success(RestEnum.SUCCESS);
			else
				resultJson = ResultUtil.error(RestEnum.FAILD);
		}
		return resultJson;
	}

	@DeleteMapping("/batchDelete")
	public ResultJson batchDelete(String ids) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(ids)) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			String[] idArr = ids.replace("，", ",").split(",");
			for (String id : idArr) {
				Integer zyId = Integer.parseInt(id);
				zengyuanService.deleteById(zyId);
			}
			resultJson = ResultUtil.error(RestEnum.SUCCESS);
		}
		return resultJson;
	}

	/**
	 * 未发送增员邮件数量
	 * 
	 * @param yearMonth
	 * @param supplierId
	 * @return
	 */
	@PostMapping("/notSendAmount")
	public ResultJson notSendEmailAmount(String yearMonth, Integer supplierId) {
		ResultJson resultJson;
		if (null == supplierId || StringUtils.isEmpty(yearMonth)) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			int[] amount = zengyuanService.amount(yearMonth, supplierId);
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("notSend", amount[0]);
			map.put("send", amount[1]);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, map);
		}
		return resultJson;
	}

	/**
	 * 修改增员供应商
	 * 
	 * @param supplierId
	 * @param zyIdStr
	 */
	@PostMapping("/updateSupplier")
	public ResultJson updateSupplier(String employeeInfo, Integer fwId) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(employeeInfo) || null == fwId) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			zengyuanService.updateSupplier(fwId, employeeInfo);
			resultJson = ResultUtil.success(RestEnum.SUCCESS);
		}
		return resultJson;
	}

	/**
	 * 导出增减员
	 * 
	 * @param yearMonth
	 * @param supplierId
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/export")
	public ResultJson exportZengjianYuan(String yearMonth, Integer supplierId, String supplierName) throws IOException {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth) || null == supplierId || null == supplierName) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			String filePath = zengyuanService.exportZengjianyuan(yearMonth, supplierId);
			DownloadFile.downloadFile(filePath, supplierName + yearMonth + ".xlsx");
			DeleteFile.DeleteFolder(filePath);
			resultJson = ResultUtil.success(RestEnum.SUCCESS);
		}
		return resultJson;
	}

	/**
	 * @param yearMonth
	 * @return
	 */
	@PostMapping("/emailAmount")
	public ResultJson emailAmount(String yearMonth) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth)) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			int[] emailAmount = zengyuanService.emailAmount(yearMonth, user().getUId(), user().getUId());
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("notSend", emailAmount[0]);
			map.put("send", emailAmount[1]);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, map);
		}
		return resultJson;
	}

	/**
	 * @param yearMonth
	 * @return
	 */
	@PostMapping("/amount")
	public ResultJson amount(String yearMonth) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth)) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			int[] amount = zengyuanService.zengjianYuanAmount(yearMonth, user().getUId(), user().getRoleType());
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("zengyuanAmount", amount[0]);
			map.put("jianyuanAmount", amount[1]);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, map);
		}
		return resultJson;
	}

	/**
	 * @param yearMonth
	 * @return
	 */
	@PostMapping("/notMatchSupplierAmount")
	public ResultJson notMatchSupplierAmount(String yearMonth) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth)) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			int notMatchSupplierAmount = zengyuanService.notMatchSupplierAmount(yearMonth);
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("notMatchAmount", notMatchSupplierAmount);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, map);
		}
		return resultJson;
	}

	/**
	 * @param yearMonth
	 * @return
	 */
	@PostMapping("/allCity")
	public ResultJson allCity(String yearMonth) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth)) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			List<Map<String, String>> list = zengyuanService.allCity(yearMonth, user().getUId(), user().getRoleType());
			resultJson = ResultUtil.success(RestEnum.SUCCESS, list);
		}
		return resultJson;
	}

	/**
	 * @param yearMonth
	 * @param city
	 * @return 根据城市参数获取用户所有增员信息
	 */
	@PostMapping("/getByCity")
	public ResultJson getByCity(String yearMonth, String city, Integer pageSize, Integer currPage) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth) || StringUtils.isEmpty(city) || null == pageSize || null == currPage) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			PageHelper.startPage(currPage, pageSize);
			List<ZengyuanPo> list = zengyuanService.selectByCity(yearMonth, city, user().getUId(),
					user().getRoleType());
			PageInfo<ZengyuanPo> pageInfo = new PageInfo<>(list);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, pageInfo);
		}
		return resultJson;
	}

	/**
	 * @param yearMonth
	 * @return 增员表中所有部门信息
	 */
	@PostMapping("/allDept")
	public ResultJson allDept(String yearMonth) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth)) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			List<Map<String, String>> list = zengyuanService.allDept(yearMonth, user().getUId(), user().getRoleType());
			resultJson = ResultUtil.success(RestEnum.SUCCESS, list);
		}
		return resultJson;
	}

	/**
	 * @param yearMonth
	 * @param city
	 * @return 根据城市参数获取用户所有增员信息
	 */
	@PostMapping("/getByDept")
	public ResultJson getByDept(String yearMonth, String deptName, Integer pageSize, Integer currPage) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth) || StringUtils.isEmpty(deptName) || null == pageSize || null == currPage) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			PageHelper.startPage(currPage, pageSize);
			List<ZengyuanPo> list = zengyuanService.selectByDept(yearMonth, user().getUId(), user().getRoleType(),
					deptName);
			PageInfo<ZengyuanPo> pageInfo = new PageInfo<>(list);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, pageInfo);
		}
		return resultJson;
	}

	/**
	 * @param yearMonth
	 * @param isSendEmail
	 * @return
	 */
	@PostMapping("/zengjianyuan")
	public ResultJson zengjianyuan(String yearMonth, Integer type, Integer isSendEmail, Integer currPage,
			Integer pageSize) {
		ResultJson resultJson = null;
		if (null == type) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else if (type == 1) {
			resultJson = ResultUtil.success(RestEnum.SUCCESS,
					zengyuanService.isSendEmailZengyuan(yearMonth, isSendEmail, user(), currPage, pageSize));
		} else if (type == 2) {
			resultJson = ResultUtil.success(RestEnum.SUCCESS,
					jianyuanService.isSendEmailJianyuan(yearMonth, isSendEmail, user(), currPage, pageSize));
		}
		return resultJson;
	}

	/**
	 * @param yearMonth
	 * @param type
	 * @param currPage
	 * @param pageSize
	 * @return 没有匹配供应商
	 */
	@PostMapping("/noSupplier")
	public ResultJson notSupplierZengjianyuan(String yearMonth, Integer type, Integer currPage, Integer pageSize) {
		ResultJson resultJson = null;
		if (null == type) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else if (type == 1) {
			resultJson = ResultUtil.success(RestEnum.SUCCESS,
					zengyuanService.noSupplierZengyuan(yearMonth, currPage, pageSize));
		} else if (type == 2) {
			resultJson = ResultUtil.success(RestEnum.SUCCESS,
					jianyuanService.noSupplierJianyuan(yearMonth, currPage, pageSize));
		}
		return resultJson;
	}

	@RequestMapping("/exportZengjianyuan")
	public ResultJson exportZengjianyuan(String yearMonth, Integer city,Integer type) throws IOException {
		return zengyuanService.export(yearMonth, city, type);
	}
}