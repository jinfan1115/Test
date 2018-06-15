package com.glp.collie.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.StringUtils;

/**
 * 字串格式化工具
 * 
 * @author xpeng
 * @date 2017-10-13
 */
public class StringFormatUtils {
  
  private static final Map<String, String> DBC_CASE_MAPPING = new HashMap<>();
  static {
    DBC_CASE_MAPPING.put("（", "(");
    DBC_CASE_MAPPING.put("）", ")");
  }

  /**
   * 半角格式化
   * @param text
   * @return
   */
  public static String formatDBCCase(String text) {
    
    if (StringUtils.isEmpty(text))
      return text;
    
    for (Entry<String, String> entry : DBC_CASE_MAPPING.entrySet())
      text = text.replaceAll(entry.getKey(), entry.getValue());
    
    return text;
  }
  
  /**
   * 身份证号转大写
   * @param idNumber
   * @return
   */
  public static String formatIDNumber(String idNumber) {
    
    if (StringUtils.isEmpty(idNumber))
      return idNumber;
    
    idNumber = idNumber.toUpperCase();
    
    return idNumber;
  }
  
  public static void main(String[] xx) {
    System.out.println(formatDBCCase("启航五洲（天津）国际贸易有限公司"));
    System.out.println(formatIDNumber("13000019880520885x"));
  }
}
