package com.hotfix.patchdispatcher.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chan on 2018/1/4.
 * 类-索引映射model
 */

public class ClassIndexMappingModel {
    public ClassIndexMappingModel() {
    }

    public ClassIndexMappingModel(String classIndex, String className) {
        this.classIndex = classIndex;
        this.className = className;
    }

    public String classIndex;
    public String className = "";
    public List<MethodIndexMappingModel> methodIndexMappingModelList = new ArrayList<>();

    public String getClassIndex() {
        return classIndex;
    }

    public void setClassIndex(String classIndex) {
        this.classIndex = classIndex;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<MethodIndexMappingModel> getMethodIndexMappingModelList() {
        return methodIndexMappingModelList;
    }

    public void setMethodIndexMappingModelList(List<MethodIndexMappingModel> methodIndexMappingModelList) {
        this.methodIndexMappingModelList = methodIndexMappingModelList;
    }

    @Override
    public boolean equals(Object model) {
        if (model instanceof ClassIndexMappingModel){
            return this.getClassIndex().equals(((ClassIndexMappingModel) model).getClassIndex());
        }

        return false;
    }

    @Override
    public String toString() {
        return "ClassIndexMappingModel{" +
                "classIndex=" + classIndex +
                ", className='" + className + '\'' +
                '}';
    }
}
