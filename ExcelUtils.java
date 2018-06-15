package com.glp.collie.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import com.aliyun.oss.model.OSSObject;
import com.glp.collie.sdk.basic.APICode;
import com.glp.collie.service.payment.PaymentService;
import com.glp.commons.api.APIException;
import com.glp.commons.oss.OSS;
//import com.glp.collie.contract.OSS;
//import com.glp.collie.sdk.basic.APICode;
//import com.glp.collie.sdk.exception.APIException;

/**
 * excel utils
 */
/**
 * @author xpeng
 *
 */
public class ExcelUtils {

  public static final String TYPE_STRING = "string";
  public static final String TYPE_NUMERIC = "numeric";
  public static final String TYPE_DATE = "date";

  @SuppressWarnings("resource")
  public static Workbook getWorkbookByOssUrl(OSS oss, String url) {
    try {
      AssertUtils.hasText(url, APICode.INVALID_PARAMETER, "oss url不能为空");
      OSSObject object = oss.getOSSClient().getObject(new URL(url), null);
      AssertUtils.notNull(object, APICode.INVALID_PARAMETER, "读取文件失败，请重新上传");

      return url.endsWith(".xls") ? new HSSFWorkbook(object.getObjectContent()) : new XSSFWorkbook(
          object.getObjectContent());
    } catch (APIException e) {
      throw e;
    } catch (Exception e) {
      throw new APIException(APICode.DATA_ERROR, String.format("url:[%s]操作失败", url));
    }
  }

  public static void closeWorkbook(Workbook workbook) {
    if (workbook != null) {
      try {
        workbook.close();
      } catch (IOException e) {
        // ignore exception
      }
    }
  }

