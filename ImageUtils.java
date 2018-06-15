package com.glp.collie.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ij.IJ;

public class ImageUtils {

  private static final Logger log = LoggerFactory.getLogger(ImageUtils.class);

  public static void main(String[] xxx) {
    System.out.println(coverteBinary("D:/fls.jpg", "D:/fls.jpg.png"));
  }

  public static boolean coverteBinary(String imagePath, String newFilePath) {
    try {
       IJ.open(imagePath);
       IJ.run("Make Binary");
       IJ.save(newFilePath);
//      Binary bi = new Binary();
//      bi.run(new BinaryProcessor(new ByteProcessor(ImageIO.read(new File(imagePath)))));
//      ImagePlus imp = new ImagePlus(imagePath);
//      IJ.runPlugIn(imp,"Make Binary",""); 
//      new FileSaver(imp).saveAsPng(newFilePath);
      // BufferedImage image = ImageIO.read(new File(imagePath));
      //
      // BufferedImage target = new
      // ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY),
      // null).filter(image, null);
      //
      // ImageIO.write(target, "jpg", new File(newFilePath));
    } catch (Exception e) {
      log.error("ImageUtils.coverteBinary error,", e);
      return false;
    }

    return true;
  }

  public static boolean downPictureToInternet(String filePath, String strUrl) {
    try {
      URL url = new URL(strUrl);
      InputStream fStream = url.openConnection().getInputStream();
      int b = 0, total = 0;
      FileOutputStream fos = new FileOutputStream(new File(filePath));
      while ((b = fStream.read()) != -1) {
        fos.write(b);
        total += b;
      }
      fStream.close();
      fos.close();

      log.info("ImageUtils Download "+ filePath + "from url["+strUrl+"]" +" ,size : " + total);
      return true;
    } catch (Exception e) {
      log.error("downPictureToInternet error : ", e);
      return false;
    }
  }

  public enum ImageType {
    jpg, png;
  }
}
