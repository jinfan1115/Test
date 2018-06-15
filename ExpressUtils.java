package com.glp.collie.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpressUtils {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(ExpressUtils.class);
  
  private static final String EXPRESS_LANGUAGE = "js";
  private static final ScriptEngineManager SCRIPT_ENGINE_MANAGER = new ScriptEngineManager();
  private static final ScriptEngine SCRIPT_ENGINE = SCRIPT_ENGINE_MANAGER.getEngineByName(EXPRESS_LANGUAGE);
  
  public static Object eval(String express,Map<String, Object> parameter) {
    if (express == null)
      return null;
    
    try {
      if (!MapUtils.isEmpty(parameter))
        for (Entry<String, Object> entry : parameter.entrySet())
          SCRIPT_ENGINE.put(entry.getKey(), entry.getValue());
      
      return SCRIPT_ENGINE.eval(express);
    } catch (Exception e) {
      LOGGER.error(null, e);
      return null;
    }
  }

  public static boolean isTrue(String express,Map<String, Object> parameter) {
      return Boolean.TRUE.equals(eval(express, parameter));
  }
  
  public static void main(String[] xxx) {
    
    Map<String, Object> param = new HashMap<String, Object>(1);
    param.put("str", "test!");
    param.put("a", 1);
    param.put("isOK", true);
    
    System.out.println(isTrue("str.equals('test!') && a==1 && isOK", param));
  }
}
