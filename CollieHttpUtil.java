package com.glp.collie.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glp.finance.common.http.SSLHttpClientBuilder;

public class CollieHttpUtil {
  
  public static final int DEFAULT_TIMEOUT_SECOND = 30000;
  public static final int MAX_TIMEOUT_SECOND = 300000;
  
  public static void main(String[] xx ){
    long s = System.currentTimeMillis();
    HTTPResponse rr = CollieHttpUtil.request(Method.post, "http://localhost:8090/collie/glpInterface1", 
        null, null, "", null, DEFAULT_TIMEOUT_SECOND);
    System.out.println(System.currentTimeMillis() - s);
    System.out.println(rr.getBody());
//    rr = HttpUtils.request(Method.post, "https://glpfl.glprop.com/glp_prod/glpInterface", 
//        null, null, "", null, DEFAULT_TIMEOUT_SECOND);
//    System.out.println(rr.getBody());
//    rr = HttpUtils.request(Method.post, "https://glpfl.glprop.com/glp_prod/glpInterface", 
//        null, null, "", null, DEFAULT_TIMEOUT_SECOND);
//    
//    System.out.println(rr.getBody());
  }

  private static final Logger logger = LoggerFactory.getLogger(CollieHttpUtil.class);

  private static HttpClient client;
  private static HttpClient sslClient;

  static {
    client = HttpClientBuilder.create()
        .setMaxConnTotal(1000)
        .setMaxConnPerRoute(1000)
        .disableAutomaticRetries()
        .disableRedirectHandling()
        .build();
    
    try {
      sslClient = SSLHttpClientBuilder.build();
    } catch (Exception e) {
      logger.error("", e);
    }
  }

  private CollieHttpUtil() {
  }

  /**
   * Http call.
   * 
   * @param method
   *          Http call method.
   * @param url
   *          Target url
   * @return
   */
  public static HTTPResponse request(String url) {
    return request(url, null);
  }

  /**
   * @param headers
   *          Http request header data
   */
  public static HTTPResponse request(String url, Map<String, String> headers) {
    return request(url, headers, null);
  }

  /**
   * @param parameters
   *          Http request parameters
   */
  public static HTTPResponse request(String url, Map<String, String> headers, Map<String, Object> params) {
    return request(url, headers, params, null);
  }

  /**
   * @param host
   *          Http host proxy
   */
  public static HTTPResponse request(String url, Map<String, String> headers, Map<String, Object> params,
      HttpHost host) {
    return request(Method.get, url, headers, params, null, host);
  }

  public static HTTPResponse request(Method method, String url, Map<String, String> headers,
      Map<String, Object> params, String reqEntity, HttpHost host) {
    return request(method, url, headers, params, reqEntity, host, DEFAULT_TIMEOUT_SECOND);
  }
  