  public static int getColnum(HSSFSheet sheet) {
    try {
      HSSFRow row = sheet.getRow(0);
      return row.getLastCellNum();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void setContent(HSSFSheet sheet, int rownum, int colnum, CellType cType, String value) {
    HSSFRow row = null;
    HSSFCell cell = null;

    row = sheet.getRow(rownum);
    if (row == null) {
      row = sheet.createRow(rownum);
    }

    cell = row.getCell(colnum);
    if (cell == null) {
      cell = row.createCell(colnum);
    }

    cell.setCellType(cType);
    switch (cType) {
    case NUMERIC:
      cell.setCellValue(Double.parseDouble(value));
      break;
    case STRING:
      cell.setCellValue(new HSSFRichTextString(value));
      break;
    case FORMULA:
      cell.setCellFormula(value);
      break;
    default:
      break;
    }
  }

  public static String getFormula(HSSFSheet sheet, int rownum, int colnum) {
    HSSFRow row = null;
    HSSFCell cell = null;

    try {
      row = sheet.getRow(rownum);
      if (row != null) {
        cell = row.getCell(colnum);

        if (cell != null) {
          if (cell.getCellTypeEnum() == CellType.FORMULA) {
            return cell.getCellFormula();
          }
        }
      }

      return "";
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static HSSFCell getCell(HSSFSheet sheet, int rownum, int colnum) {
    HSSFRow row = null;
    HSSFCell cell = null;

    try {
      row = sheet.getRow(rownum);
      if (row != null) {
        cell = row.getCell(colnum);
        return cell;
      }

      return null;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  public static String getContent(Sheet sheet, int rownum, int colnum) {
    if (sheet instanceof HSSFSheet) {
      return getContent((HSSFSheet) sheet, rownum, colnum);
    }
    if (sheet instanceof XSSFSheet) {
      return getContent((XSSFSheet) sheet, rownum, colnum);
    }
    return null;
  }

  /**
   * xls文件
   * 
   * @param sheet
   * @param rownum
   * @param colnum
   * @return
   */
  public static String getContent(HSSFSheet sheet, int rownum, int colnum) {
    HSSFRow row = null;
    HSSFCell cell = null;
    HSSFRichTextString text = null;
    java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
    nf.setGroupingUsed(false);

    try {
      row = sheet.getRow(rownum);
      if (row != null) {
        cell = row.getCell(colnum);

        if (cell != null) {
          CellType cType = cell.getCellTypeEnum();
          switch (cType) {
          case NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
              return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getJavaDate(cell
                  .getNumericCellValue()));
            } else {
              return nf.format(cell.getNumericCellValue());
            }
          case STRING:
            text = cell.getRichStringCellValue();
            return text.getString().trim();
          case FORMULA:
            sheet.setDisplayFormulas(true);
            return nf.format(cell.getNumericCellValue());
          case BLANK:
            return "";
          default:
            break;
          }
        }
      }
      return null;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * xlsx 文件
   * 
   * @param sheet
   * @param rownum
   * @param colnum
   * @return
   */
  public static String getContent(XSSFSheet sheet, int rownum, int colnum) {
    XSSFRow row = null;
    XSSFCell cell = null;
    XSSFRichTextString text = null;
    java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
    nf.setGroupingUsed(false);

    try {
      row = sheet.getRow(rownum);
      if (row != null) {
        cell = row.getCell(colnum);

        if (cell != null) {
          CellType cType = cell.getCellTypeEnum();
          switch (cType) {
          case NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
              return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getJavaDate(cell
                  .getNumericCellValue()));
            } else {
              return nf.format(cell.getNumericCellValue());
            }
          case STRING:
            text = cell.getRichStringCellValue();
            return text.getString().trim();
          case FORMULA:
            sheet.setDisplayFormulas(true);
            return nf.format(cell.getNumericCellValue());
          case BLANK:
            return "";
          default:
            break;
          }
        }
      }
      return null;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Object getContent(HSSFSheet sheet, int rownum, int colnum, String type) {
    HSSFRow row = null;
    HSSFCell cell = null;
    HSSFRichTextString text = null;
    java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
    nf.setGroupingUsed(false);

    try {
      row = sheet.getRow(rownum);
      if (row != null) {
        cell = row.getCell(colnum);

        if (cell != null) {
          CellType cType = cell.getCellTypeEnum();
          switch (cType) {
          case NUMERIC:
            if (type.equals(ExcelUtils.TYPE_DATE)) {
              return cell.getDateCellValue();
            } else {
              return nf.format(cell.getNumericCellValue());
            }
          case STRING:
            text = cell.getRichStringCellValue();
            return text.getString().trim();
          case FORMULA:
            sheet.setDisplayFormulas(true);
            return nf.format(cell.getNumericCellValue());
            // return Double.toString(cell.getNumericCellValue());
          case BLANK:
            if (type.equals(ExcelUtils.TYPE_NUMERIC)) {
              return "0";
            }
            return "";
          default:
            break;
          }
        } else {
          if (type.equals(ExcelUtils.TYPE_NUMERIC)) {
            return "0";
          } else {
            return "";
          }
        }
      }

      return "";
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static List<String> getTitle(HSSFSheet sheet, int startrow, int startcol) {
    List<String> titleList = new ArrayList<String>();
    int colnum = getColnum(sheet);

    for (int i = startcol; i < colnum; i++) {
      titleList.add(ExcelUtils.getContent(sheet, startrow, i));
    }

    return titleList;
  }

  /**
   * 通过模板生成XLS
   * 
   * @param templatePath
   * @param SheetName
   * @param dlistMap
   *          动态数据（key为模板中#list=右侧名称，list<Objet>的顺序为模板中字段的顺序）
   * @param sMap
   *          静态数据（）
   * @param endPoint
   *          边界坐标。行轴:列轴 例：1:10 2行11列
   * @throws EncryptedDocumentException
   * @throws InvalidFormatException
   * @throws IOException
   *           注：暂不支持定义多动态list数据
   */
  public static Workbook bulidXlsByTemplate(String templatePath, String SheetName,
      Map<String, List<List<Object>>> dlistMap, Map<String, Object> sMap, String endPoint)
      throws EncryptedDocumentException, InvalidFormatException, IOException {

    InputStream inp = PaymentService.class.getResourceAsStream(templatePath);
    Workbook wb = WorkbookFactory.create(inp);
    Map<String, String> zbMap = new HashMap<String, String>();
    Sheet sheet = null;
    if (StringUtils.isEmpty(SheetName)) {
      sheet = wb.getSheetAt(0);
    } else {
      sheet = wb.getSheet(SheetName);
    }
    String[] eps = endPoint.split(":");
    for (int i = 0; i <= Integer.parseInt(eps[0]); ++i) {
      Row row = sheet.getRow(i);
      if (row == null)
        continue;
      for (int j = 0; j <= Integer.parseInt(eps[1]); ++j) {
        Cell cell = row.getCell(0);
        if (cell == null)
          continue;
        String flage = cell.getStringCellValue();
        if (flage.indexOf("#") != -1) {
          zbMap.put(flage.split("=")[1], i + ":" + j);// 行:列
        }
      }
    }

    // CellStyle style = wb.createCellStyle();
    // Font font = wb.createFont();
    // font.setBold(false);
    // style.setFont(font);
    // style.setWrapText(true);
    // Map<String, Object> properties = new HashMap<String, Object>();

    // properties.put(CellUtil.BORDER_TOP, BorderStyle.THIN.getCode());
    // properties.put(CellUtil.BORDER_BOTTOM, BorderStyle.THIN.getCode());
    // properties.put(CellUtil.BORDER_LEFT, BorderStyle.THIN.getCode());
    // properties.put(CellUtil.BORDER_RIGHT, BorderStyle.THIN.getCode());

    if (dlistMap != null) {// 生成动态数据

      for (String key : dlistMap.keySet()) {
        List<List<Object>> list = dlistMap.get(key);
        if (list == null)
          continue;
        int rowNum = Integer.valueOf(zbMap.get(key).split(":")[0]);
        Row row = sheet.getRow(rowNum);
        for (int i = 0; i < list.size(); ++i) {
          List<Object> cellDatas = list.get(i);
          for (int j = 0; j < cellDatas.size(); ++j) {
            Cell cell = row.getCell(j) == null ? row.createCell(j) : row.getCell(j);
            Object o = cellDatas.get(j);
            if (o instanceof String) {
              cell.setCellValue(o.toString());
              cell.setCellType(CellType.STRING);
              // cell.setCellStyle(style);
              // CellUtil.setCellStyleProperties(cell, properties);
            } else if (o instanceof BigDecimal) {
              cell.setCellValue(((BigDecimal) o).doubleValue());
              cell.setCellType(CellType.NUMERIC);
              // cell.setCellStyle(style);
              // CellUtil.setCellStyleProperties(cell, properties);
            } else if (o instanceof Double) {
              cell.setCellValue((Double) o);
              cell.setCellType(CellType.NUMERIC);
              // cell.setCellStyle(style);
              // CellUtil.setCellStyleProperties(cell, properties);
            } else if (o instanceof Integer) {
              cell.setCellValue((Integer) o);
              cell.setCellType(CellType.NUMERIC);
              // cell.setCellStyle(style);
              // CellUtil.setCellStyleProperties(cell, properties);
            }
          }
          ++rowNum;
          row = sheet.getRow(rowNum) == null ? sheet.createRow(rowNum) : sheet.getRow(rowNum);
        }
      }
    }

    if (sMap != null) {// 生成静态数据

    }

    return wb;
  }

}
