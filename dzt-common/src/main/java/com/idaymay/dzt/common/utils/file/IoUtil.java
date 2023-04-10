package com.idaymay.dzt.common.utils.file;


import java.io.*;

/**
 * 统一的io处理工具类
 *
 * @author liting
 */
public class IoUtil {
    /**
     * 默认的字节读取大小
     */
    private static final int SIZE = 4 * 1024;

    /**
     * @param inputStream
     * @param targetFile
     * @param closeStream 输入流转文件
     * @return java.io.File
     * @author littlehui
     * @date 2021/11/15 16:04
     */
    public static File inputStream2File(
            InputStream inputStream, String targetFile, boolean closeStream) throws IOException {
        File file = new File(targetFile);
        OutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            byte[] buffer = new byte[SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            outStream.flush();
        } finally {
            if (outStream != null) {
                outStream.close();
            }
            if (closeStream && inputStream != null) {
                inputStream.close();
            }
        }
        return file;
    }

    /**
     * 输入流转String
     *
     * @param is
     * @param charsetName
     * @param closeStream
     * @return java.lang.String
     * @author littlehui
     * @date 2021/11/15 16:05
     */
    public static String inputStream2String(InputStream is, String charsetName, boolean closeStream)
            throws IOException {
        ByteArrayOutputStream result = null;
        try {
            result = new ByteArrayOutputStream();
            byte[] buffer = new byte[SIZE];
            int length;
            while ((length = is.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String str = result.toString(charsetName);
            return str;
        } finally {
            if (closeStream && is != null) {
                is.close();
            }
            if (result != null) {
                result.close();
            }
        }
    }

    /**
     * String转输入流
     *
     * @param s
     * @param charsetName
     * @return java.io.ByteArrayInputStream
     * @author littlehui
     * @date 2021/11/15 16:05
     */
    public static ByteArrayInputStream string2InputStream(String s, String charsetName)
            throws UnsupportedEncodingException {
        return new ByteArrayInputStream(s.getBytes(charsetName));
    }

    /**
     * 文件路径转文件输入流
     *
     * @param path
     * @return java.io.FileInputStream
     * @author littlehui
     * @date 2021/11/15 16:06
     */
    public static FileInputStream file2InputStream(String path) throws FileNotFoundException {
        return new FileInputStream(path);
    }


    /**
     * 检查目录存不存在，不存在则创建.
     *
     * @param path
     * @return void
     * @author littlehui
     * @date 2021/11/15 16:06
     */
    public static void checkPath(String path) {
        File file = new File(path);
        if (file != null && file.isDirectory() && !file.exists()) {
            file.mkdirs();
        }
        if (file == null || !file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * byte数组转输入流
     *
     * @param buf
     * @return java.io.InputStream
     * @author littlehui
     * @date 2021/11/15 16:06
     */
    public static final InputStream byte2InputStream(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

    /**
     * 读取输入流并返回byte数组
     *
     * @param inStream
     * @return byte[]
     * @author littlehui
     * @date 2021/11/15 16:06
     */
    public static final byte[] inputStream2byte(InputStream inStream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[SIZE];
        int rc = 0;
        while ((rc = inStream.read(buff)) != -1) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    /**
     * inputStream转输出流
     *
     * @param in
     * @return java.io.ByteArrayOutputStream
     * @author littlehui
     * @date 2021/11/15 16:07
     */
    public static ByteArrayOutputStream inputStream2OutputStream(InputStream in) throws Exception {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1) {
            swapStream.write(ch);
        }
        return swapStream;
    }

    /**
     * string转输出流
     *
     * @param in
     * @return java.io.ByteArrayOutputStream
     * @author littlehui
     * @date 2021/11/15 16:07
     */
    public static ByteArrayOutputStream string2OutputStream(String in) throws Exception {
        return inputStream2OutputStream(string2InputStream(in));
    }

    /**
     * String转inputStream
     *
     * @param in
     * @return java.io.ByteArrayInputStream
     * @author littlehui
     * @date 2021/11/15 16:07
     */
    public static ByteArrayInputStream string2InputStream(String in) {
        ByteArrayInputStream input = new ByteArrayInputStream(in.getBytes());
        return input;
    }

    /**
     * 读取文件并返回byte数组
     *
     * @param file
     * @return byte[]
     * @author littlehui
     * @date 2021/11/15 16:07
     */
    public static byte[] file2Byte(File file) throws IOException {
        byte[] buffer;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            byte[] b = new byte[SIZE];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
        buffer = bos.toByteArray();
        return buffer;
    }

    /**
     * byte数组写入文件
     *
     * @param bytes
     * @param fileName
     * @return void
     * @author littlehui
     * @date 2021/11/15 16:08
     */
    public static void byte2File(byte[] bytes, String fileName) throws IOException {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            file = new File(fileName);
            checkPath(file.getParent());
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
            bos.flush();
        } finally {
            if (bos != null) {
                bos.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }
}
