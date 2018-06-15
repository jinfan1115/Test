package com.glp.collie.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ListUtils {

  private ListUtils() {

  }

  // public static <T> List<T> deepCopyList(List<T> src) {
  // List<T> dest = null;
  // try {
  // ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
  // ObjectOutputStream out = new ObjectOutputStream(byteOut);
  // out.writeObject(src);
  // ByteArrayInputStream byteIn = new
  // ByteArrayInputStream(byteOut.toByteArray());
  // ObjectInputStream in = new ObjectInputStream(byteIn);
  // dest = (List<T>) in.readObject();
  // } catch (IOException e) {
  // return null;
  // } catch (ClassNotFoundException e) {
  // return null;
  // }
  //
  // return dest;
  // }

  public static Object deepClone(Object obj) throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject(obj);
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(bis);
    return ois.readObject();
  }
}
