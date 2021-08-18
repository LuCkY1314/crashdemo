package com.hotfix.patchdispatcher;

/**
 * Created by chan on 2018/1/11.
 * 补丁类
 */

public class Patch {

    //patch名称，唯一标识
    private String name;

    //patch的MD5值，用于校验patch文件是否合法
    private String md5;

    //patch文件保存的路径
    private String savePath;

    //patch是否已经加载修复成功
    private boolean hasFixed;

    //描述patch中信息的类名称
    private String patchInfoName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public boolean hasFixed() {
        return hasFixed;
    }

    public void setHasFixed(boolean hasFixed) {
        this.hasFixed = hasFixed;
    }

    public String getPatchInfoName() {
        return patchInfoName;
    }

    public void setPatchInfoName(String patchInfoName) {
        this.patchInfoName = patchInfoName;
    }
}
