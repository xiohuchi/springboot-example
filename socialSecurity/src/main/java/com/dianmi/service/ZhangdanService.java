package com.dianmi.service;

import java.util.List;

import com.dianmi.model.ZhangdanMingxi;
import com.dianmi.model.po.FenzuZhangdanPo;
import com.dianmi.model.po.ZhangDanMingxiPo;
import com.dianmi.model.po.ZongZhangdanPo;

public interface ZhangdanService {

	public List<ZongZhangdanPo> supplierZhangdan(String yearMonth, int userId, String supplierName, int roleType);

	public List<ZongZhangdanPo> customerZhangdan(String yearMonth, int userId, String customerName, int roleType);

	public List<FenzuZhangdanPo> groupByCustomer(String yearMonth, int supplierId);

	public List<FenzuZhangdanPo> groupBySupplier(String yearMonth, int customerId);

	public List<ZhangDanMingxiPo> supplierZhangdanMingxi(String yearMonth, int supplierId);

	public List<ZhangDanMingxiPo> customerZhangdanMingxi(String yearMonth, int customerId);

	public ZhangdanMingxi getById(int zdId);

	public int update(String zhangdanMingxiStr);

	public int updateZhangdanByZaiceId(ZhangdanMingxi supplierDetail);

	public int delete(int zmId);
	
	public void updateGongjijin(double gongjijinGongsi, double gongjijinGeren, int zaiceId);

}
