package com.tcbaby.build.action;

import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.tcbaby.build.handle.GenerateBuildEntityMethodHandler;

public class GenerateBuildEntityMethodAction extends BaseGenerateAction {

    public GenerateBuildEntityMethodAction() {
        super(new GenerateBuildEntityMethodHandler());
    }

    @Override
    protected boolean isValidForClass(PsiClass targetClass) {
        return super.isValidForClass(targetClass)
                && !(targetClass instanceof PsiAnonymousClass);
    }
}
