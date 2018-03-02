package com.dianmi.utils;

import java.math.BigDecimal;

/**
 * @Author:create by lzw
 * @Date:2017年11月29日 上午10:21:23
 * @Description:
 */
public class MathArithmetic {
	private static final int DEF_DIV_SCALE = 10; // 这个类不能实例化

	// 加法运算
	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}

	// 减法运算
	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	// 乘法运算
	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	// 除法运算
	public static double div(double v1, double v2) {
		return div(v1, v2, DEF_DIV_SCALE);
	}

	// 除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("指定精度必须大于0");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	// 提供精确的小数位四舍五入处理。
	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("指定精度必须大于0");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}