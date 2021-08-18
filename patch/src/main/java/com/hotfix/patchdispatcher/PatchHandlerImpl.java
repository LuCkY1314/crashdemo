package com.hotfix.patchdispatcher;

import android.content.Context;

import com.hotfix.patchdispatcher.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chan on 2018/1/11.
 * patch处理实现类
 */

public class PatchHandlerImpl implements IPatchHandler {
    private Context context;

    PatchHandlerImpl(Context context) {
        this.context = context;
    }

    @Override
    public List<Patch> acquirePatches() {
        List<Patch> patches = new ArrayList<>();

//        patches = fetchPatchesFromServer();

        Patch patch1 = new Patch();
        patch1.setName("patch.dex");
        patch1.setSavePath(getDexFilePath(context, patch1.getName()));
        patch1.setHasFixed(false);
        patch1.setPatchInfoName("com.hotfix.patch.PatchesInfoImpl");

//        Patch patch1 = new Patch();
//        patch1.setName("patch1.dex");
//        patch1.setSavePath(getDexFilePath(context, patch1.getName()));
//        patch1.setHasFixed(false);
//        patch1.setPatchInfoName("ctrip.hotfix.patch.patch1.Patch1InfoImpl");
//
//        Patch patch2 = new Patch();
//        patch2.setName("patch2.dex");
//        patch2.setSavePath(getDexFilePath(context, patch2.getName()));
//        patch2.setHasFixed(false);
//        patch2.setPatchInfoName("ctrip.hotfix.patch.patch2.Patch2InfoImpl");
//
        patches.add(patch1);
//        patches.add(patch2);
        return patches;
    }

    private String getDexFilePath(Context context, String dexFileName) {
        try {
            String dexFileTargetParentPath = context.getFilesDir().getAbsolutePath() + File.separator + "hotfix";
            File dexParentFile = new File(dexFileTargetParentPath);

            if (!dexParentFile.exists()){
                dexParentFile.mkdir();
            }

            File dexFile = new File(dexParentFile, dexFileName);
            if (dexFile.exists()){
                FileUtils.removeFile(dexFile.getAbsolutePath());
            }
            dexFile.createNewFile();

            InputStream is = context.getAssets().open(dexFileName);
            OutputStream os = new FileOutputStream(dexFile);
            int len;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            is.close();
            os.close();

            return dexFile.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean verifyPatch(Patch patch) {
        // TODO: 2018/1/12 这里是patch的校验逻辑，包括检查本地dex文件是否存在，验证dex文件的md5是否合法 etc.

        return true;
    }

    private List<Patch> fetchPatchesFromServer(){
        // TODO: 2018/1/11 从服务端获取patch列表
        return null;
    }
}
