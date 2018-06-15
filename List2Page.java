package com.glp.collie.util;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**
 * 对List结果集按照分页返回
 * @author xinli
 *
 */
public class List2Page {
	
	private List2Page() {

	}
	/**
	 * 对List结果集分页返回
	 * @param reqList 待分页结果集
	 * @param req_page 第几页
	 * @param req_size 每页显示数
	 * @return
	 */
	public static Object subList(List<?> reqList, int req_page, int req_size) {
		if (CollectionUtils.isEmpty(reqList)) {
			return null;
		}
		// 1、验证请求参数的page和size
		req_page = req_page <= 0 ? 1 : req_page;
		req_size = req_size <= 0 ? 0 : req_size;

		if (((req_page - 1) * req_size) > reqList.size()) {
			return null;
		}

		int end = (req_page * req_size) > reqList.size() ? reqList.size()
				: (req_page * req_size);
		// 2、截取分页数据
		List<?> resultList = reqList.subList((req_page - 1) * req_size, end);
		return resultList;
	}

}
