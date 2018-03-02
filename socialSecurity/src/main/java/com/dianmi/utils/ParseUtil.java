package com.dianmi.utils;

/**
 * create by lzw 2017年11月20日-上午10:06:46 description:
 */
public class ParseUtil {

	/**
	 * @param gongjijinRatioStr
	 * @return 公积金比例：公司比例+个人比例
	 */
	public static float[] parseGongjijinStrRatioToFloat(String gongjijinRatioStr) {
		float[] ratio = new float[2];
		float companyRatio = Float.parseFloat(gongjijinRatioStr.substring(0, gongjijinRatioStr.lastIndexOf("%+")))
				/ 100;
		float personRation = Float.parseFloat(
				gongjijinRatioStr.substring(gongjijinRatioStr.lastIndexOf("%+") + 2, gongjijinRatioStr.length() - 1))
				/ 100;
		ratio[0] = companyRatio;
		ratio[1] = personRation;
		return ratio;
	}

}