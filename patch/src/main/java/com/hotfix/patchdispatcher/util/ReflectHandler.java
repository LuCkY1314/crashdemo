package com.hotfix.patchdispatcher.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by chan on 2018/1/23.
 * 反射工具类
 * 对常规的反射方法做了封装，在反射方法中传入方法和成员的声明类类名，从而精准匹配，同时便于反射时方法名和成员变量名的字符串替换
 */

public class ReflectHandler {

    public static Object invokeMethod(String declaringClassName, String methodName, Object invokeInstance, Class<?>[] paramTypes, Object[] paramInstances){
        try {
            Class<?> declaringClazz = Class.forName(declaringClassName);

            if (declaringClazz == null || declaringClazz.isInterface()) {
                Class<?> invokeClass = invokeInstance.getClass();
                Method method;
                for (; invokeClass != null; invokeClass = invokeClass.getSuperclass()) {
                    method = invokeClass.getMethod(methodName, paramTypes);
                    if (method != null) {
                        if (!method.isAccessible()) {
                            method.setAccessible(true);
                            return method.invoke(invokeInstance, paramInstances);
                        }
                    }
                }
            } else {
                Method method = declaringClazz.getMethod(methodName, paramTypes);
                if (method != null) {
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                        return method.invoke(invokeInstance, paramInstances);
                    }
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Object invokeDeclaredMethod(String declaringClassName, String methodName, Object invokeInstance, Class<?>[] paramTypes, Object[] paramInstances){
        try {
            Class<?> declaringClazz = Class.forName(declaringClassName);

            if (declaringClazz == null || declaringClazz.isInterface()) {
                Class<?> invokeClass = invokeInstance.getClass();
                Method method;
                for (; invokeClass != null; invokeClass = invokeClass.getSuperclass()) {
                    method = invokeClass.getDeclaredMethod(methodName, paramTypes);
                    if (method != null) {
                        if (!method.isAccessible()) {
                            method.setAccessible(true);
                            return method.invoke(invokeInstance, paramInstances);
                        }
                    }
                }
            } else {
                Method method;
                for (; declaringClazz != null; declaringClazz = declaringClazz.getSuperclass()){
                    method = declaringClazz.getDeclaredMethod(methodName, paramTypes);
                    if (method != null) {
                        if (!method.isAccessible()) {
                            method.setAccessible(true);
                            return method.invoke(invokeInstance, paramInstances);
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Object getFieldValue(String declaringClassName, String fieldName, Object targetInstance){
        try {
            Class<?> declaringClazz = Class.forName(declaringClassName);

            if (declaringClazz == null){
                Class<?> clazz = targetInstance.getClass();
                for (; clazz != null; clazz=clazz.getSuperclass()){
                    Field field = clazz.getField(fieldName);
                    if (field != null){
                        if (!field.isAccessible()){
                            field.setAccessible(true);
                        }

                        return field.get(targetInstance);
                    }
                }
            }else {
                Field field = declaringClazz.getField(fieldName);
                if (field != null){
                    if (!field.isAccessible()){
                        field.setAccessible(true);
                    }

                    return field.get(targetInstance);
                }
            }
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Object getDeclaredFieldValue(String declaringClassName, String fieldName, Object targetInstance){
        try {
            Class<?> declaringClazz = Class.forName(declaringClassName);

            if (declaringClazz == null){
                Class<?> clazz = targetInstance.getClass();
                for (; clazz != null; clazz=clazz.getSuperclass()){
                    Field field = clazz.getDeclaredField(fieldName);
                    if (field != null){
                        if (!field.isAccessible()){
                            field.setAccessible(true);
                        }

                        return field.get(targetInstance);
                    }
                }
            }else {
                Field field = declaringClazz.getDeclaredField(fieldName);
                if (field != null){
                    if (!field.isAccessible()){
                        field.setAccessible(true);
                    }

                    return field.get(targetInstance);
                }
            }
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void setFieldValue(String declaringClassName, String fieldName, Object targetInstance, Object value){
        try {
            Class<?> declaringClazz = Class.forName(declaringClassName);

            for (; declaringClazz != null; declaringClazz = declaringClazz.getSuperclass()) {
                Field field = declaringClazz.getDeclaredField(fieldName);
                if (field != null) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }

                    field.set(targetInstance, value);
                    break;
                }
            }
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
