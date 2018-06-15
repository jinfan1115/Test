package com.glp.collie.util;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.glp.collie.model.CommonResponse;
import com.glp.collie.util.CryptUtils.CryptType;
import com.glp.commons.api.APICode;
import com.glp.commons.api.APIException;
import com.glp.util.http.HttpUtils;
import com.glp.util.http.HttpUtils.HTTPResponse;
import com.glp.util.http.HttpUtils.Method;

public class HttpClientUtils {

  private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
  
  public static class HttpClientResponse implements Serializable {
    private static final long serialVersionUID = 8060221527131555304L;
    private Integer code;
    private String message;
    private Object data;
    private String tid;

    public HttpClientResponse() {
    }

    public HttpClientResponse(Integer code, String message, Object data) {
      this.code = code;
      this.message = message;
      this.data = data;
    }
    
    public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public Integer getCode() {
      return code;
    }

    public void setCode(Integer code) {
      this.code = code;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public Object getData() {
      return data;
    }

    public void setData(Object data) {
      this.data = data;
    }
    
    

    @Override
	public String toString() {
		return "{\"code\":" + code + ", \"message\":\"" + message + "\", \"data\":" + data +", \"tid\":\""+tid+ "\"}";
	}

	public CommonResponse parse() {
      CommonResponse response = new CommonResponse();
      if (this.code != APICode.SUCCESS.getCode()) {
        response.setCode(this.code);
        response.setMessage(APICode.SUCCESS.getMessage());
      }
      response.setData(this.data);
      return response;
    }
  }

  /**
   * 不加密的post请求
   * 
   * @param path
   *          请求的url
   * @param headers
   *          请求的headers
   * @param request
   *          请求的对象
   * @return HttpClientResponse
   * @throws Exception
   */
  public static HttpClientResponse post(String path, Map<String, String> headers, Object request)
      throws Exception {
    return request(Method.post, path, headers, null, request, null, null, HttpUtils.DEFAULT_TIMEOUT_SECOND);
  }

  /**
   * 不加密的post请求
   * 
   * @param path
   *          请求的url
   * @param headers
   *          请求的headers
   * @param request
   *          请求的对象
   * @param timeout
   *          超时时间
   * 
   * @return HttpClientResponse
   * @throws Exception
   */
  public static HttpClientResponse post(String path, Map<String, String> headers, Object request,
      Integer timeout) throws Exception {
    return request(Method.post, path, headers, null, request, null, null, timeout);
  }

  /**
   * 加密的post请求
   * 
   * @param path
   *          请求的url
   * @param headers
   *          请求的headers
   * @param request
   *          请求的对象
   * @param encryptType
   *          加密密钥类型
   * @param decryptType
   *          解密密钥类型
   * @return HttpClientResponse
   * @throws Exception
   */
  public static HttpClientResponse post(String path, Map<String, String> headers, Object request,
      CryptType encryptType, CryptType decryptType) throws Exception {
    return request(Method.post, path, headers, null, request, encryptType, decryptType,
        HttpUtils.DEFAULT_TIMEOUT_SECOND);
  }

  /**
   * http请求
   * 
   * @param method
   *          method类型
   * @param path
   *          请求地址
   * @param headers
   *          头信息
   * @param params
   *          url中的请求参数
   * @param request
   *          请求体
   * @param encryptType
   *          加密类型
   * @param decryptType
   *          解密类型
   * @param timeout
   *          超时时间
   * @return
   * @throws Exception
   */
  public static HttpClientResponse request(Method method, String path, Map<String, String> headers,
      Map<String, Object> params, Object request, CryptType encryptType, CryptType decryptType,
      Integer timeout) throws Exception {
    String $request = null;
    if (request != null) {
      $request = JSON.toJSONString(request);
    }
    if (encryptType != null) {
      $request = CryptUtils.encrypt($request, encryptType);
    }
    HTTPResponse response = HttpUtils.request(method, path, headers, params, $request, null, timeout);
    if (response == null) {
      throw new APIException(APICode.ERROR, "请求其他系统, 网络异常");
    }
    if (response.getCode() != APICode.SUCCESS.getCode()) {
      throw new APIException(APICode.ERROR, "请求其他系统, 网络异常, 响应码错误");
    }
    String body = response.getBody();
    if (body == null || body.isEmpty()) {
      throw new APIException(APICode.ERROR, "请求其他系统, 网络异常, 无响应体");
    }
    if (decryptType != null) {
      body = CryptUtils.decrypt(body, decryptType);
    }
    HttpClientResponse innerResponse = null;
    try {
      innerResponse = JSON.parseObject(body, HttpClientResponse.class);
    } catch (Exception e) {
      logger.error("@request ~body:{}", body);
      throw e;
    }
    if (innerResponse == null) {
      throw new APIException(APICode.ERROR, "请求其他系统, 返回的数据包错误");
    }
    if (innerResponse.getCode() == null) {
      throw new APIException(APICode.ERROR, "请求其他系统, 返回的数据包错误, 无状态码");
    }
    return innerResponse;
  }
  
  /**
   * http请求
   * 
   * @param method
   *          method类型
   * @param path
   *          请求地址
   * @param headers
   *          头信息
   * @param params
   *          url中的请求参数
   * @param request
   *          请求体
   * @param encryptType
   *          加密类型
   * @param decryptType
   *          解密类型
   * @param timeout
   *          超时时间
   * @return
   * @throws Exception
   */
  public static HttpClientResponse httpRequest(com.glp.collie.util.CollieHttpUtil.Method method, String path, Map<String, String> headers,
      Map<String, Object> params, Object request, CryptType encryptType, CryptType decryptType,
      Integer timeout) throws Exception {
    String $request = null;
    if (request != null) {
      $request = JSON.toJSONString(request);
    }
    if (encryptType != null) {
      $request = CryptUtils.encrypt($request, encryptType);
    }
    com.glp.collie.util.CollieHttpUtil.HTTPResponse response = CollieHttpUtil.request(method, path, headers, params, $request, null, timeout);
    if (response == null) {
      throw new APIException(APICode.ERROR, "请求其他系统, 网络异常");
    }
    if (response.getCode() != APICode.SUCCESS.getCode()) {
      throw new APIException(APICode.ERROR, "请求其他系统, 网络异常, 响应码错误");
    }
    String body = response.getBody();
    if (body == null || body.isEmpty()) {
      throw new APIException(APICode.ERROR, "请求其他系统, 网络异常, 无响应体");
    }
    if (decryptType != null) {
      body = CryptUtils.decrypt(body, decryptType);
    }
    HttpClientResponse innerResponse = null;
    try {
      innerResponse = JSON.parseObject(body, HttpClientResponse.class);
    } catch (Exception e) {
      logger.error("@request ~body:{}", body);
      throw e;
    }
    if (innerResponse == null) {
      throw new APIException(APICode.ERROR, "请求其他系统, 返回的数据包错误");
    }
    if (innerResponse.getCode() == null) {
      throw new APIException(APICode.ERROR, "请求其他系统, 返回的数据包错误, 无状态码");
    }
    return innerResponse;
  }
}
