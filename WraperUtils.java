package com.glp.collie.util;

import com.glp.collie.constant.Constants;

/**
 * 包装类工具
 * @author xpeng
 *
 */
public class WraperUtils {

  public static Integer parseInteger(String text) {
    if (ValidateUtils.isEmpty(text))
      return null;
    
    if (ValidateUtils.isInt(text = text.trim()))
      return Integer.parseInt(text);
    
    return null;
  }
  
  public static Double parseDouble(String text) {
    if (ValidateUtils.isEmpty(text))
      return null;
    
    if (ValidateUtils.isNumber(text = text.trim()))
      return Double.parseDouble(text);
    
    return null;
  }
  
  public static String dateFormat(String text) {
    if (ValidateUtils.isEmpty(text))
      return null;
    
    if (!ValidateUtils.isDate(text = text.trim()))
      return null;
    
    text = text.replaceAll(Constants.SEPARATOR_SLANT, Constants.SEPARATOR_MINUS);
    text = text.replaceAll(Constants.DATE_TIME_FORMAT_REGEXP, "$1");
    return text;
  }
  
  
}
