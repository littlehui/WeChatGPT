package com.idaymay.dzt.common.utils.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @Description TODO
 * @ClassName AppendFileUtil
 * @Author littlehui
 * @Date 2020/11/8 00:17
 * @Version 1.0
 **/
public class AppendFileUtil {

    /**
     * 追加文件：使用FileWriter
     *
     * @param fileNamePath
     * @param content
     */
    public static void appendWriteln(String fileNamePath, String content) {
        FileWriter writer = null;
        try {
            File file = new File(fileNamePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(fileNamePath, true);
            writer.write(content + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            File file = new File("./text.txt");
            if (file.createNewFile()) {
                System.out.println("Create file successed");
            }
            appendWriteln("./text.txt", "\n123");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
