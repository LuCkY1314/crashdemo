package com.hotfix.patchdispatcher;

public interface IChangeDispatcher {
	public Object accessFunc(int functionIndex, Object[] arg, Object object);
    public boolean needFixFunc(int functionIndex);
}
