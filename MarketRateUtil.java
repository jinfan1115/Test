package com.glp.collie.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
/**
 * 汇率工具类
 * 从国家外汇管理局获取最新汇率
 * @author hwang1
 */
public class MarketRateUtil {
	/**
	 * 币种代码、名称对照
	 */
	public static final HashMap<String,String> CURRENCY_CODE=new HashMap<String,String>(){
	  
    private static final long serialVersionUID = 5299067476871177564L;

  {
			put("美元","USD");
			put("加元","CAD");
			put("澳元","AUD");
			put("欧元","EUR");
			put("英镑","GBP");
			put("港元","HKD");
			put("日元","JPY");
	}};
//	public static final HashMap<String,String> CURRENCY_CODE=new HashMap<String,String>(){{
//		put("美元","USD");
//		put("欧元","EUR");
//		put("日元","JPY");
//		put("港元","HKD");
//		put("英镑","GBP");
//		put("瑞士法郎","CHF");
//		put("新加坡元","SGD");
//		put("新西兰元","NZD");
//		put("加元","CAD");
//		put("澳元","AUD");
//		//菲律宾比索
//		put("比索","PHP");
//		//意大利里拉
//		put("里拉","ITL");
//		put("挪威克朗","NOK");
//		put("瑞典克朗","SEK");
//		put("丹麦克朗","DKK");
//		//波兰兹罗提
//		put("兹罗提","PLN");
//		//匈牙利福林
//		put("福林","HUF");
//		//伊朗里亚尔，给一个，有太多了
//		put("里亚尔","IRR");
//		put("林吉特","MYR");
//		put("卢布","RUB");
//		put("兰特","ZAR");
//		put("韩元","KRW");
//		//阿联酋迪拉姆
//		put("迪拉姆","AED");
//	}};
	
	public static void main(String[] args) {
		String rateInfo=getRateInfo();
		System.out.println(rateInfo);
	}
	
	/**
	 * 根据业务需求，只给出其中币种汇率，超出则直接提示前台币种异常
	 * "美元","USD");
	   "加元","CAD");
	   "澳元","AUD");
	   "欧元","EUR");
	   "英镑","GBP");
	   "港元","HKD");
	   "日元","JPY");
	 * @return
	 */
	public static String getRateInfo(){
		String rateInfo="";
		String rateHtmlStr="";
		try {
			/*
			 * 正式上生产请放开
			 */
		  rateHtmlStr=fetch();
			//抓取测试用
//			rateHtmlStr=fetchTest();
			
		  
			rateInfo=parseHtml(rateHtmlStr);
		} catch (Exception e) {
			//TODO 增加日志,获取汇率失败
			e.printStackTrace();
		}
		return rateInfo;
	}

	/**
	 * 抓取测试，获取本地html文件
	 * @return
	 * @throws Exception 
	 */
	private static String fetchTest() throws Exception {
		File file = new File("C:/myGLP/file/rate.html");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);

		String s;
		StringBuffer sbf = new StringBuffer();
		while ((s = br.readLine()) != null) {
			sbf.append(s);
		}

		fr.close();
		return sbf.toString();
	}

	/**
	 * 从国家外汇管理局获取最新汇率
	 * @return
	 * @throws Exception
	 */
	private static String fetch() throws Exception {
		DateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
		String startDate = dt1.format(new Date());
		String endDate = dt1.format(new Date());
		
		String urlString = "http://www.safe.gov.cn/AppStructured/view/project!RMBQuery.action?projectBean.startDate="
				+ startDate + "&projectBean.endDate=" + endDate;
		
		StringBuffer html = new StringBuffer();
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		String temp;
		while ((temp = br.readLine()) != null) {
			html.append(temp).append("\n");
		}
		br.close();
		isr.close();
		return html.toString();
	}
	
	/**
	 * 解析从国家外汇管理局获取最新汇率html文件
	 * @param allHTML
	 * @return
	 */
	private static String parseHtml(String allHTML) {
		String[] rows = allHTML.split(
				"<tr class=\"first\" onMouseover='this.style.backgroundColor=\"#fffcbf\"' onMouseout='this.style.backgroundColor=\"#eff6fe\"'>");
		int rownumber = rows.length - 1;
		String currentRow = null;

		String[] titleHTMLs = null;
		String[] titSubHTMLs = null;

		String[] rateHTMLs = null;
		String[] rateSubHTMLs = null;

		StringBuffer sbf = new StringBuffer();

		String strDate = null;
		//外币Code
		String cCode="";
		for (int i = 1; i <= rownumber; i++) {
			currentRow = rows[0];
			titleHTMLs = currentRow
					.split("<th width=\"8%\" class=\"table_head\" style=\"cursor:'default'\" id=\"comtemplatename\" >");
			currentRow = rows[i];
			rateHTMLs = currentRow.split("<td width=\"8%\" align=\"center\"  >");

			rateSubHTMLs = rateHTMLs[1].split("&nbsp;");
			strDate = rateSubHTMLs[0].trim();
			for (int j = 1; j < titleHTMLs.length; j++) {
				titSubHTMLs = titleHTMLs[j].split("&nbsp;");
				rateSubHTMLs = rateHTMLs[(j + 1)].split("&nbsp;");
				String title = titSubHTMLs[0].trim();
				String rate = rateSubHTMLs[0].trim();
				cCode=getCode(title);
				if(cCode==null || cCode.trim().length()==0){
					continue;
				}
//				rate=getActualRate(cCode,rate);
				sbf.append(strDate + ","+ title+ ","+ cCode + "," +rate + ",CNY");
				if(j!=titleHTMLs.length-1){
					sbf.append(";");
				}
			}
		}
		return sbf.toString();
	}

	/**
	 * 根据币种获取正向汇率。从国家外汇管理局获取的汇率有正向汇率也有反向汇率，需要进行转换。
	 * 但目前系统接受的汇率较少，且均为正向汇率，故本方法暂时无需实现
	 * 
	 * 	正向汇率：100外币对 多少人民币
	 *  反向汇率：100人民币对 多少外币
	 * @param cCode
	 * @param rate
	 * @return
	 */
	private static String getActualRate(String cCode, String rate) {
		return rate;
	}

	/**
	 * 将外币中文转成ISO币种代码
	 * @param title
	 * @return
	 */
	private static String getCode(String title) {
		return CURRENCY_CODE.get(title);
	}
}
