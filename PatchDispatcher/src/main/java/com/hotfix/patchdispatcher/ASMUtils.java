package com.hotfix.patchdispatcher;

import java.util.concurrent.ConcurrentHashMap;

public class ASMUtils {
	public static ConcurrentHashMap<String, IChangeDispatcher> mInterface = new ConcurrentHashMap<>();
	
	public static IChangeDispatcher getInterface(String classIndex, int functionIndex){
        if (mInterface != null && mInterface.get(classIndex) != null && mInterface.get(classIndex).needFixFunc(functionIndex)){
            return mInterface.get(classIndex);
        }
        return null;
    }
}
