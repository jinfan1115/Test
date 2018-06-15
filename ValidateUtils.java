package com.glp.collie.util;

import java.util.List;

import org.springframework.util.StringUtils;

import com.glp.collie.constant.Constants;
import com.glp.collie.constant.Constants.CustomerType;
import com.glp.collie.constant.CreditConstants.GuaranteeMeasureType;
import com.glp.collie.sdk.basic.APICode;

public class ValidateUtils {

  public static boolean isEmpty(String text) {
    return StringUtils.isEmpty(text) || StringUtils.isEmpty(text.trim());
  }

  public static boolean isDate(String text) {

    if (isEmpty(text))
      return false;

    return text.matches(Constants.DATE_TIME_FORMAT_REGEXP);
  }

  public static boolean isNumber(String text) {
    if (isEmpty(text))
      return false;

    return text.matches(Constants.NUMBER_REGEXP);
  }

  public static boolean isInt(String text) {
    if (isEmpty(text))
      return false;

    return text.matches(Constants.INT_REGEXP);
  }

  public static boolean notEmpty(String text) {
    return !isEmpty(text);
  }

  public static boolean equals(List<Boolean> list, String target) {
    boolean left = list == null || list.isEmpty();
    boolean right = target == null || target.isEmpty();
    if (left != right) {
      return false;
    }
    if (left) {
      return true;
    }
    AssertUtils.isTrue(list.size() == target.length(), APICode.ERROR);
    AssertUtils.isFalse(list.contains(null), APICode.ERROR);
    AssertUtils.isTrue(target.matches("[01]+"), APICode.ERROR);
    StringBuilder sb = new StringBuilder();
    for (Boolean b : list) {
      if (b) {
        sb.append(1);
      } else {
        sb.append(0);
      }
    }
    left = sb.toString().equals(target);
    return left;
  }

  public static boolean in(List<Boolean> list, String targets) {
    boolean left = list == null || list.isEmpty();
    boolean right = targets == null || targets.isEmpty();
    if (left != right) {
      return false;
    }
    if (left) {
      return true;
    }
    String[] targetArray = targets.split(",");
    int size = 0;
    for (int i = 0; i < targetArray.length; i++) {
      if (i == 0) {
        size = targetArray[i].length();
      } else {
        AssertUtils.isTrue(targetArray[i].length() == size, APICode.ERROR);
      }
    }
    AssertUtils.isTrue(list.size() == size, APICode.ERROR);
    AssertUtils.isFalse(list.contains(null), APICode.ERROR);
    for (int i = 0; i < targetArray.length; i++) {
      AssertUtils.isTrue(targetArray[i].matches("[01]+"), APICode.ERROR);
    }
    StringBuilder sb = new StringBuilder();
    for (Boolean b : list) {
      if (b) {
        sb.append(1);
      } else {
        sb.append(0);
      }
    }
    String str = sb.toString();
    for (int i = 0; i < targetArray.length; i++) {
      if (str.equals(targetArray[i])) {
        return true;
      }
    }
    return false;
  }

  public static boolean and(List<Boolean> list) {
    boolean result = true;
    for (Boolean _result : list) {
      result &= _result;
      if (!result) {
        return false;
      }
    }
    return true;
  }

  public static boolean or(List<Boolean> list) {
    boolean result = false;
    for (Boolean _result : list) {
      result |= _result;
      if (result) {
        return true;
      }
    }
    return false;
  }

  public static String orToSequence(List<Boolean> list) {
    if (or(list)) {
      return "1";
    } else {
      return "0";
    }
  }

  public static String convertToSequence(List<Boolean> list) {
    StringBuilder sb = new StringBuilder();
    for (Boolean b : list) {
      if (b == null) {
        sb.append(2);
      } else if (b) {
        sb.append(1);
      } else {
        sb.append(0);
      }
    }
    String str = sb.toString();
    return str;
  }

  public static boolean isGuaranteeMeasureAvail(String guaranteeMeasure) {
    if (StringUtils.isEmpty(guaranteeMeasure)) {
      return false;
    }
    for (GuaranteeMeasureType guaranteeMeasureType : GuaranteeMeasureType.values()) {
      if (guaranteeMeasure.indexOf(guaranteeMeasureType.getName()) != -1) {
        return true;
      }
    }
    return false;
  }

  public static boolean yes(Integer value) {
    return value == null ? false : value.intValue() == Constants.INT_YES;
  }

  public static boolean checkCustomerCodeAndType(String text) {
    if (CustomerType.CUSTOMER_TYPE_PERSON.getName().equals(text)) {
      return true;
    } else if (CustomerType.CUSTOMER_TYPE_COMPANY.getName().equals(text)) {
      return false;
    }
    if (text == null || text.isEmpty()) {
      return false;
    } else {
      return true;
    }
  }
  
}
