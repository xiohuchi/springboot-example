package com.dianmi.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dianmi.mapper.ZhangdanMapper;
import com.dianmi.mapper.ZhangdanMingxiMapper;
import com.dianmi.model.ZhangdanMingxi;
import com.dianmi.model.po.FenzuZhangdanPo;
import com.dianmi.model.po.ZhangDanMingxiPo;
import com.dianmi.model.po.ZongZhangdanPo;
import com.dianmi.service.ZhangdanService;

@Service
public class ZhangdanServiceImpl implements ZhangdanService {
	@Autowired
	private ZhangdanMapper zhangdanMapper;
	@Autowired
	private ZhangdanMingxiMapper zhangdanMingxiMapper;

	public List<ZongZhangdanPo> supplierZhangdan(String yearMonth, int userId, String supplierName, int roleType) {
		return zhangdanMapper.supplierZhangdan(yearMonth, userId, "%" + supplierName + "%", roleType);
	}

	public List<ZongZhangdanPo> customerZhangdan(String yearMonth, int userId, String customerName, int roleType) {
		return zhangdanMapper.customerZhangdan(yearMonth, userId, "%" + customerName + "%", roleType);
	}

	public List<FenzuZhangdanPo> groupByCustomer(String yearMonth, int supplierId) {
		return zhangdanMapper.groupByCustomer(yearMonth, supplierId);
	}

	public List<FenzuZhangdanPo> groupBySupplier(String yearMonth, int customerId) {
		return zhangdanMapper.groupBySupplier(yearMonth, customerId);
	}

	public List<ZhangDanMingxiPo> supplierZhangdanMingxi(String yearMonth, int supplierId) {
		return zhangdanMingxiMapper.supplierZhangdanMingxi(yearMonth, supplierId);
	}

	public List<ZhangDanMingxiPo> customerZhangdanMingxi(String yearMonth, int customerId) {
		return zhangdanMingxiMapper.customerZhangdanMingxi(yearMonth, customerId);
	}

	public ZhangdanMingxi getById(int zdId) {
		return zhangdanMingxiMapper.selectByPrimaryKey(zdId);
	}

	public int update(String zhangdanMingxiStr) {
		zhangdanMingxiStr = zhangdanMingxiStr.substring(zhangdanMingxiStr.indexOf("{"),
				zhangdanMingxiStr.lastIndexOf("}") + 1);
		JSONObject json = JSONObject.parseObject(zhangdanMingxiStr);
		ZhangdanMingxi zhangdanMingxi = new ZhangdanMingxi(json.getInteger("zdId"), json.getInteger("zaiceId"),
				json.getDouble("yuqiYanglaoGongsi"), json.getDouble("yuqiYanglaoGeren"),
				json.getDouble("yuqiJibenYiliaoGongsi"), json.getDouble("yuqiJibenYiliaoGeren"),
				json.getDouble("yuqiDabingYiliaoGongsi"), json.getDouble("yuqiDabingYiliaoGeren"),
				json.getDouble("yuqiShiyeGongsi"), json.getDouble("yuqiShiyeGeren"),
				json.getDouble("yuqiGongshangGongsi"), json.getDouble("yuqiShengyuGongsi"),
				json.getDouble("yuqiGongjijinGongsi"), json.getDouble("yuqiGongjijinGeren"),
				json.getDouble("yuqiQitaGongsi"), json.getDouble("yuqiQitaGeren"),
				json.getDouble("yuqiCanbaojinGongsi"), json.getDouble("yuqiFuwufei"),
				json.getDouble("shijiYanglaoGongsi"), json.getDouble("shijiYanglaoGeren"),
				json.getDouble("shijiJibenYiliaoGongsi"), json.getDouble("shijiJibenYiliaoGeren"),
				json.getDouble("shijiYuqiDabingYiliaoGongsi"), json.getDouble("shijiYuqiDabingYiliaoGeren"),
				json.getDouble("shijiYuqiShiyeGongsi"), json.getDouble("shijiYuqiShiyeGeren"),
				json.getDouble("shijiYuqiGongshangGongsi"), json.getDouble("shijiYuqiShengyuGongsi"),
				json.getDouble("shijiYuqiGongjijinGongsi"), json.getDouble("shijiYuqiGongjijinGeren"),
				json.getDouble("shijiYuqiQitaGongsi"), json.getDouble("shijiYuqiQitaGeren"),
				json.getDouble("shijiYuqiCanbaojinGongsi"), json.getDouble("shijiYuqiFuwufei"));
		return zhangdanMingxiMapper.updateByPrimaryKey(zhangdanMingxi);
	}

	@Override
	public int updateZhangdanByZaiceId(ZhangdanMingxi zhangdanMingxi) {
		return zhangdanMingxiMapper.updateZhangdanByZaiceId(zhangdanMingxi);
	}

	public int delete(int zmId) {
		return zhangdanMingxiMapper.deleteByPrimaryKey(zmId);
	}

	/**
	 * @param gongjijinGongsi
	 * @param gongjijiGeren
	 * @param zaiceId
	 *            修改实际费用中公积金数据
	 */
	public void updateGongjijin(double gongjijinGongsi, double gongjijinGeren, int zaiceId) {
		zhangdanMingxiMapper.updateGongjijin(gongjijinGongsi, gongjijinGeren, zaiceId);
	}
}