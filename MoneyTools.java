package com.glp.collie.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MoneyTools {

  /**
   * 汉语中数字大写
   */
  private static final String[] CN_UPPER_NUMBER = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
  /**
   * 汉语中货币单位大写，这样的设计类似于占位符
   */
  private static final String[] CN_UPPER_MONETRAY_UNIT = { "分", "角", "元", "拾", "佰", "仟", "万", "拾", "佰", "仟",
      "亿", "拾", "佰", "仟", "兆", "拾", "佰", "仟" };
  /**
   * 特殊字符：整
   */
  private static final String CN_FULL = "整";
  /**
   * 特殊字符：负
   */
  private static final String CN_NEGATIVE = "负";
  /**
   * 金额的精度，默认值为4
   */
  private static final int MONEY_PRECISION = 2;
  /**
   * 特殊字符：零元整
   */
  private static final String CN_ZEOR_FULL = "零元" + CN_FULL;

  private static final DecimalFormat MICROMETER_FORMAT = new DecimalFormat("#,##0.00");

  /**
   * 把输入的金额转换为汉语中人民币的大写
   */
  public static String number2CNMontrayUnit(double money) {
    BigDecimal numberOfMoney = new BigDecimal(money);

    StringBuilder buff = new StringBuilder();
    // -1, 0, or 1 as the value of this BigDecimal is negative, zero, or
    // positive.
    int signum = numberOfMoney.signum();
    // 零元整的情况
    if (signum == 0) {
      return CN_ZEOR_FULL;
    }
    // 这里会进行金额的四舍五入
    long number = numberOfMoney.movePointRight(MONEY_PRECISION).setScale(0, 4).abs().longValue();
    // 得到小数点后两位值
    long scale = number % 100;
    int numUnit = 0;
    int numIndex = 0;
    boolean getZero = false;
    // 判断最后两位数，一共有四中情况：00 = 0, 01 = 1, 10, 11
    if (!(scale > 0)) {
      numIndex = 2;
      number = number / 100;
      getZero = true;
    }
    if ((scale > 0) && (!(scale % 10 > 0))) {
      numIndex = 1;
      number = number / 10;
      getZero = true;
    }
    int zeroSize = 0;
    while (true) {
      if (number <= 0) {
        break;
      }
      // 每次获取到最后一个数
      numUnit = (int) (number % 10);
      if (numUnit > 0) {
        if ((numIndex == 9) && (zeroSize >= 3)) {
          buff.insert(0, CN_UPPER_MONETRAY_UNIT[6]);
        }
        if ((numIndex == 13) && (zeroSize >= 3)) {
          buff.insert(0, CN_UPPER_MONETRAY_UNIT[10]);
        }
        buff.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
        buff.insert(0, CN_UPPER_NUMBER[numUnit]);
        getZero = false;
        zeroSize = 0;
      } else {
        ++zeroSize;
        if (!(getZero)) {
          buff.insert(0, CN_UPPER_NUMBER[numUnit]);
        }
        if (numIndex == 2) {
          if (number > 0) {
            buff.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
          }
        } else if (((numIndex - 2) % 4 == 0) && (number % 1000 > 0)) {
          buff.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
        }
        getZero = true;
      }
      // 让number每次都去掉最后一个数
      number = number / 10;
      ++numIndex;
    }
    // 如果signum == -1，则说明输入的数字为负数，就在最前面追加特殊字符：负
    if (signum == -1) {
      buff.insert(0, CN_NEGATIVE);
    }
    // 输入的数字小数点后两位为"00"的情况，则要在最后追加特殊字符：整
    // if (!(scale > 0)) {
    // buff.append(CN_FULL);
    // }
    return buff.toString();
  }

  public static String fmtMicrometer(double number) {
    return MICROMETER_FORMAT.format(number);
  }

  public static void main(String[] args) {
//    double money = 1827555.00;
//    String s = MoneyTools.number2CNMontrayUnit(money);
//    System.out.println("你输入的金额为：【" + money + "】   #--# [" + s.toString() + "]");
    System.out.println(numberFormat(0D, "#,##0.##"));
  }

  /**
   * 去除小数点后面的多余的0，如果小数点后都是多余的0，则小数点也去掉
   * 
   * @param numberStr
   *          数字类型字符串
   * @return
   */
  public static String subZeroAndDot(String numberStr) {
    if (numberStr.indexOf(".") > 0) {
      numberStr = numberStr.replaceAll("0+?$", "");// 去掉多余的0
      numberStr = numberStr.replaceAll("[.]$", "");// 如最后一位是.则去掉
    }
    return numberStr;
  }

  public static String numberFormat(Double d, String format) {
    BigDecimal bd = null;
    if (d == null) {
      bd = BigDecimal.ZERO;
    } else {
      bd = BigDecimal.valueOf(d);
    }
    DecimalFormat bf = new DecimalFormat(format);
    return bf.format(bd);
  }
}
