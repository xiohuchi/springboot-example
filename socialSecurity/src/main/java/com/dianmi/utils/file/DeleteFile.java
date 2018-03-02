package com.dianmi.utils.file;

import java.io.File;

/**
 * created by www
 * 2017/9/30 18:01
 */
public class DeleteFile {
    // 删除文件方法：可以是文件或文件夹
    public static boolean DeleteFolder(String sPath) {
        boolean result = false;
        File file = new File(sPath);
        // 判断目录或文件是否存在
        if (!file.exists()) { // 不存在返回 false
            return result;
        } else {
            // 判断是否为文件
            if (file.isFile()) { // 为文件时调用删除文件方法
                return deleteFile(sPath);
            } else { // 为目录时调用删除目录方法
                return deleteDirectory(sPath);
            }
        }
    }

    /**
     * 删除单个文件
     */
    public static boolean deleteFile(String sPath) {
        boolean result = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            result = true;
        }
        return result;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     */
    public static boolean deleteDirectory(String sPath) {
        boolean result = false;
        // 如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        result = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                result = deleteFile(files[i].getAbsolutePath());
                if (!result)
                    break;
            } // 删除子目录
            else {
                result = deleteDirectory(files[i].getAbsolutePath());
                if (!result)
                    break;
            }
        }
        if (!result)
            return false;
        // 删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }
}
