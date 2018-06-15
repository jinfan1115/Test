package com.glp.collie.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class PDFUtils {
  
  private static final Logger log = LoggerFactory.getLogger(PDFUtils.class);
  
  public static List<String> convertToPNG(String pdfFilePath) {
    if (StringUtils.isEmpty(pdfFilePath))
      return null;

    PDDocument document = null;
    try {

      File sourceFile = new File(pdfFilePath);
      if (!sourceFile.exists()) 
        return null;
      
      document = PDDocument.load(sourceFile);
      PDFRenderer pdfRenderer = new PDFRenderer(document);
      
      int pageNumber = 0;
      String pngFilePath = null;
      List<String> list = new ArrayList<String>();
      
      for (PDPage unuse : document.getPages()) {
        
        BufferedImage bim = pdfRenderer.renderImageWithDPI(pageNumber, 100, ImageType.BINARY);
        
        pngFilePath = pdfFilePath + "-" + (pageNumber++) + ".png";
        if (ImageIOUtil.writeImage(bim, pngFilePath, 300))
          list.add(pngFilePath);
      }
      
      return list;
      
    } catch (Exception e) {
      log.error(null, e); 
    } finally {
      try {
        document.close();
      } catch (IOException e) {
        log.error(null, e); 
      }
    }
    
    return null;
  }

  public static void main(String[] args) {
  }

}
