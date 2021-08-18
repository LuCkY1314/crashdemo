package com.hotfix.patchdispatcher.model;

/**
 * Created by chan on 2018/1/4.
 * 索引-方法映射model
 */

public class MethodIndexMappingModel {
    public String MethodName = "";
    public int index;

    public MethodIndexMappingModel() {
    }

    public MethodIndexMappingModel(String methodName, int index) {
        MethodName = methodName;
        this.index = index;
    }

    public String getMethodName() {
        return MethodName;
    }

    public void setMethodName(String methodName) {
        MethodName = methodName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object model) {
        if (model instanceof MethodIndexMappingModel){
            return this.getIndex() == ((MethodIndexMappingModel)model).getIndex();
        }

        return false;
    }
}
