package com.crtip.test.hotfixdemo;

import com.android.tools.r8.org.objectweb.asm.Label;
import com.android.tools.r8.org.objectweb.asm.MethodVisitor;
import com.android.tools.r8.org.objectweb.asm.Opcodes;
import com.android.tools.r8.org.objectweb.asm.Type;
import com.android.tools.r8.org.objectweb.asm.commons.GeneratorAdapter;
import com.hotfix.patchdispatcher.ASMUtils;
import com.hotfix.patchdispatcher.IChangeDispatcher;

import java.util.ArrayList;
import java.util.List;


public class MethodInsertOperator extends GeneratorAdapter implements Opcodes {
    private static final String OWNERCLASSNAME = ASMUtils.class.getCanonicalName().replaceAll("\\.", "/");
    private static final String OWNERINTERFACENAME = IChangeDispatcher.class.getCanonicalName().replaceAll("\\.", "/");
    private static final String REDIRECTCLASSNAME = org.objectweb.asm.Type.getDescriptor(IChangeDispatcher.class);

    private int methodIndex = 0;
    private String classIndex;
    private Type[] descList;
    private Type returnType;
    private boolean mIsStatic;

    MethodInsertOperator(MethodVisitor mv, int access, String name, String desc, String classIndex, int methodIndex, boolean isStatic) {
        super(ASM8, mv, access, name, desc);
        this.methodIndex = methodIndex;
        this.classIndex = classIndex;
        descList = Type.getArgumentTypes(desc);
        returnType = Type.getReturnType(desc);
        mIsStatic = isStatic;
    }

    @Override
    public void visitCode() {
        List<String> argsList = new ArrayList<String>(descList.length);
        for (Type type : descList) {
            argsList.add(type.getDescriptor());
        }
        mv.visitCode();

        String returnTypeStr = returnType.getDescriptor();


        mv.visitLdcInsn(classIndex);
        mv.visitIntInsn(Opcodes.SIPUSH, methodIndex);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, OWNERCLASSNAME, "getInterface", "(Ljava/lang/String;I)" + REDIRECTCLASSNAME);
        Label l0 = new Label();
        mv.visitJumpInsn(Opcodes.IFNULL, l0);

        mv.visitLdcInsn(classIndex);
        mv.visitIntInsn(Opcodes.SIPUSH, methodIndex);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, OWNERCLASSNAME, "getInterface", "(Ljava/lang/String;I)" + REDIRECTCLASSNAME);


        //?????????????????????????????????
        mv.visitIntInsn(Opcodes.SIPUSH, methodIndex);

        //??????????????????
        if (argsList.size() == 0) {
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
        } else {
            RobustAsmUtil.createObjectArray(mv, argsList, mIsStatic);
        }

        //??????????????????this,???????????????static?????????????????????null
        if (mIsStatic) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        } else {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
        }

        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, OWNERINTERFACENAME, "accessFunc",
                "(I[Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

        //???????????????????????????????????????
        if ("V".equals(returnTypeStr)) {
            mv.visitInsn(Opcodes.POP);
            mv.visitInsn(Opcodes.RETURN);
        } else {
            //??????????????????
            if (!RobustAsmUtil.castPrimateToObj(mv, returnTypeStr)) {
                //???????????????????????????????????????????????????????????????????????????????????????????????????????????????,??????????????????????????????;
                //?????????Ljava/lang/String; ==??? java/lang/String
                String newTypeStr = null;
                int len = returnTypeStr.length();
                if (returnTypeStr.startsWith("[")) {
                    newTypeStr = returnTypeStr.substring(0, len);
                } else {
                    newTypeStr = returnTypeStr.substring(1, len - 1);
                }
                mv.visitTypeInsn(Opcodes.CHECKCAST, newTypeStr);
            }

            //?????????????????????????????????????????????????????????
            mv.visitInsn(RobustAsmUtil.getReturnTypeCode(returnTypeStr));
        }

        mv.visitLabel(l0);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    }

    @Override
    public void visitInsn(int opcode) {
        mv.visitInsn(opcode);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocal) {
        mv.visitMaxs(maxStack + 4, maxLocal);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}