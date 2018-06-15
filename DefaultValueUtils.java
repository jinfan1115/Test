package com.glp.collie.util;

/**
 * 缺省值
 * @author xpeng
 *
 */
public class DefaultValueUtils {

  public static double ifNull(Double value) {
    if (value == null)
      return 0d;
    
    return value;
  }
  
}
