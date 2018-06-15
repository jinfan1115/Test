package com.glp.collie.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.glp.collie.constant.Keys;
import com.glp.collie.model.cnmapping.CNMapping;
import com.glp.collie.model.material.Material;
import com.glp.collie.model.material.MaterialCompart;
import com.glp.collie.service.cnmapping.CNMappingService;
import com.glp.collie.service.config.ConfigDao;
import com.glp.collie.service.material.MaterialService;
import com.google.common.collect.Maps;

public class FastjsonArrayUtils extends FastjsonUtils {
	
	private CNMappingService cnMappingService;
	private MaterialService materialService;
	private ConfigDao configDao;
	
	public void setCnMappingService(CNMappingService cnMappingService) {
		this.cnMappingService = cnMappingService;
	}
	public void setMaterialService(MaterialService materialService) {
		this.materialService = materialService;
	}
	public void setConfigDao(ConfigDao configDao) {
		this.configDao = configDao;
	}
	
	@SuppressWarnings("rawtypes")
	private List insertList;
    @SuppressWarnings("rawtypes")
	private List deleteList;
    @SuppressWarnings("rawtypes")
	private List updateList;
    private Map<String,Object> gmap = new ConcurrentHashMap<String,Object>();
    @SuppressWarnings("rawtypes")
	public FastjsonArrayUtils() {
      insertList = new ArrayList();
      deleteList = new ArrayList();
      updateList = new ArrayList();
    }
    
