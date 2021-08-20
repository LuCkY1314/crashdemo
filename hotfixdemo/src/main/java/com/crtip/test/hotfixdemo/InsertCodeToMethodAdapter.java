package com.crtip.test.hotfixdemo;

import com.android.tools.r8.org.objectweb.asm.ClassVisitor;
import com.android.tools.r8.org.objectweb.asm.ClassWriter;
import com.android.tools.r8.org.objectweb.asm.MethodVisitor;
import com.android.tools.r8.org.objectweb.asm.Opcodes;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chan on 2018/1/10.
 */

public class InsertCodeToMethodAdapter extends ClassVisitor implements Opcodes {
    private static final String CLASS_INITIALIZER = "<clinit>";
    private static final String CONSTRUCTOR = "<init>";

    private ClassWriter writer;
    private String className;
    private Map<String, Boolean> methodInstructionsTypeMap;
    private String classIndex;
    private OnMethodVisitedListener onMethodVisitedListener;

    private AtomicInteger modifiedMethodCount = new AtomicInteger(0);

    public InsertCodeToMethodAdapter(ClassWriter writer, String className, String classIndex, Map<String, Boolean> methodInstructionsTypeMap, OnMethodVisitedListener onMethodVisitedListener) {
        super(ASM8, writer);
        this.writer = writer;
        this.className = className;
        this.classIndex = classIndex;
        this.methodInstructionsTypeMap = methodInstructionsTypeMap;
        this.onMethodVisitedListener = onMethodVisitedListener;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (!isMethodNeedToInsert(access, name, desc, methodInstructionsTypeMap)) {
            return mv;
        }

        /**
         * desc: "(Lcom/example/chan/hotfixtest2/Calculator;IILjava/lang/String;)I"
         * */
        onMethodVisitedListener.onMethodVisited(modifiedMethodCount.incrementAndGet(), name + desc);

        return new MethodInsertOperator(mv, access, name, desc, classIndex, modifiedMethodCount.get(), isStatic(access));
    }

    private boolean isMethodNeedToInsert(int access, String name, String desc, Map<String, Boolean> methodInstructionsTypeMap) {
        //类初始化函数和构造函数过滤
        if (CLASS_INITIALIZER.equals(name) || CONSTRUCTOR.equals(name)) {
            return false;
        }

        // synthetic 方法暂时不aop 比如AsyncTask 会生成一些同名 synthetic方法,对synthetic 以及private的方法也插入的代码，主要是针对lambda表达式
        if (((access & Opcodes.ACC_SYNTHETIC) != 0) && ((access & Opcodes.ACC_PRIVATE) == 0)) {
            return false;
        }
        if ((access & Opcodes.ACC_ABSTRACT) != 0) {
            return false;
        }
        if ((access & Opcodes.ACC_NATIVE) != 0) {
            return false;
        }
        if ((access & Opcodes.ACC_INTERFACE) != 0) {
            return false;
        }

        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            return false;
        }

//        return methodInstructionsTypeMap.getOrDefault(name + desc, false);
        return true;
    }

    private boolean isProtect(int access) {
        return (access & Opcodes.ACC_PROTECTED) != 0;
    }

    private int setPublic(int access) {
        return (access & ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PUBLIC;
    }

    private boolean isStatic(int access) {
        return (access & Opcodes.ACC_STATIC) != 0;
    }

    public interface OnMethodVisitedListener {
        void onMethodVisited(int methodIndex, String methodName);
    }
}