  /**
   * timeout鏄痟ttp瓒呮椂鏃堕棿鍙傛暟,鍗曚綅涓烘绉�,鍖洪棿涓�(0,5鍒嗛挓],榛樿涓�30绉�
   * 
   * @return
   */
  public static HTTPResponse request(Method method, String url, Map<String, String> headers,
      Map<String, Object> params, String reqEntity, HttpHost host, int timeout) {
    if (timeout <=0)
      timeout = DEFAULT_TIMEOUT_SECOND;
    if (timeout > MAX_TIMEOUT_SECOND)
      timeout = MAX_TIMEOUT_SECOND;
    
    long start = System.currentTimeMillis();

    if (StringUtils.isBlank(url)) {
      logger.error("Http call, 'url' is null.");
      return null;
    }
    
    try {
      List<NameValuePair> listParam = new ArrayList<NameValuePair>();
      if (!MapUtils.isEmpty(params)) {
        for (Entry<String, Object> entry : params.entrySet()) {
          listParam.add(new BasicNameValuePair(String.valueOf(entry.getKey()), String.valueOf(entry
              .getValue())));
        }
      }

      RequestConfig config = RequestConfig.custom()
          .setConnectTimeout(timeout)
          .setConnectionRequestTimeout(timeout)
          .setSocketTimeout(timeout)
          .setProxy(host)
          .build();

      HttpRequestBase m = Method.buildMethod(method, url, config, listParam);

      
      if (!MapUtils.isEmpty(headers)) {
        for (Entry<String, String> entry : headers.entrySet()) {
          m.addHeader(entry.getKey(), entry.getValue());
        }
      }

      if (reqEntity != null && reqEntity.length() > 0 && (m instanceof HttpPut || m instanceof HttpPost)) {
        ((HttpEntityEnclosingRequestBase) m).setEntity(new StringEntity(reqEntity.toString(), Consts.UTF_8));
      }

      InputStream is = null;
      HttpEntity resEntity = null;

      StringBuilder sBuilder = new StringBuilder();
      byte[] bData = null;

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
        HttpResponse response = null;
        
        try {
          
          if (url.startsWith("https")) {
            response = sslClient.execute(m);
          } else {
            response = client.execute(m);
          }
          
        } catch (Exception e) {
          logger.error("", e);
        }
        
        int code = 0;
        Map<String, String> responseHeader = null;
        if (response != null) {
          code = response.getStatusLine().getStatusCode();
          if (response.getAllHeaders() != null) {
            responseHeader = new HashMap<String, String>(response.getAllHeaders().length);
            for (Header h : response.getAllHeaders()) {
              responseHeader.put(h.getName(), h.getValue());
            }
          }
          if ((resEntity = response.getEntity()) != null) {
            BufferedHttpEntity bEntity = new BufferedHttpEntity(response.getEntity());
            bData = EntityUtils.toByteArray(bEntity);
            is = bEntity.getContent();
            if (resEntity.getContentEncoding() != null
                && resEntity.getContentEncoding().getValue().contains("gzip")) {
              is = new GZIPInputStream(is);
            }

            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader reader = new BufferedReader(isr);

            String line;
            while ((line = reader.readLine()) != null) {
              sBuilder.append(line);
              sBuilder.append("\n");
            }
            
            reader.close();
            isr.close();
            is.close();

            /*
             * int len; byte[] buf = new byte[2048]; // 2 kb buffer while ((len
             * = is.read(buf)) > 0) { baos.write(buf, 0, len); }
             */
          }
        }
        logger.info("Http request finished in time [" + (System.currentTimeMillis() - start) + "]ms. URL is "
            + url);
        HTTPResponse resp = new HTTPResponse();
        // resp.setBody(new String(baos.toByteArray(), Consts.UTF_8));
        resp.setBody(sBuilder.toString());
        resp.setHeader(responseHeader);
        resp.setCode(code);
        
        resp.setBinaryData(bData);
        return resp;
      } catch (Exception ex) {
        logger.error("Http call has exception, 'url' is " + url + ". Cause:" + ex.getMessage());
      } finally {
        try {
          if (is != null) {
            is.close();
            is = null;
          }
          if (baos != null) {
            baos.close();
            baos = null;
          }
          m.releaseConnection();
          m = null;
        } catch (Exception ex) {
          // not need print stack trace
        }
      }
    } catch (Exception ex) {
      logger.error("Http call has exception, 'url' is " + url + ". Cause:" + ex.getMessage());
    }
    return null;
  }

  /**
   * http method
   */
  public enum Method {
    get {
      @Override
      public HttpRequestBase create(String url, RequestConfig config, List<NameValuePair> params)
          throws Exception {
        if (!CollectionUtils.isEmpty(params)) {
          if (url.indexOf("?") != -1) {
            url += "&" + URLEncodedUtils.format(params, Charset.forName("UTF-8"));
          } else {
            url += "?" + URLEncodedUtils.format(params, Charset.forName("UTF-8"));
          }
        }
        logger.info("Http get url " + url);
        HttpGet get = new HttpGet(url);
        if (config != null) {
          get.setConfig(config);
        }
        return get;
      }
    },
    post {
      @Override
      public HttpRequestBase create(String url, RequestConfig config, List<NameValuePair> params)
          throws Exception {
        HttpPost post = new HttpPost(url);
       
        if (config != null) {
          post.setConfig(config);
        }

        /*if (!CollectionUtils.isEmpty(params)) {
          post.setEntity(new UrlEncodedFormEntity(params, Charset.forName("UTF-8")));
          if (url.indexOf("?") != -1) {
            url += "&" + URLEncodedUtils.format(params, Charset.forName("UTF-8"));
          } else {
            url += "?" + URLEncodedUtils.format(params, Charset.forName("UTF-8"));
          }
          post.setURI(new URI(url));
        }*/
        post.setEntity(new UrlEncodedFormEntity(params, "utf-8")); 
        return post;
      }
    },
    put {
      @Override
      public HttpRequestBase create(String url, RequestConfig config, List<NameValuePair> params)
          throws Exception {
        HttpPut put = new HttpPut(url);
        if (config != null) {
          put.setConfig(config);
        }
        if (!CollectionUtils.isEmpty(params)) {
          put.setEntity(new UrlEncodedFormEntity(params, Charset.forName("UTF-8")));
          if (url.indexOf("?") != -1) {
            url += "&" + URLEncodedUtils.format(params, Charset.forName("UTF-8"));
          } else {
            url += "?" + URLEncodedUtils.format(params, Charset.forName("UTF-8"));
          }
          put.setURI(new URI(url));
        }
        return put;
      }
    },
    delete {
      @Override
      public HttpRequestBase create(String url, RequestConfig config, List<NameValuePair> params)
          throws Exception {
        if (!CollectionUtils.isEmpty(params)) {
          if (url.indexOf("?") != -1) {
            url += "&" + URLEncodedUtils.format(params, Charset.forName("UTF-8"));
          } else {
            url += "?" + URLEncodedUtils.format(params, Charset.forName("UTF-8"));
          }
        }
        HttpDelete delete = new HttpDelete(url);
        if (config != null) {
          delete.setConfig(config);
        }
        return delete;
      }
    };
    /**
     * create http method
     */
    abstract HttpRequestBase create(String url, RequestConfig config, List<NameValuePair> params)
        throws Exception;

    /**
     * bulid method
     */
    public static HttpRequestBase buildMethod(Method method, String url, RequestConfig config,
        List<NameValuePair> params) throws Exception {
      if (method == null) {
        return Method.get.create(url, config, params);
      }
      return method.create(url, config, params);
    }
  }

  public static class HTTPResponse {
    private int code;
    private String body;
    private Map<String, String> header;
    private byte[] binaryData;

    public HTTPResponse() {
    }

    public HTTPResponse(int code, String body, Map<String, String> header) {
      this.code = code;
      this.body = body;
      this.header = header;
    }

    public int getCode() {
      return code;
    }

    public void setCode(int code) {
      this.code = code;
    }

    public String getBody() {
      return body;
    }

    public void setBody(String body) {
      this.body = body;
    }

    public Map<String, String> getHeader() {
      return header;
    }

    public void setHeader(Map<String, String> header) {
      this.header = header;
    }

    public byte[] getBinaryData() {
      return binaryData;
    }

    public void setBinaryData(byte[] binaryData) {
      this.binaryData = binaryData;
    }
  }

}
