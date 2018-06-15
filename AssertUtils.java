package com.glp.collie.util;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.glp.collie.sdk.basic.APICode;
import com.glp.commons.api.APIException;

/**
 * Assert工具类 失败则抛出APIException
 * 
 * @author hhuang
 *
 */
public class AssertUtils {

  public static void fail(APICode apiCode, String message) {
    throw new APIException(apiCode, message);
  }

  public static void isTrue(boolean expression, APICode apiCode) {
    isTrue(expression, apiCode, null);
  }

  public static void isTrue(boolean expression, APICode apiCode, String message) {
    if (!expression)
      fail(apiCode, message);
  }

  public static void isFalse(boolean expression, APICode apiCode) {
    isFalse(expression, apiCode, null);
  }

  public static void isFalse(boolean expression, APICode apiCode, String message) {
    if (expression)
      fail(apiCode, message);
  }

  public static void isEmpty(Collection<?> collection, APICode apiCode) {
    isEmpty(collection, apiCode, null);
  }

  public static void isEmpty(Collection<?> collection, APICode apiCode, String message) {
    if (!CollectionUtils.isEmpty(collection))
      fail(apiCode, message);
  }

  public static void isNotEmpty(Collection<?> collection, APICode apiCode) {
    isNotEmpty(collection, apiCode, null);
  }

  public static void isNotEmpty(Collection<?> collection, APICode apiCode, String message) {
    if (CollectionUtils.isEmpty(collection))
      fail(apiCode, message);
  }

  public static void hasText(String text, APICode apiCode) {
    hasText(text, apiCode, null);
  }

  public static void hasText(String text, APICode apiCode, String message) {
    if (StringUtils.isBlank(text))
      fail(apiCode, message);
  }

  public static void notNull(Object object, APICode apiCode) {
    notNull(object, apiCode, null);
  }

  public static void notNull(Object object, APICode apiCode, String message) {
    if (object == null)
      fail(apiCode, message);
  }

  public static void isNull(Object object, APICode apiCode) {
    isNull(object, apiCode, null);
  }

  public static void isNull(Object object, APICode apiCode, String message) {
    if (null != object) {
      fail(apiCode, message);
    }
  }

  public static void isNotEmpty(String text, APICode apiCode, String message) {
    if (StringUtils.isBlank(text))
      fail(apiCode, message);
  }

  public static void isNotEmpty(String text, APICode apiCode) {
    isNotEmpty(text, apiCode, null);
  }

  public static void isNotDate(String text, APICode apiCode, String message) {

    if (StringUtils.isBlank(text))
      return;

    if (!ValidateUtils.isDate(text))
      fail(apiCode, message);
  }

  public static void isNotNumber(String text, APICode apiCode, String message) {

    if (StringUtils.isBlank(text))
      return;

    if (!ValidateUtils.isNumber(text))
      fail(apiCode, message);
  }
}
