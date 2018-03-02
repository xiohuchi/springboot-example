package com.dianmi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.dianmi.model.Jianyuan;
import com.dianmi.model.po.JianyuanPo;
import com.dianmi.service.JianyuanService;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * created by www 2017/10/25 17:36
 */
@RestController
@RequestMapping("/api/jianyuan")
public class JianyuanController extends BaseController {

	@Autowired
	private JianyuanService jianyuanService;

	/**
	 * @param yearMonth
	 * @param paramName
	 * @param pageSize
	 * @param currPage
	 * @param supplierId
	 * @return
	 */
	@PostMapping("/jianyuanPaging")
	public ResultJson jianyuanPaging(String yearMonth, String paramName, Integer pageSize, Integer currPage,
			Integer supplierId) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth) || currPage == null || pageSize == null || supplierId == null) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			PageHelper.startPage(currPage, pageSize);
			Map<String, Object> map = new HashMap<>();
			if (StringUtils.isEmpty(paramName)) {
				paramName = "";
			}
			List<JianyuanPo> list = jianyuanService.jianyuanPaging(yearMonth, paramName, supplierId);
			PageInfo<JianyuanPo> pageInfo = new PageInfo<>(list);
			map.put("jianyuanPaging", pageInfo);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, map);
		}
		return resultJson;
	}

	/**
	 * @param jyId
	 * @return
	 */
	@PostMapping("/delete")
	public ResultJson delete(Integer jyId) {
		ResultJson resultJson;
		if (jyId == null) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			jianyuanService.deleteById(jyId);
			resultJson = ResultUtil.success(RestEnum.SUCCESS);
		}
		return resultJson;
	}

	/**
	 * @param jyId
	 * @return
	 */
	@PostMapping("/toEdit")
	public ResultJson findByZyId(Integer jyId) {
		ResultJson resultJson;
		if (jyId == null) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			Jianyuan jianyuan = jianyuanService.findByZyId(jyId);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, jianyuan);
		}
		return resultJson;

	}

	/**
	 * @param jianyuanStr
	 * @return
	 */
	@PutMapping("/update")
	public ResultJson update(String jianyuanStr) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(jianyuanStr)) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			int result = jianyuanService.update(jianyuanStr);
			if (result == 1)
				resultJson = ResultUtil.success(RestEnum.SUCCESS);
			else
				resultJson = ResultUtil.error(RestEnum.FAILD);
		}
		return resultJson;
	}

	/**
	 * @param ids
	 * @return
	 */
	@DeleteMapping("/batchDelete")
	public ResultJson batchDelete(String ids) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(ids)) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			String[] idArr = ids.replace("，", ",").split(",");
			for (String id : idArr) {
				Integer jyId = Integer.parseInt(id);
				jianyuanService.deleteById(jyId);
			}
			resultJson = ResultUtil.error(RestEnum.SUCCESS);
		}
		return resultJson;
	}

	/**
	 * @param yearMonth
	 * @param supplierId
	 * @return
	 */
	@PostMapping("/notSendAmount")
	public ResultJson notSendEmailAmount(String yearMonth, Integer supplierId) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth) || null == supplierId) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			int[] amount = jianyuanService.amount(yearMonth, supplierId);
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("notSend", amount[0]);
			map.put("send", amount[1]);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, map);
		}
		return resultJson;
	}

	/**
	 * @param yearMonth
	 * @return 用户所有供应商已发未发减员信息总数
	 */
	@PostMapping("/emailAmount")
	public ResultJson emailAmount(String yearMonth) {
		ResultJson resultJson;
		if (StringUtils.isEmpty(yearMonth)) {
			resultJson = ResultUtil.error(RestEnum.PARAMETER_ERROR);
		} else {
			int[] amount = jianyuanService.emailAmount(yearMonth, user().getRoleType(), user().getUId());
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("notSend", amount[0]);
			map.put("send", amount[1]);
			resultJson = ResultUtil.success(RestEnum.SUCCESS, map);
		}
		return resultJson;
	}
}
