package com.glp.collie.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


public abstract class FastjsonUtils {
	
	public JSONObject str2JSONObject(String jsonString){
		return JSON.parseObject(jsonString);
	}
	
	public String object2JSONString(Object o){
		return JSON.toJSONString(o);
	}
	
	public JSONObject object2JSONObject(Object o){
		return str2JSONObject(object2JSONString(o));
	}
	
	public JSONArray list2JSONArray(List<?> insertList){
		return JSON.parseArray(object2JSONString(insertList));
	}
	
	public JSONObject java2JSONOject(Object o){
		return str2JSONObject(object2JSONString(o));
	}
	
	public <T> T object2Java(Object json,Class<T> clazz){
		return jsonObject2Java((JSONObject)json, clazz);
	}
	
	public <T> T jsonObject2Java(JSONObject json,Class<T> clazz){
		return JSON.toJavaObject(json, clazz);
	}
	
	public <T> List<T> JSONArray2JavaList(JSONArray array,Class<T> clazz){
		return array.toJavaList(clazz);
	}
	//Ignore	
	public Map<String,Object> compartr(Map<String,Object> map,JSONObject jo1,Map<String,Object> mapc,JSONObject jo2,Set<String> set){
		Set<String> sk1 = jo1.keySet();
        for (String key : sk1) {
        	if(set.contains(key)){
        		continue;
        	}
        	Object o1 = jo1.get(key);
        	Object o2 = jo2.get(key);
        	compartr(map,key,o1,mapc,o2);
		}
		return map;
	}
	//Include
	public Map<String,Object> compartr(Set<String> set,Map<String,Object> map,JSONObject jo1,Map<String,Object> mapc,JSONObject jo2){
		Set<String> sk1 = jo1.keySet();
        for (String key : sk1) {
        	if(!set.contains(key)){
        		continue;
        	}
        	Object o1 = jo1.get(key);
        	Object o2 = jo2.get(key);
        	compartr(map,key,o1,mapc,o2);
		}
		return map;
	}
	public Map<String,Object> compartr(Map<String,Object> map,JSONObject jo1,Map<String,Object> mapc,JSONObject jo2){
		Set<String> sk1 = jo2.keySet();
        for (String key : sk1) {
        	Object o1 = jo1.get(key)==null?"":jo1.get(key);
        	Object o2 = jo2.get(key);
        	compartr(map,key,o1,mapc,o2);
		}
		return map;
	}
	
	public Map<String,Object> compartr(Map<String,Object> map,String key,Object o1,Map<String,Object> mapc, Object o2) {  
    	if (o1 instanceof JSONObject && o2 instanceof JSONObject) {  
        	return compartr(map,(JSONObject) o1, mapc, (JSONObject) o2);  
        }  else if (o1  instanceof JSONArray && o2 instanceof JSONArray) {  
        	return compartr(map,key,(JSONArray) o1,mapc, (JSONArray) o2);  
        }  else if (o1 == null && o2 == null) {  
        	return null;  
        }    else {
        	return compartr(map,key,o1,mapc,o2,true);
        }
    }
	
	public abstract Map<String,Object> compartr(Map<String,Object> map,String key,Object o1,Map<String,Object> mapc,Object o2,boolean base);
	
	public abstract Map<String,Object> compartr(Map<String,Object> map,String key,JSONArray ja1,Map<String,Object> mapc,JSONArray ja2);
	
	public <T> Class<?> getClassByName(String className){
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
