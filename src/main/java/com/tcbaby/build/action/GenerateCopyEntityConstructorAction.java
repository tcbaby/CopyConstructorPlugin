package com.tcbaby.build.action;

import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.tcbaby.build.util.ConstructorUtil;
import com.tcbaby.build.handle.GenerateCopyEntityConstructorHandler;

public class GenerateCopyEntityConstructorAction extends BaseGenerateAction {

    public GenerateCopyEntityConstructorAction() {
        super(new GenerateCopyEntityConstructorHandler());
    }

    @Override
    protected boolean isValidForClass(PsiClass targetClass) {
        return super.isValidForClass(targetClass)
                && !(targetClass instanceof PsiAnonymousClass)
                && !ConstructorUtil.hasCopyConstructor(targetClass);
    }
}
