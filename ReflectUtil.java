package com.glp.collie.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectUtil {

  public static Object invoke(Object o, String methodName, Object[] parameterValue, Class<?>... parameterType)
      throws Exception {
    Class<?> clazz = o.getClass();
    Method m = clazz.getMethod(methodName, parameterType);
    return m.invoke(o, parameterValue);
  }
  
  public static Object invoke(Class<?> clazz, String methodName, Object[] parameterValue, Class<?>... parameterType)
      throws Exception {
    Method m = clazz.getMethod(methodName, parameterType);
    return m.invoke(null, parameterValue);
  }

  
  public static void main(String[] args) throws Exception {
    Object objct = ReflectUtil.invoke(ValidateUtils.class, "equals", new Object[] {
        new ArrayList<Boolean>(Arrays.asList(false, true)), "01" }, List.class, String.class);
    System.out.println(objct.toString());
  }

  public static void main_1(String[] args) throws Exception {
    Object objct = ReflectUtil.invoke(ValidateUtils.class, "equals", new Object[] {
        new ArrayList<Boolean>(Arrays.asList(true, true)), "01" }, List.class, String.class);
    System.out.println(objct.toString());
  }
}
