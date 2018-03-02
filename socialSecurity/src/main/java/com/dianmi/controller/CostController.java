package com.dianmi.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dianmi.service.CostDgService;
import com.dianmi.service.CostGzService;
import com.dianmi.service.CostService;
import com.dianmi.service.CostSzService;
import com.dianmi.service.CostZhService;
import com.dianmi.utils.json.RestEnum;
import com.dianmi.utils.json.ResultJson;
import com.dianmi.utils.json.ResultUtil;

/**
 * 广州深圳珠海东莞公积金社保费用导入
 */
@RestController
@RequestMapping("/api/cost")
public class CostController extends BaseController {
	@Value("${file.maximum.upload.size}")
	private long maxFileSize;
	@Autowired
	private CostDgService costDgService;
	@Autowired
	private CostZhService costZhService;
	@Autowired
	private CostGzService costGzService;
	@Autowired
	private CostSzService costSzService;
	@Autowired
	private CostService costService;

	/**
	 * @param file
	 * @param yearMonth
	 * @param supplierId
	 * @return 导入社保费用数据
	 */
	@PostMapping("/importCost")
	public ResultJson importCost(String yearMonth, Integer type, Integer supplierId, MultipartFile file) {
		// 深圳立德
		if (type == 1) {
			return costSzService.importCost(file, user().getUId(), supplierId, yearMonth);
			// 广州立德深圳分公司
		} else if (type == 2) {
			return costSzService.importCost(file, user().getUId(), supplierId, yearMonth);
			// 上海立德深圳分公司
		} else if (type == 3) {
			return costSzService.importCost(file, user().getUId(), supplierId, yearMonth);
			// 东莞
		} else if (type == 4) {
			return costDgService.importCost(file, user().getUId(), supplierId, yearMonth);
			// 珠海
		} else if (type == 5) {
			return costZhService.importCost(file, user().getUId(), supplierId, yearMonth);
			// 广州
		} else if (type == 6) {
			return costGzService.importCost(file, user().getUId(), supplierId, yearMonth);
		} else {
			return ResultUtil.error(RestEnum.PARAMETER_ERROR);
		}
	}

	/**
	 * @param yearMonth
	 * @param type
	 * @param file
	 * @param supplierId
	 * @return 导入公积金费用
	 */
	@PostMapping("/importGongjijin")
	public ResultJson importGongjijin(String yearMonth, Integer type, Integer supplierId, MultipartFile file) {
		// 深圳立德
		if (type == 1) {
			return costSzService.importGongjijin(file, user().getUId(), supplierId, yearMonth);
			// 广州立德深圳分公司
		} else if (type == 2) {
			return costSzService.importGongjijin(file, user().getUId(), supplierId, yearMonth);
			// 上海立德深圳分公司
		} else if (type == 3) {
			return costSzService.importGongjijin(file, user().getUId(), supplierId, yearMonth);
			// 东莞
		} else if (type == 4) {
			return costDgService.importGongjijin(file, user().getUId(), supplierId, yearMonth);
			// 珠海
		} else if (type == 5) {
			return costZhService.importGongjijin(file, user().getUId(), supplierId, yearMonth);
			// 广州
		} else if (type == 6) {
			return costGzService.importGongjijin(file, user().getUId(), supplierId, yearMonth);
		} else {
			return ResultUtil.error(RestEnum.PARAMETER_ERROR);
		}
	}

	/**
	 * @param yearMonth
	 * @return 自由户总费用
	 */
	@RequestMapping("/cost")
	public ResultJson cost(String yearMonth) {
		return costService.cost(yearMonth);
	}

	/**
	 * @param yearMonth
	 * @param type
	 * @param supplierId
	 * @return 导出费用详细
	 */
	@RequestMapping("/exportCost")
	public ResultJson exportCost(String yearMonth, Integer type, Integer supplierId) throws IOException {
		return costService.exportCost(yearMonth, type, supplierId);
	}

}