package com.crtip.test.hotfixdemo;

import com.android.tools.r8.org.objectweb.asm.ClassReader;
import com.android.tools.r8.org.objectweb.asm.ClassWriter;
import com.android.tools.r8.org.objectweb.asm.tree.AbstractInsnNode;
import com.android.tools.r8.org.objectweb.asm.tree.ClassNode;
import com.android.tools.r8.org.objectweb.asm.tree.InsnList;
import com.android.tools.r8.org.objectweb.asm.tree.MethodNode;
import com.android.tools.r8.org.objectweb.asm.util.CheckClassAdapter;
import com.hotfix.patchdispatcher.model.MethodIndexMappingModel;

import org.apache.http.util.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * jar 包修改工具
 *
 * @author: sqchan
 * @date: 2021-08-03 11:21
 */
public class ASMTestUtil {
    private static final String WORK_DIR = System.getProperty("user.dir");//user.dir指定了当前的路径
    private static final String DIR = WORK_DIR + "/modifyJar";
    private static final String JAR_DIR = DIR + "/jar";

    /**
     * 测试案例
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {
        String filePath = "/Users/sqchan/mine/R8CrashDemo/app/build/intermediates/javac/release/classes/com/crtip/test/r8crashdemo/utils/Test.class";
        byte[] origin = checkClass(filePath);
        try {
            writeClass(JAR_DIR + "/Test_origin.class", origin);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("origin class is right");
        String classPath = "com/crtip/test/r8crashdemo/utils/Test.class";
        byte[] after = generate(origin, classPath);
        try {
            writeClass(JAR_DIR + "/Test_after.class", after);
        } catch (IOException e) {
            e.printStackTrace();
        }
        check(after);
        System.out.println("modify class is right");
    }

    /**
     * 生成 hotfix 插桩后的 byte
     *
     * @param bytes
     * @param className
     * @return
     */
    private static byte[] generate(byte[] bytes, String className) {
        ClassReader reader = new ClassReader(bytes);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        ClassNode node = new ClassNode();
        reader.accept(node, 8);
        Map<String, Boolean> methodInstructionsTypeMap = new HashMap<>();
        List<MethodNode> methods = node.methods;
        for (MethodNode it : methods) {
            boolean isMethodInvoke = false;
            InsnList insnList = it.instructions;
            for (int i = 0; i < insnList.size(); i++) {
                if (insnList.get(i).getType() == AbstractInsnNode.METHOD_INSN) {
                    isMethodInvoke = true;
                    break;
                }
            }
            methodInstructionsTypeMap.put(it.name + it.desc, isMethodInvoke);
        }
        String classIndex = "222";
        List<MethodIndexMappingModel> models = new ArrayList<>();
        InsertCodeToMethodAdapter adapter = new InsertCodeToMethodAdapter(writer, className, classIndex, methodInstructionsTypeMap,
                (methodIndex, methodName) -> {
                    MethodIndexMappingModel methodModel = new MethodIndexMappingModel(handleOriginalMethodName(methodName), methodIndex);
                    models.add(methodModel);
                });
        reader.accept(adapter, org.objectweb.asm.ClassReader.EXPAND_FRAMES);
        return writer.toByteArray();
    }

    /**
     * 处理原始 class 内的方法名
     *
     * @param originalMethodName
     * @return
     */
    private static String handleOriginalMethodName(String originalMethodName) {
        if (TextUtils.isEmpty(originalMethodName)) {
            return null;
        }

        String signature = originalMethodName.substring(originalMethodName.indexOf("(") + 1, originalMethodName.indexOf(")"));
        //方法无参，忽略返回类型
        if (TextUtils.isEmpty(signature)) {
            return originalMethodName.substring(0, originalMethodName.indexOf(")") + 1);
        }

        String methodSimpleName = originalMethodName.substring(0, originalMethodName.indexOf("("));
        //方法含参，处理原始参数字段，忽略返回类型
        String[] slices = signature.split(";");
        Map<String, String> primitiveTypeMap = new HashMap<String, String>();
        primitiveTypeMap.put("B", "boolean");
        primitiveTypeMap.put("C", "char");
        primitiveTypeMap.put("D", "double");
        primitiveTypeMap.put("F", "float");
        primitiveTypeMap.put("I", "int");
        primitiveTypeMap.put("J", "long");
        primitiveTypeMap.put("S", "short");
        primitiveTypeMap.put("Z", "boolean");
        if (slices.length > 0) {
            for (String slice : slices) {
//                println("slice:" + slice)
                char start = slice.charAt(0);

                //非基本类型
                if (start == 'L') {
                    signature = signature.replace(slice, slice.substring(1).replaceAll("/", "\\."));
                } else if (primitiveTypeMap.containsKey(String.valueOf(start))) {
                    //扫描该string
                    //含非基本类型
                    if (slice.contains("/")) {
                        char[] array = slice.toCharArray();
                        List list = Arrays.asList(array);
                        int indexOfObject = list.indexOf("L");
                        if (indexOfObject > 0) {
                            //将所有基本类型作字符串替换
                            String primitiveStr = slice.substring(0, indexOfObject);
//                            println("primitiveStr:" + primitiveStr)
                            char[] primitiveChars = primitiveStr.toCharArray();
                            for (char c : primitiveChars) {
                                primitiveStr = primitiveStr.replace(String.valueOf(c), primitiveTypeMap.get(String.valueOf(c)) + ",");
                            }
//                            println("primitiveStr handled:" + primitiveStr)

                            //对非基本类型字符串作替换
                            String objectStr = slice.substring(indexOfObject + 1);
//                            println("objectStr:" + objectStr)
                            objectStr = objectStr.replaceAll("/", "\\.");
//                            println("objectStr handled:" + objectStr)

                            //最终替换
                            StringBuilder sliceReplacement = new StringBuilder();
                            sliceReplacement.append(primitiveStr).append(objectStr);

                            signature = signature.replace(slice, sliceReplacement.toString());
                        }
                    } else {
                        //全部基本类型
                        String primitiveStr = slice;
                        char[] primitiveChars = primitiveStr.toCharArray();
                        for (char c : primitiveChars) {
                            primitiveStr = primitiveStr.replace(String.valueOf(c), primitiveTypeMap.get(String.valueOf(c)) + ",");
                        }
                        signature = signature.replace(slice, primitiveStr.substring(0, primitiveStr.length() - 1));
                    }
                }
            }
        }

        signature = signature.replaceAll(";", ",");
        if (signature.endsWith(",")) {
            signature = signature.substring(0, signature.length() - 1);
        }

        StringBuilder handledMethodName = new StringBuilder();
        handledMethodName.append(methodSimpleName).append("(").append(signature).append(")");
        return handledMethodName.toString();
    }


    public static byte[] checkClass(String filePath) {
        File file = new File(filePath);
        long length = file.length();
        if (length > Integer.MAX_VALUE) System.out.println("file is too large");
        byte[] bytes = new byte[(int) length];
        try {
            FileInputStream inputStream = new FileInputStream(filePath);
            inputStream.read(bytes);
            check(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static void check(byte[] bytes) {
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(new CheckClassAdapter(589824, new ClassNode(), true) {
        }, 8);
    }

    public static void writeClass(String targetPath, byte[] bytes) throws IOException {
        FileOutputStream fos = new FileOutputStream(targetPath);
        fos.write(bytes, 0, bytes.length);
    }
}
