package com.glp.collie.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class FastjsonPKUtils extends FastjsonUtils {
	
	/**
	 * 
	 */
    public Map<String,Object> compartr(Map<String,Object> map,String key,Object o1,Map<String,Object> mapc,Object o2,boolean base){
    	String[] sta = {"id","updateTime","updateBy","rejectFieldList","procDefKey","status","procInstId","updateBy","createTime","reject"};
    	Set<String> set = new HashSet<String>(Arrays.asList(sta));
    	if(set.contains(key) || (map.get("isUpdate")!=null && map.get("isUpdate").equals(1))){
    		return null;
    	}
    	if(!o1.equals(o2)){ 
			map.put("isUpdate", 1);
		}
		return map;
	}
    
  /**
    *比较出两个对象的增、删、改
    *@param map 比较结果
    *@param key 节点名 
    *@param ja1 源对象
    *@param ja2 目标对象
    * */
	public Map<String, Object> compartr(Map<String, Object> map, String key, JSONArray ja1, Map<String, Object> mapc, JSONArray ja2) {
		String[] sta = {"id","creditLineLeaseTradeType","creditLineZPX","creditLinePurchase","creditLineLease","creditLineLoan","creditLinePledge","approveLog","taskDefList","creditLineFactoring","rejectFieldList"};
    	
		Set<String> set = new HashSet<String>(Arrays.asList(sta));
		if(set.contains(key) || (map.get("isUpdate")!=null && map.get("isUpdate").equals(1))){
    		return null;
    	}
		
		if(("creditLine".equals(key) || "creditLineFactoring".equals(key)) && ja1!=null){
			JSONArray jsa1 = new JSONArray();
			JSONArray jsa2 = new JSONArray();
			for (Object object : ja1) {
				JSONObject json = super.object2JSONObject(object);
				json.remove("status");
				json.remove("updateTime");
				jsa1.add(json);
			}
			ja1 = jsa1;
			for (Object object : ja2) {
				JSONObject json = super.object2JSONObject(object);
				json.remove("status");
				json.remove("updateTime");
				jsa2.add(json);
			}
			ja2 = jsa2;
		}
		else if ("materialList".equals(key) || "attributeList".equals(key)){
			JSONArray jsa1 = new JSONArray();
			JSONArray jsa2 = new JSONArray();
			for (Object object : ja1) {
				JSONObject json = super.object2JSONObject(object);
				json.remove("id");
				jsa1.add(json);
			}
			ja1 = jsa1;
			for (Object object : ja2) {
				JSONObject json = super.object2JSONObject(object);
				json.remove("id");
				jsa2.add(json);
			}
			ja2 = jsa2;
		}
		if(!ja1.toString().equals(ja2.toString())){
			map.put("isUpdate", 1);
		}
		return map;
	}
}