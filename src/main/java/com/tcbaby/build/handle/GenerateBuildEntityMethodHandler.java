package com.tcbaby.build.handle;

import com.intellij.codeInsight.generation.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.tcbaby.build.util.ConstructorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author tcbaby
 * @date 2020/9/7 9:48
 */
public class GenerateBuildEntityMethodHandler extends GenerateMembersHandlerBase {

    public GenerateBuildEntityMethodHandler() {
        super(null);
    }

    @Override
    protected ClassMember[] getAllOriginalMembers(PsiClass psiClass) {
        return toMembers(ConstructorUtil.getAllCopyableFields(psiClass));
    }

    @Override
    protected GenerationInfo[] generateMemberPrototypes(PsiClass psiClass, ClassMember classMember) throws IncorrectOperationException {
        return null;
    }

    @Nullable
    @Override
    protected ClassMember[] chooseMembers(ClassMember[] members, boolean allowEmptySelection, boolean copyJavadocCheckbox, Project project, @Nullable Editor editor) {
        return members;
    }

    @NotNull
    @Override
    protected List<? extends GenerationInfo> generateMemberPrototypes(PsiClass psiClass, ClassMember[] classMember) throws IncorrectOperationException {
        PsiMethod buildTargetMethod = generateBuildEntityMethod(psiClass, classMember);
        return Collections.singletonList(new PsiGenerationInfo<PsiMethod>(buildTargetMethod));
    }

    private PsiMethod generateBuildEntityMethod(PsiClass psiClass, ClassMember[] copyableFields) {
//        String targetClass = Messages.showInputDialog("Target Class", "Generates a build target method", Messages.getInformationIcon());
        String parameterName = "entity";
        String targetClass = psiClass.getName();
        if (targetClass.endsWith("Vo")) {
            targetClass = targetClass.substring(0, targetClass.length() - 2);
        }

        StringBuilder code = new StringBuilder();
        code.append(String.format("public %s buildOrUpdate%s(%s %s) {", targetClass, targetClass, targetClass, parameterName));
        code.append("if (" + parameterName + " == null) {");
        code.append(String.format("%s = new %s();", parameterName, targetClass));
        code.append("}");

        for (ClassMember fieldMember : copyableFields) {
            PsiField field = ((PsiFieldMember) fieldMember).getElement();
            String name = field.getName();
            // ignore id
            if (!name.equals("id")) {
                code.append(String.format("%s.set%s(%s);", parameterName, firstCharToUpperCase(name), name));
            }
        }
        code.append("return " + parameterName + ";");
        code.append("}");

        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
        PsiMethod method = elementFactory.createMethodFromText(code.toString(), psiClass);
        return method;
    }

    public String firstCharToLowerCase(String str) {
        return str.length() > 0 ? str.substring(0, 1).toLowerCase() + str.substring(1) : str;
    }

    public String firstCharToUpperCase(String str) {
        return str.length() > 0 ? str.substring(0, 1).toUpperCase() + str.substring(1) : str;
    }

    public boolean isClassName(String className) {
        char char0;
        return className != null && className.length() > 1 &&
                (char0 = className.charAt(0)) >= 'A' && char0 <= 'z';
    }

    private ClassMember[] toMembers(List<PsiField> allCopyableFields) {
        ClassMember[] classMembers = new ClassMember[allCopyableFields.size()];
        for (int i = 0; i < allCopyableFields.size(); i++) {
            classMembers[i] = new PsiFieldMember(allCopyableFields.get(i));
        }
        return classMembers;
    }
}
