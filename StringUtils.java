package com.glp.collie.util;

import java.util.UUID;

public class StringUtils {
  public static String getUUIDSequence() {
    UUID uuid = UUID.randomUUID();
    String[] $uuid = uuid.toString().split("-");
    StringBuilder sb = new StringBuilder();
    for (String _uuid : $uuid) {
      sb.append(_uuid);
    }
    return sb.toString();
  }
  
  /**
   * 字符串判空
     StringUtils.isBlank(null) = true
     StringUtils.isBlank("") = true
     StringUtils.isBlank(" ") = true
     StringUtils.isBlank("        ") = true
     StringUtils.isBlank("\t \n \f \r") = true  对于制表符、换行符、换页符和回车符
     StringUtils.isBlank()   //均识为空白符
     StringUtils.isBlank("\b") = false   //"\b"为单词边界符
     StringUtils.isBlank("bob") = false
     StringUtils.isBlank(" bob ") = false
   * */
  public static boolean isBlank(String arg){
    return org.apache.commons.lang3.StringUtils.isBlank(arg);
  }
  
  public static boolean isNotBlank(String arg){
    return org.apache.commons.lang3.StringUtils.isNotBlank(arg);
  }
  
  /**
   * 字符串判空
    StringUtils.isEmpty(null) = true
    StringUtils.isEmpty("") = true 
    StringUtils.isEmpty(" ") = false 注意在 StringUtils 中空格作非空处理
    StringUtils.isEmpty("   ") = false
    StringUtils.isEmpty("bob") = false
    StringUtils.isEmpty(" bob ") = false
   * */
  public static boolean isEmpty(String arg){
    return org.apache.commons.lang3.StringUtils.isEmpty(arg);
  }
  
  public static void main(String[] args) {
    System.out.println(getUUIDSequence());
  }
}
