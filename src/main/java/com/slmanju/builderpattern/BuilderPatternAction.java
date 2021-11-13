package com.slmanju.builderpattern;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.Objects;

import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;

/**
 * @author Manjula Jayawardana
 */
public class BuilderPatternAction extends AnAction {

  @Override
  public void actionPerformed(AnActionEvent actionEvent) {
    PsiFile psiFile = actionEvent.getData(CommonDataKeys.PSI_FILE);
    Editor editor = actionEvent.getData(CommonDataKeys.EDITOR);

    assert editor != null;
    assert psiFile != null;
    int offset = editor.getCaretModel().getOffset();
    PsiElement elementAt = psiFile.findElementAt(offset);
    PsiClass targetClass = getParentOfType(elementAt, PsiClass.class);

    if (Objects.nonNull(targetClass)) {
      WriteCommandAction
          .writeCommandAction(targetClass.getProject())
          .run(() -> StandardBuilderGenerator.getInstance(targetClass).generate());
    } else {
      HintManager.getInstance().showErrorHint(editor, "Builder must be generated inside class");
    }
  }

}
