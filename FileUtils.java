package com.glp.collie.util;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

  public static String getTextFromPath(String path, Charset charset) throws Exception {
    BufferedReader bufferedReader = null;
    try {
      Path $path = Paths.get(FileUtils.class.getResource(path).toURI());
      bufferedReader = Files.newBufferedReader($path, charset);
      String line = null;
      StringBuilder stringBuilder = new StringBuilder();
      while ((line = bufferedReader.readLine()) != null) {
        stringBuilder.append(line);
      }
      return stringBuilder.toString();
    } finally {
      if (bufferedReader != null) {
        bufferedReader.close();
      }
    }
  }

  public static String getTextFromPath(String pathStr) throws Exception {
    return getTextFromPath(pathStr, StandardCharsets.UTF_8);
  }
}
