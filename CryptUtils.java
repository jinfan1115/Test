package com.glp.collie.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glp.util.security.Base64;
import com.glp.util.security.RSA;

public class CryptUtils {

  private static final Logger logger = LoggerFactory.getLogger(CryptUtils.class);

  private static final Map<String, String> map = new HashMap<>();

  public static void init() throws Exception {
    map.put("PAY_PAYMENT_PUK", FileUtils.getTextFromPath("/security/payment.puk"));
    map.put("PAY_COLLIE_PVK", FileUtils.getTextFromPath("/security/collie.pvk"));
  }

  public enum CryptType {
    PAY_PAYMENT_PUK, PAY_COLLIE_PVK,
    EC_PUK, EC_PVK;
    public String getContent() throws Exception {
      if (map == null) {
        throw new Exception("没有找到密钥文件");
      }
      String content = map.get(this.name());
      if (content == null) {
        throw new Exception("未加载密钥文件");
      }
      return content;
    }
  }

  public static String encrypt(String data, CryptType keyType) throws Exception {
    String publicKey = keyType.getContent();
    String encrypt = encrypt(data, publicKey);
    return encrypt;
  }

  public static String decrypt(String data, CryptType keyType) throws Exception {
    String privateKey = keyType.getContent();
    String decrypt = decrypt(data, privateKey);
    return decrypt;
  }

  /**
   * 根据业务平台公钥加密
   * 
   * @param data
   * @param publicKey
   * @return
   */
  public static String encrypt(String data, String publicKey) {
    String encryptData = null;
    // 公钥加密过程
    try {
      logger.info("公钥加密，加密前：data = {}", data);
      byte[] $encryptData = RSA.encryptPublicKey(data, publicKey);
      encryptData = Base64.byteToBase64($encryptData).replaceAll("\n", "").replaceAll("\"", "");
      logger.debug("公钥加密，加密后：encryptData = {}", encryptData);
    } catch (Exception e) {
      logger.error("公钥加密异常：", e);
    }
    return encryptData;
  }

  /**
   * 根据支付平台私钥解密
   * 
   * @param cryptedData
   * @param privateKey
   * @return
   */
  public static String decrypt(String cryptedData, String privateKey) {
    String decryptData = null;
    try {
      logger.debug("私钥解密，解密前：cryptedData = {}", cryptedData);
      cryptedData = cryptedData.replaceAll("\"", "");
      byte[] $cryptedData = Base64.base64ToByte(cryptedData);
      decryptData = RSA.decryptPrivateKey($cryptedData, privateKey);
      logger.info("私钥解密，解密后：decryptData = {}", decryptData);
    } catch (Exception e) {
      logger.error("私钥解密异常：", e);
    }
    return decryptData;
  }
}
