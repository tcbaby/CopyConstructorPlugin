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

public class GenerateCopyEntityConstructorHandler extends GenerateMembersHandlerBase {

    public GenerateCopyEntityConstructorHandler() {
        super(null);
    }

    @Override
    protected String getNothingFoundMessage() {
        return "Copy constructor already exists";
    }

    @Override
    protected ClassMember[] getAllOriginalMembers(PsiClass aClass) {
        return toMembers(ConstructorUtil.getAllCopyableFields(aClass));
    }

    @Nullable
    @Override
    protected ClassMember[] chooseMembers(ClassMember[] members, boolean allowEmptySelection, boolean copyJavadocCheckbox, Project project, @Nullable Editor editor) {
        return members;
    }

    @NotNull
    @Override
    protected List<? extends GenerationInfo> generateMemberPrototypes(PsiClass aClass, ClassMember[] members) throws IncorrectOperationException {
        PsiMethod copyConstructor = generateCopyEntityConstructor(aClass, members);
        return Collections.singletonList(new PsiGenerationInfo<PsiMethod>(copyConstructor));
    }

    @Override
    protected GenerationInfo[] generateMemberPrototypes(PsiClass aClass, ClassMember originalMember) throws IncorrectOperationException {
        return null;
    }

    private PsiMethod generateCopyEntityConstructor(PsiClass psiClass, ClassMember[] copyableFields) {
//		String sourceClass = Messages.showInputDialog("Source Class", "Generate Code Constructor", Messages.getInformationIcon());
        String parameterName = "entity";
        String sourceClass = psiClass.getName();
        if (sourceClass.endsWith("Vo")) {
            sourceClass = sourceClass.substring(0, sourceClass.length() - 2);
        }

        StringBuilder code = new StringBuilder();
        code.append(String.format("public %s(%s %s) {", psiClass.getName(), sourceClass, parameterName));

        boolean superclassHasCopyConstructor = ConstructorUtil.hasCopyConstructor(psiClass.getSuperClass());
        if (superclassHasCopyConstructor) {
            code.append(String.format("super(%s);", parameterName));
        }

        for (ClassMember fieldMember : copyableFields) {
            PsiField field = ((PsiFieldMember) fieldMember).getElement();
            String name = field.getName();
            code.append(String.format("this.%s = %s.get%s();", name, parameterName, firstCharToUpperCase(name)));
        }
        code.append("}");

        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
        PsiMethod constructor = elementFactory.createMethodFromText(code.toString(), psiClass);
        return constructor;
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