    Set<String> setStr = new HashSet<String>();
    ThreadLocal<String>  configStr = new ThreadLocal<String>();
    public Map<String,Object> compartr(Map<String,Object> map,String key,Object o1,Map<String,Object> mapc,Object o2,boolean base){
    	if(configStr==null || configStr.get()==null){
    		Map<String, String> configs = configDao.getGroupConfigMap(Keys.COLLIE_IGNORE_KEY);
    	    configStr.set(configs.get(Keys.COLLIE_IGNORE_KEY));
    	    setStr = new HashSet<String>(Arrays.asList(configStr.get().split("\\|")));
    	}
    	if(setStr.contains(key)){
    		return map;
    	}
    	if(gmap.isEmpty()){
    		List<CNMapping> gList = cnMappingService.selectKeyValueByGroup(Keys.MGROUP);
    		gmap = listToMap(gList);
    	}
    	if(o1==null || !o1.equals(o2)){ 
			if("url".equals(key)){
				String[] ao1 = o1==null?null:o1.toString().split("\\|");
				String[] ao2 = o2.toString().split("\\|");
				Set<String> s01 = new HashSet<String>(Arrays.asList(ao1));
				Set<String> s02 = new HashSet<String>(Arrays.asList(ao2));
				Set<String> s011 = new HashSet<String>();
				Set<String> s012 = new HashSet<String>();
				Map<String,Object> ma = new HashMap<String,Object>();
				String na = "";
				for (String str : s01) {
					String nm = str.split("\\:\\:")[0];
					if(nm.indexOf("#")==-1){
						s011.add(nm);
					}
					else{
						String [] arra = nm.split("\\#");
						na = (String) mapc.get(arra[0]);
						if(arra.length<=1) continue;
						s011.add(arra[1]);
					}
				}
				for (String str : s02) {
					String nm = str.split("\\:\\:")[0];
					if(nm.indexOf("#")==-1){
						s012.add(nm);
					}
					else{
						String [] arra = nm.split("\\#");
						na = (String) mapc.get(arra[0]);
						if(arra.length<=1) continue;
						s012.add(arra[1]);
					}
				}
				if(s011.isEmpty() && !s012.isEmpty()){
					ma.put("insert", na+"#"+s012.toString());
				}
				if(!s011.isEmpty() && s012.isEmpty()){
					ma.put("delete", na+"#"+s011.toString());
				}
				if(!s011.isEmpty() && !s012.isEmpty()){
					Set<String> result = new HashSet<String>();
					result.addAll(s011);
					result.removeAll(s012);
					if (!result.isEmpty())
						ma.put("delete", na+"#"+result.toString());
					result.clear();
					result.addAll(s012);
					result.removeAll(s011);
					if(!result.isEmpty())
						ma.put("insert", na+"#"+result.toString());
				}
				map.putAll(ma);
			}
			else if ("guaranteeMeasure".equals(key)){
				String[] ao1 = o1==null?null:o1.toString().split(",");
				Set<String> s01 = new HashSet<String>(Arrays.asList(ao1));
				StringBuffer sb = new StringBuffer();
				String na = (String) mapc.get(key);
				sb.append(na+"#");
				for (String str : s01) {
					sb.append(gmap.get(str)==null?o1:gmap.get(str)+",");
				}
				map.put(key, sb);
			}
			else{
				String s1 = o1.toString();
				if(s1.indexOf(",")!=-1){
					String[] sa1 = s1.split(",");
					StringBuffer sb = new StringBuffer();
					sb.append(mapc.get(key)==null?"":(mapc.get(key)+"#"));
					for (int i=0;i<sa1.length;i++) {
						sb.append(gmap.get(sa1[i])==null?(i==0?sa1[i]:(","+sa1[i])):(i==0?gmap.get(sa1[i]):(","+gmap.get(sa1[i]))));
					}
					map.put(key, sb);
				}
				else
					map.put(key, mapc.get(key)==null?(gmap.get(o1)==null?o1:gmap.get(o1)):mapc.get(key)+"#"+(gmap.get(o1)==null?o1:gmap.get(o1)));
			}
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
	@SuppressWarnings("unchecked")
	public Map<String, Object> compartr(Map<String, Object> map, String key, JSONArray ja1, Map<String, Object> mapc, JSONArray ja2) {
		if(configStr==null || configStr.get()==null){
    		Map<String, String> configs = configDao.getGroupConfigMap(Keys.COLLIE_IGNORE_KEY);
    	    configStr.set(configs.get(Keys.COLLIE_IGNORE_KEY));
    	    setStr = new HashSet<String>(Arrays.asList(configStr.get().split("\\|")));
    	}
    	if(setStr.contains(key)){
    		return map;
    	}
    	List<CNMapping> gList = cnMappingService.selectKeyValueByGroup(Keys.MGROUP);
		gmap = listToMap(gList);
		//ja1、ja2的list
		List<?> cl1;
		List<?> cl2;
		String className,id,type;
		if ("creditLine".equals(key)){
			clear();
			id = "id";
			className = "com.glp.collie.model.credit.JSON2CreditLine";
			type = "creditLine";
		}else if ("materialList".equals(key)){
			clear();
			className = "com.glp.collie.model.material.MaterialCompart";
			id = "materialId";
			type = "materialList";
		} else if ("attributeList".equals(key)){
			clear();
			className = "com.glp.collie.model.attributeValue.AttributeValueJson";
			id = "attributeDefinitionId";
			type = "attributeList";
		}  
//		else if ("creditLineGuarantor".equals(key)){
//			clear();
//			className = "com.glp.collie.model.credit.JSON2Guarantor";
//			id = "idNumber";
//			type = "creditLineGuarantor";
//		}
		else {
			if(ja1.toString().equals(ja2.toString())){
				return map;
			}
			map.put(key, "有修改#"+ja1);
			return map;
		}
		//根据反射生成类实例
		Class<?> clazz = super.getClassByName(className);
		cl1 = super.JSONArray2JavaList(ja1,clazz);
		cl2 = super.JSONArray2JavaList(ja2,clazz);
		if ("creditLine".equals(key)){
			JSONArray jsc1 = super.list2JSONArray(cl1);
			JSONArray jsc2 = super.list2JSONArray(cl2);
			JSONArray jsa1 = new JSONArray();
			JSONArray jsa2 = new JSONArray();
			for (Object obj : jsc1) {
				JSONObject json = super.str2JSONObject(obj.toString());
				if(json.get("modifiedCreditId")==null) 
					jsa1.add(json);
				else{
					json.put("id", json.get("modifiedCreditId"));
					jsa1.add(json);
				}
			}
			cl1 = super.JSONArray2JavaList(jsa1,clazz);
			super.JSONArray2JavaList(ja1,clazz);
			for (Object obj : jsc2) {
				JSONObject json = super.str2JSONObject(obj.toString());
				if(json.get("modifiedCreditId")==null)
					jsa2.add(json);
				else{
					json.put("id", json.get("modifiedCreditId"));
					jsa2.add(json);
				}
			}
			cl2 = super.JSONArray2JavaList(jsa2,clazz);
			
		}
		compartr(cl1, cl2);
		Map<String,Object> tmap = new TreeMap<String,Object>();
		if(!insertList.isEmpty()){
			JSONArray jsona = super.list2JSONArray(insertList);
			JSONArray jsa = new JSONArray();
			if("creditLine".equals(key)){
				for (Object obj : jsona) {
					JSONObject json = super.str2JSONObject(obj.toString());
					json.put("relationType", gmap.get(json.get("relationType")));
					json.put("guaranteeMeasure", gmap.get(json.get("guaranteeMeasure")));
					json.put("productLine", gmap.get(json.get("productLine")));
					json.put("type", gmap.get(json.get("type")));
					json.put("currency", gmap.get(json.get("currency")));
					json.put("bizType", gmap.get(json.get("bizType")));
					jsa.add(json);
				}
				tmap.put("insert", jsa);
			}else if ("creditLineGuarantor".equals(key)){
				for (Object obj : jsona) {
					JSONObject json = super.str2JSONObject(obj.toString());
					json.put("content", gmap.get(json.get("content")));
					json.put("type", gmap.get(json.get("type")));
					jsa.add(json);
				}
				tmap.put("insert", jsa);
			}
			else{
				tmap.put("insert", jsona);
			}
		}
		if(!deleteList.isEmpty()){
			JSONArray jsona = super.list2JSONArray(deleteList);
			tmap.put("delete", jsona);
//			JSONArray jsa = new JSONArray();
//			if("creditLine".equals(key)){
//				for (Object obj : jsona) {
//					JSONObject json = super.str2JSONObject(obj.toString());
//					json.put("relationType", gmap.get(json.get("relationType")));
//					json.put("guaranteeMeasure", gmap.get(json.get("guaranteeMeasure")));
//					json.put("productLine", gmap.get(json.get("productLine")));
//					json.put("type", gmap.get(json.get("type")));
//					json.put("currency", gmap.get(json.get("currency")));
//					json.put("bizType", gmap.get(json.get("bizType")));
//					jsa.add(json);
//				}
//				tmap.put("delete", jsa);
//			}else if ("creditLineGuarantor".equals(key)){
//				for (Object obj : jsona) {
//					JSONObject json = super.str2JSONObject(obj.toString());
//					json.put("content", gmap.get(json.get("content")));
//					json.put("type", gmap.get(json.get("type")));
//					jsa.add(json);
//				}
//				tmap.put("delete", jsa);
//			}
//			else{
//				tmap.put("delete", jsona);
//			}
		}
		//取交集
		cl1.retainAll(updateList);
		cl2.retainAll(updateList);
		Map<String,Object> param = null;
		if ("materialList".equals(key)){
			handleMaterialList((List<MaterialCompart>)cl1);
			handleMaterialList((List<MaterialCompart>)cl2);
			List<Material> listM = materialService.getMaterialList();
			param = listToMapM(listM);
		}
		else{
			List<CNMapping> list = cnMappingService.selectKeyValueByType(type);
			param = listToMap(list);
		}
		Map<Object, Object> cmap = new TreeMap<Object, Object>();
		Map<Object, Object> other = new TreeMap<Object, Object>();
		Map<Object, Object> pious = new TreeMap<Object, Object>();
		Map<Object, Object> rm = new TreeMap<Object, Object>();
		Map<Object, Object> cmc = new TreeMap<Object, Object>();
		Map<Object, Object> asset = new TreeMap<Object, Object>();
		Map<Object, Object> basic = new TreeMap<Object, Object>();
		Map<Object, Object> financial = new TreeMap<Object, Object>();
		for (Object c : cl1) {
			Object cl = cl2.get(cl2.indexOf(c));
			Map<String,Object> pmap = new TreeMap<String,Object>();
			super.compartr(pmap, super.java2JSONOject(c), param,super.java2JSONOject(cl));
			JSONObject json = super.java2JSONOject(cl);
			if(!pmap.isEmpty()){
				if ("materialList".equals(type) && "OTHER".equals(json.get("type"))){
					other.put(json.get(id)==null?key:json.get(id), pmap);
				}
				else if ("materialList".equals(type) && "PIOUS".equals(json.get("type"))){
					pious.put(json.get(id)==null?key:json.get(id), pmap);
				}
				else if ("materialList".equals(type) && "RM".equals(json.get("type"))){
					rm.put(json.get(id)==null?key:json.get(id), pmap);
				}
				else if ("materialList".equals(type) && "CMC".equals(json.get("type"))){
					cmc.put(json.get(id)==null?key:json.get(id), pmap);
				}
				else if ("materialList".equals(type) && "ASSET".equals(json.get("type"))){
					asset.put(json.get(id)==null?key:json.get(id), pmap);
				}
				else if ("materialList".equals(type) && "BASIC".equals(json.get("type"))){
					basic.put(json.get(id)==null?key:json.get(id), pmap);
				}
				else if ("materialList".equals(type) && "FINANCIAL".equals(json.get("type"))){
					financial.put(json.get(id)==null?key:json.get(id), pmap);
				}
				else
					cmap.put(json.get(id)==null?key:json.get(id), pmap);
			}
		}
		if("materialList".equals(type)){
			if(!other.isEmpty())
				cmap.put("OTHER", other);
			if(!pious.isEmpty())
				cmap.put("PIOUS", pious);
			if(!rm.isEmpty())
				cmap.put("RM", rm);
			if(!cmc.isEmpty())
				cmap.put("CMC", cmc);
			if(!asset.isEmpty())
				cmap.put("ASSET", asset);
			if(!basic.isEmpty())
				cmap.put("BASIC", basic);
			if(!financial.isEmpty())
				cmap.put("FINANCIAL", financial);
		}
		if (!cmap.isEmpty()){
			tmap.put("update", cmap);
		}
		if(!tmap.isEmpty())
			map.put(key, tmap);
		return map;
	}
	
	public void clear(){
		insertList.clear();
		deleteList.clear();
		updateList.clear();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void compartr(List l1,List l2){
		if (l1 == null && l2 == null)
	      return;
	    // 全部删除
	    if (l2.isEmpty()) {
	    	deleteList = l1;
	    	return;
	    }
	    // 全部新增
	    if (l1.isEmpty()) {
	    	insertList = l2;
	    	return;
	    }
	    // 有新增+更新+删除
	    for (Object obj : l2) {
	      // 更新的
	      if (l1.contains(obj))
	    	  updateList.add(obj);
	      else
	    	  insertList.add(obj);// 新增的
	    }
	    // 要删的
	    for (Object obj : l1)
	      if (!l2.contains(obj))
	    	  deleteList.add(obj);
	}
	
	
	/** 
	 * 将对象装换为map 
	 * @param bean 
	 * @return 
	 */  
	public Map<String, Object> beanToMapM(Material bean) {  
	    Map<String, Object> map = Maps.newHashMap();  
	    if (bean != null) {  
            map.put(bean.getId()+"", bean.getName());  
	    }  
	    return map;  
	}  
	  
	/**
	 * 将java list 转成Map
	 * @param objList
	 * @return
	 */
	 
	public Map<String, Object> listToMapM(List<Material> objList) {  
		Map<String, Object> root = Maps.newHashMap();  
	    if (objList != null && objList.size() > 0) {  
	        Map<String, Object> map = null;  
	        Material bean = null;  
	        for (int i = 0,size = objList.size(); i < size; i++) {  
	            bean = objList.get(i);  
	            map = beanToMapM(bean);  
	            root.putAll(map);  
	        }  
	    }  
	    return root;  
	}
	
	public List<MaterialCompart> handleMaterialList(List<MaterialCompart> list){
		for (MaterialCompart mat : list) {
			mat.setUrl(mat.getMaterialId()+"#"+(mat.getUrl()==null?"":mat.getUrl()));
		}
		return list;
	}
	

	/** 
	 * 将对象装换为map 
	 * @param bean 
	 * @return 
	 */  
	public Map<String, Object> beanToMap(CNMapping bean) {  
	    Map<String, Object> map = Maps.newHashMap();  
	    if (bean != null) {  
            map.put(bean.getKey()+"", bean.getValue());  
	    }  
	    return map;  
	}  
	  
	/**
	 * 将java list 转成Map
	 * @param objList
	 * @return
	 */
	 
	public Map<String, Object> listToMap(List<CNMapping> objList) {  
		Map<String, Object> root = Maps.newHashMap();  
	    if (objList != null && objList.size() > 0) {  
	        Map<String, Object> map = null;  
	        CNMapping bean = null;  
	        for (int i = 0,size = objList.size(); i < size; i++) {  
	            bean = objList.get(i);  
	            map = beanToMap(bean);  
	            root.putAll(map);  
	        }  
	    }  
	    return root;  
	}
}