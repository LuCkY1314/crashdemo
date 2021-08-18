package com.hotfix.patchdispatcher;

/**
 * Created by chan on 2017/12/28.
 * Information including both class to fix and class for patching.
 */

public class PatchClassInfo {
    private String fixedClassName;
    private String patchClassName;

    public PatchClassInfo(String fixedClassName, String patchClassName) {
        this.fixedClassName = fixedClassName;
        this.patchClassName = patchClassName;
    }

    public String getFixedClassName() {
        return fixedClassName;
    }

    public String getPatchClassName() {
        return patchClassName;
    }
}