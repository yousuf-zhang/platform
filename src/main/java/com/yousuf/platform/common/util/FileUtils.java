package com.yousuf.platform.common.util;

import org.springframework.core.io.ClassPathResource;

import java.io.*;

/**
 * ClassName: FileUtils
 * <p> Description: 文件读写工具类
 *
 * @author zhangshuai 2019/11/7
 */
public class FileUtils {
    /**
     * <p> Title: writeObject
     * <p> Description: 把对象写入文件
     *
     * @param object 写入对象
     * @param fileName 写入路径
     *
     * @author zhangshuai 2019/11/7
     *
     */
    public static void writeObject(Object object, String fileName) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(fileName));
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.flush();
        oos.close();
    }
    /**
     * <p> Title: readObject
     * <p> Description: 读取文件
     *
     * @param fileName 文件
     *
     * @return java.lang.Object
     *
     * @author zhangshuai 2019/11/7
     *
     */
    public static Object readObject(String fileName) throws IOException, ClassNotFoundException {
        ClassPathResource classPathResource = new ClassPathResource(fileName);
        FileInputStream fis = new FileInputStream(classPathResource.getFile());
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object o = ois.readObject();
        ois.close();
        return o;
    }
}
