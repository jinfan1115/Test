package com.glp.collie.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.glp.collie.model.CommonResponse;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

/**
 * Java Bean属性检查工具类
 * 
 * @author wangp
 */
public final class BeanValCheckUtils {
	public static final String REGEX_ZIMU_DAXIE = "[A-Z]+";

    public static final String REGEX_YOUBIAN = "^[1-9]\\d{5}$";
    public static final String REGEX_MOBILE = "^((13[0-9])|(15[0-9])|(16[0-9])|(17[0-9])|(18[0-9])|(19[0-9])|(14[5|7]))\\d{8}$";
//    public static final String REGEX_EMAIL =  "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
//    public static final String REGEX_EMAIL_MIBU1 =  "^[a-zA-Z_]{1,}[0-9]{0,}@(([a-zA-z0-9]-*){1,}\\.){1,3}[a-zA-z\\-]{1,}$";
//    public static final String REGEX_EMAIL_MIBU2 = "^[A-Za-z\\d]+([-_.][A-Za-z\\d]+)*@([A-Za-z\\d]+[-.])+[A-Za-z\\d]{2,4}$";
//    public static final String REGEX_EMAIL =  "^[[a-zA-Z_]{0,}[0-9]{0,}\\-{0,}.{0,}_{0,}]{1,}@(([a-zA-z0-9]-*){1,}\\.){1,3}[a-zA-z\\-]{1,}$";
    public static final String REGEX_EMAIL =  "^[[a-zA-Z0-9_-].{0,}]{1,}@(([a-zA-z0-9]-*){1,}\\.){1,3}[a-zA-z\\-]{1,}$";
    public static final String REGEX_ID_CARD = "REGEX_ID_CARD";
    public static final String REGEX_ID_CREDIT_CODE = "REGEX_ID_CREDIT_CODE";
	
