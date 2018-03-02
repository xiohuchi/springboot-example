package com.dianmi.service.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.dianmi.mapper.CostDgMapper;
import com.dianmi.mapper.CostGzMapper;
import com.dianmi.mapper.CostSzMapper;
import com.dianmi.mapper.CostZhMapper;
import com.dianmi.mapper.EmailRecordMapper;
import com.dianmi.mapper.EmployeeFuwufanganMapper;
import com.dianmi.mapper.FuwufanganMapper;
import com.dianmi.mapper.JianyuanMapper;
import com.dianmi.mapper.JianyuanYichangMapper;
import com.dianmi.mapper.SupplierCostMapper;
import com.dianmi.mapper.SupplierMapper;
import com.dianmi.mapper.SupplierTemplateMapper;
import com.dianmi.mapper.ZaiceMapper;
import com.dianmi.mapper.ZengyuanMapper;
import com.dianmi.mapper.ZengyuanYichangMapper;
import com.dianmi.mapper.ZhangdanMapper;
import com.dianmi.mapper.ZhangdanMingxiMapper;
import com.dianmi.model.po.ZaiceCustomerMsg;
import com.dianmi.service.CustomerService;
import com.dianmi.service.JianyuanService;
import com.dianmi.service.OperationRecordService;
import com.dianmi.service.UserService;
import com.dianmi.service.ZaiceService;
import com.dianmi.service.ZengyuanService;
import com.dianmi.service.ZhangdanService;
import com.dianmi.utils.GetSexFromIDCard;

/**
 * @Author:create by lzw
 * @Date:2017年11月30日 下午2:39:00
 * @Description:
 */
public class CommonService {
	@Autowired
	protected ZengyuanService zengyuanService;
	@Autowired
	protected JianyuanService jianyuanService;
	@Autowired
	protected ZaiceService zaiceService;
	@Autowired
	protected CostGzMapper costGzMapper;
	@Autowired
	protected CostSzMapper costSzMapper;
	@Autowired
	protected CostZhMapper costZhMapper;
	@Autowired
	protected CostDgMapper costDgMapper;
	@Autowired
	protected CustomerService customerService;
	@Autowired
	protected ZhangdanService zhangdanService;
	@Autowired
	protected UserService userService;
	@Autowired
	protected SupplierMapper supplierMapper;
	@Autowired
	protected ZhangdanMapper zhangdanMapper;
	@Autowired
	protected SupplierCostMapper supplierCostMapper;
	@Autowired
	protected ZengyuanMapper zengyuanMapper;
	@Autowired
	protected JianyuanMapper jianyuanMapper;
	@Autowired
	protected OperationRecordService operationRecordService;
	@Autowired
	protected FuwufanganMapper fuwufanganMapper;
	@Autowired
	protected ZhangdanMingxiMapper zhangdanMingxiMapper;
	@Autowired
	protected ZaiceMapper zaiceMapper;
	@Autowired
	protected EmailRecordMapper emailRecordMapper;
	@Autowired
	protected EmployeeFuwufanganMapper employeeFuwufanganMapper;
	@Autowired
	protected SupplierTemplateMapper supplierTemplateMapper;
	@Autowired
	protected ZengyuanYichangMapper zengyuanYichangMapper;
	@Autowired
	protected JianyuanYichangMapper jianyuanYichangMapper;

	protected final String GUANGZHOU = "广州";
	protected final String SHENZHEN = "深圳";
	protected final String ZHUHAI = "珠海";
	protected final String DONGGUAN = "东莞";

	/**
	 * @return
	 */
	protected String realPath() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		return request.getSession().getServletContext().getRealPath("/");
	}

	/**
	 * @param yearMonth
	 * @param cityName
	 * @param certificateNumber
	 * @return 根据月份、城市、员工身份证信息从在册表获取在册id、客户id、客户名称
	 */
	protected ZaiceCustomerMsg zaiceMsg(String yearMonth, String cityName, String certificateNumber) {
		// 根据月份、城市、身份证号码从在册表中获取在册信息
		Map<String, Object> maps = zaiceService.selectZaiceMsg(yearMonth, cityName, certificateNumber);
		int customerId = 0;
		String customerName = null;
		int zaiceId = 0;
		if (null != maps) {
			zaiceId = (int) maps.get("zc_id");
			customerId = (Integer) maps.get("customer_id");
			customerName = (String) maps.get("customer_name");
		}
		ZaiceCustomerMsg customerMsg = new ZaiceCustomerMsg(zaiceId, customerId, customerName);
		return customerMsg;
	}

	/**
	 * @param certificateNumberList
	 * @param yearMonth
	 * @param DONGGUAN
	 */
	protected void updateToZengyuanFailed(List<String> certificateNumberList, String yearMonth, String city) {
		for (String certificateNumber : certificateNumberList) {
			zengyuanService.zengyuanFaild(yearMonth, city, certificateNumber);
		}
	}

	/**
	 * @param certificateNumberList
	 * @param yearMonth
	 * @param DONGGUAN
	 */
	protected void updateToJianyuanFailed(List<String> certificateNumberList, String yearMonth, String city) {
		for (String certificateNumber : certificateNumberList) {
			jianyuanService.jianyuanFaild(yearMonth, city, certificateNumber);
		}
	}

	/**
	 * @param certificateNumberList
	 * @param yearMonth
	 * @param DONGGUAN
	 */
	protected void updateToJianyuanSuccess(List<String> certificateNumberList, String yearMonth, String city) {
		for (String certificateNumber : certificateNumberList) {
			jianyuanService.jianyuanSuccess(yearMonth, city, certificateNumber);
		}
	}

}