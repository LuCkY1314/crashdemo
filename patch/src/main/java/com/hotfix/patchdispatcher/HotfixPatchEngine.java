package com.hotfix.patchdispatcher;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.hotfix.patchdispatcher.model.ClassIndexMappingModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

/**
 * Created by chan on 2018/1/11.
 *
 */

public class HotfixPatchEngine extends Thread {
    private IPatchHandler patchHandler;
    private Context context;
    private IPatchHandler.OnPatchFixListener patchFixListener;
    private AtomicInteger fixedPatchesCount = new AtomicInteger(0);

    HotfixPatchEngine(Context context, IPatchHandler patchHandler, IPatchHandler.OnPatchFixListener patchFixListener) {
        this.patchHandler = patchHandler;
        this.context = context;
        this.patchFixListener = patchFixListener;
    }

    @Override
    public void run() {
        //获取patch列表
        List<Patch> patches = patchHandler.acquirePatches();

        if (patches == null || patches.size() == 0){
            patchFixListener.onPatchFixException("Patches are empty");
            return;
        }

        patchFixListener.onPatchesAcquired(patches);

        //对patch列表中的每一个patch提取信息
        for (Patch patch : patches) {
            //若该patch已经加载完成修复，略过后续步骤
            if (patch.hasFixed()) {
                patchFixListener.onPatchFixSuccess(patch);
                fixedPatchesCount.incrementAndGet();
                continue;
            }

            //校验patch是否合法，包括本地是否存在文件及MD5是否正确
            if (patchHandler.verifyPatch(patch)) {
                String dexFilePath = patch.getSavePath();
                Log.i(context.getPackageName(), "dexFilePath:" + dexFilePath);

                DexClassLoader classLoader = new DexClassLoader(dexFilePath, makeDexOptimizedFile(), null, context.getClassLoader());
                try {
                    Class<?> patchInfoClazz = classLoader.loadClass(patch.getPatchInfoName());
                    IPatchesInfo<PatchClassInfo> patchesInfo = (IPatchesInfo<PatchClassInfo>) patchInfoClazz.newInstance();
                    if (patchesInfo != null) {
                        List<PatchClassInfo> patchClassInfoList = patchesInfo.getPatches();
                        List<ClassIndexMappingModel> classModels = loadInsertedClasses();
                        int fixedClassCount = 0;

                        for (PatchClassInfo info : patchClassInfoList) {
                            Class<?> patchClazz = classLoader.loadClass(info.getPatchClassName());
                            IChangeDispatcher changeDispatcher = (IChangeDispatcher) patchClazz.newInstance();
                            String fixedClassName = info.getFixedClassName();
                            if (classModels != null && classModels.size() > 0) {
                                for (ClassIndexMappingModel model : classModels) {
                                    Log.i(context.getPackageName(), "fixedClassName:" + fixedClassName);
                                    Log.i(context.getPackageName(), "modelClassName:" + model.getClassName());
                                    if (fixedClassName.equals(model.getClassName())) {
                                        ASMUtils.mInterface.put(model.getClassIndex(), changeDispatcher);
                                        classModels.remove(model);

                                        fixedClassCount++;
                                        break;
                                    }
                                }
                            }
                        }
                        //修复完成的class数量等于info中class总数量，则整个patch完成修复
                        Log.i(context.getPackageName(), "fixedClassCount:" + fixedClassCount + "\n" + "patchClassInfoListSize:" + patchClassInfoList.size());
                        if (fixedClassCount == patchClassInfoList.size()) {
                            patch.setHasFixed(true);
                            fixedPatchesCount.incrementAndGet();

                            patchFixListener.onPatchFixSuccess(patch);
                        } else {
                            patchFixListener.onPatchFixFailed(patch);
                        }
                    }

                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    patchFixListener.onPatchFixException("Patch:" + patch.getName() + e.getMessage());
                }
            } else {
                patchFixListener.onPatchFixException("Patch:" + patch.getName() + "verification failed");
            }
        }

        if (fixedPatchesCount.get() == patches.size()){
            patchFixListener.onAllPatchesFixCompleted(patches);
        }

    }

    private String makeDexOptimizedFile(){
        String dexOptimizedPath = context.getFilesDir() + File.separator + "odex" + File.separator;
        File odexFile = new File(dexOptimizedPath);

        if (!odexFile.getParentFile().exists()){
            odexFile.getParentFile().mkdirs();
        }

        if (!odexFile.exists()){
            odexFile.mkdir();
        }

        return odexFile.getAbsolutePath();
    }

    //获取所有插桩代码类的信息，只有插过桩的代码才能进行热修复
    private List<ClassIndexMappingModel> loadInsertedClasses(){
        List<ClassIndexMappingModel> classModels = new ArrayList<>();

        // TODO: 2018/1/11 这里需要从构建过程中生成的map文件中解析出classModel列表数据
        try {
            InputStream is = context.getAssets().open("classes.map");
            BufferedReader reader =  new BufferedReader(new InputStreamReader(is));
            StringBuilder classesJsonStr = new StringBuilder();
            String readLine;
            while ((readLine = reader.readLine()) != null){
                classesJsonStr.append(readLine).append("\n");
            }

            Log.i(context.getPackageName(), classesJsonStr.toString());

            classModels.addAll(JSON.parseArray(classesJsonStr.toString(), ClassIndexMappingModel.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classModels;
    }
}
