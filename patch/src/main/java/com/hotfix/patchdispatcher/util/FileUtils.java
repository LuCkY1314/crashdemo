package com.hotfix.patchdispatcher.util;

import android.text.TextUtils;

import java.io.File;

/**
 * Created by chan on 2018/1/11.
 * file工具类
 */

public class FileUtils {
    public static boolean removeFile(String filePath){
        if (TextUtils.isEmpty(filePath)){
            throw new IllegalArgumentException("file path is empty");
        }

        File file = new File(filePath);
        if (!file.exists()){
            return true;
        }

        if (file.isDirectory()){
            File[] files = file.listFiles();
            if (files != null && files.length > 0){
                for (File offspringFile : files){
                    if (!removeFile(offspringFile.getAbsolutePath())){
                        return false;
                    }
                }
            }
        }

        return file.delete();
    }
}
