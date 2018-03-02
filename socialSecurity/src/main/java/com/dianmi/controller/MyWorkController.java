package com.dianmi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dianmi.service.MyWorkService;
import com.dianmi.utils.json.ResultJson;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 我的工作
 */
@RestController
@RequestMapping("/api/work/")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MyWorkController extends BaseController {

	@NonNull
	private MyWorkService myWorkService;

	/**
	 * 增减员
	 * 
	 * @param yearMonth
	 * @return
	 */
	@RequestMapping(value = "addAndDelStaff")
	public ResultJson addAndDelStaff(String yearMonth) {
		return myWorkService.addAndDelStaff(user(), yearMonth);
	}

	/**
	 * @param yearMonth
	 * @return
	 */
	@PostMapping("zaiceMsg")
	public ResultJson zaiceMsg(String yearMonth) {
		return myWorkService.zaiceMsg(yearMonth, user());
	}
}
