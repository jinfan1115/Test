package com.glp.collie.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 本地测试用ID生成器，不支持分布式
 * 
 * @author wangpan
 */
public final class LocalIdUtils {

	public static synchronized String getTestIdNum() {
		String datetime = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
		String s = Integer.toString(new Random().nextInt(99));
		return datetime + addLeftZero(s, 2);
	}

	/**
	 * 左填0
	 * 
	 * @author shijing 2015年6月29日下午1:24:32
	 * @param s
	 * @param length
	 * @return
	 */
	public static String addLeftZero(String s, int length) {
		// StringBuilder sb=new StringBuilder();
		int old = s.length();
		if (length > old) {
			char[] c = new char[length];
			char[] x = s.toCharArray();
			if (x.length > length) {
				throw new IllegalArgumentException("Numeric value is larger than intended length: " + s + " LEN " + length);
			}
			int lim = c.length - x.length;
			for (int i = 0; i < lim; i++) {
				c[i] = '0';
			}
			System.arraycopy(x, 0, c, lim, x.length);
			return new String(c);
		}
		return s.substring(0, length);
	}
}
