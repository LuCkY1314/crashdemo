package com.hotfix.patchdispatcher;

import android.content.Context;

/**
 * Created by chan on 2018/1/2.
 * Patch manager providing patching method.
 */

public class HotfixPatchProxy {
    private static volatile HotfixPatchProxy hotfixPatchProxy;

    private HotfixPatchProxy() {
    }

    public static HotfixPatchProxy getInstance(){
        if (hotfixPatchProxy == null){
            synchronized (HotfixPatchProxy.class){
                if (hotfixPatchProxy == null){
                    hotfixPatchProxy = new HotfixPatchProxy();
                }
            }
        }

        return hotfixPatchProxy;
    }

    public void patch(Context context, IPatchHandler.OnPatchFixListener onPatchFixListener) {
        new HotfixPatchEngine(context, new PatchHandlerImpl(context), onPatchFixListener).start();
    }
}