	/***
	 * 检查参数合法性
	 * @param response
	 * @param object
	 * @param paramEN
	 * @param paramCN
	 * @param isMust
	 * @param min
	 * @param max
	 * @param regEx
	 * @return
	 */
	public static boolean check(CommonResponse response, Object object, String paramEN, String paramCN, boolean isMust, BigDecimal min , BigDecimal max, String regEx, List<String> enumList){
		String paramDesc = "("+paramCN+"/"+paramEN+")";
		if(isMust && object == null){
			fail(response, paramDesc+"::参数为空，请重新输入");
			return false;
		}else if(!isMust && object == null){
			return true;
		}
		
		if(object instanceof String){
			String v = (String)object;
			if(isMust && org.apache.commons.lang3.StringUtils.isBlank(v)){
				fail(response, paramDesc+"::参数为空，请重新输入");
				return false;
			}
			if(!isMust && org.apache.commons.lang3.StringUtils.isBlank(v)) {
				return true;
			}
			if(min != null || max != null){
				int length = length(v);
				if(min != null){
					if(length < min.intValue()){
						fail(response, paramDesc+"::参数过短，最小长度："+min+",实际长度："+length);
						return false;
					}
				}
				if(max != null){
					if(length > max.intValue()){
						fail(response, paramDesc+"::参数过长，最大长度："+max+",实际长度："+length);
						return false;
					}
				}
			}
			if(org.apache.commons.lang3.StringUtils.isNotBlank(regEx)){
			   
			   if(regEx.equals(REGEX_ID_CARD)) {
//			  	 	return true;
			  	 if(!IdcardUtils.validateCard(v)) {
				    fail(response, paramDesc+"::参数格式不合法，请重新输入");
				    return false;
			  	 }
			   }else if(regEx.equals(REGEX_ID_CREDIT_CODE)) {
			  	 if(!IdcardUtils.validateCreditCode(v)) {
					    fail(response, paramDesc+"::参数格式不合法，请重新输入");
					    return false;
				  	 }
			  	 return true;
				 }else if(regEx.equals(REGEX_ZIMU_DAXIE)) {
				   String tempV = v.replaceAll(" ","").replaceAll("　","");
				   Pattern pattern = Pattern.compile(regEx);
					Matcher matcher = pattern.matcher(tempV);
					if (!matcher.matches()) {
						fail(response, paramDesc+"::参数格式不合法，请重新输入");
						return false;
					}
			   }/*else if(regEx.equals(REGEX_EMAIL)) {
						Pattern pattern1 = Pattern.compile(REGEX_EMAIL);
						Pattern pattern2 = Pattern.compile(REGEX_EMAIL_MIBU1);
						Pattern pattern3 = Pattern.compile(REGEX_EMAIL_MIBU2);
						Matcher matcher1 = pattern1.matcher(v);
						Matcher matcher2 = pattern2.matcher(v);
						Matcher matcher3 = pattern3.matcher(v);
						if (!(matcher1.matches()||matcher2.matches()||matcher3.matches())) {
							fail(response, paramDesc + "::参数格式不合法，请重新输入");
							return false;
						}
				   }*/else {
					Pattern pattern = Pattern.compile(regEx);
					Matcher matcher = pattern.matcher(v);
					if (!matcher.matches()) {
						fail(response, paramDesc + "::参数格式不合法，请重新输入");
						return false;
					}
			   }
			}
			if(enumList != null && enumList.size() > 0){
				if(!enumList.contains(v)){
					fail(response, paramDesc+"::参数格式不合法，必须是枚举值："+new Gson().toJson(enumList));
					return false;
				}
			}
		}else {
			BigDecimal v = null;
			if (object instanceof Integer) {
				Integer vv = (Integer)object;
				v = BigDecimal.valueOf(vv);
			} else if (object instanceof Long) {
				Long vv = (Long)object;
				v = BigDecimal.valueOf(vv);
			} else if (object instanceof Float) {
				Float vv = (Float)object;
				v = BigDecimal.valueOf(vv);
			} else if (object instanceof Double) {
				Double vv = (Double)object;
				v = BigDecimal.valueOf(vv);
			} else if (object instanceof BigDecimal) {
				v = (BigDecimal)object; 
			}
			
			if(min != null || max != null){
				if(min != null){
					if(v.compareTo(min) < 0 ){
						fail(response, paramDesc+"::参数过小，最小值："+min+",实际值："+v.toEngineeringString());
						return false;
					}
				}
				if(max != null){
					if(v.compareTo(max) > 0 ){
						fail(response, paramDesc+"::参数过大，最大值："+max+",实际值："+v.toEngineeringString());
						return false;
					}
				}
			}
			if(enumList != null && enumList.size() > 0){
				List<BigDecimal> newEnumList = Lists.newArrayList();
				for(String un:enumList){
					if(org.apache.commons.lang3.StringUtils.isBlank(un)){
						continue;
					}
					BigDecimal nv = new BigDecimal(un);
					newEnumList.add(nv);
				}
				if(!newEnumList.contains(v)){
					fail(response, paramDesc+"::参数格式不合法，必须是枚举值："+new Gson().toJson(enumList));
					return false; 
				}
			}

		} 
		return true;
	}

	public static void fail(CommonResponse response, String message) {
		response.setCode(1001);
		response.setMessage(message);
	}

	public static boolean isLetter(char c) {
		int k = 0x80;
		return c / k == 0 ? true : false;
	}

	/**
	 * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为3,英文字符长度为1
	 * 
	 * @param String 需要得到长度的字符串
	 * @return int 得到的字符串长度
	 */
	public static int length(String s) {
		if (s == null)
			return 0;
		char[] c = s.toCharArray();
		int len = 0;
		for (int i = 0; i < c.length; i++) {
			len++;
			if (!isLetter(c[i])) {
				len++;
				len++;
			}
		}
		return len;
	}
	public static void main(String[] args) {
		Pattern pattern1 = Pattern.compile(REGEX_EMAIL);
		Matcher matcher1 = pattern1.matcher("a12-._@163.com");
		System.out.println(matcher1.matches());
	}
}
