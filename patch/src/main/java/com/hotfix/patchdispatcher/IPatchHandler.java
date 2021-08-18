package com.hotfix.patchdispatcher;

import java.util.List;

/**
 * Created by chan on 2018/1/11.
 * patch处理接口
 */

public interface IPatchHandler {

    /**获取patch列表，实际使用时从服务端拉取*/
    List<Patch> acquirePatches();

    /**检验patch是否合法*/
    boolean verifyPatch(Patch patch);

    interface OnPatchFixListener{

        /**获取到patch列表回调*/
        void onPatchesAcquired(List<Patch> patches);

        /**patch完成修复回调*/
        void onPatchFixSuccess(Patch patch);

        /**patch修复失败回调*/
        void onPatchFixFailed(Patch patch);

        /**所有patch完成修复*/
        void onAllPatchesFixCompleted(List<Patch> patches);

        /**修复异常回调*/
        void onPatchFixException(String msg);
    }
}
