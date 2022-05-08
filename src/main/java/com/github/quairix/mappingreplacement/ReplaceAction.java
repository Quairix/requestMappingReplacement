package com.github.quairix.mappingreplacement;

import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static com.github.quairix.mappingreplacement.NotifyReplacement.notifyInfo;
import static com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction;


public class ReplaceAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        // show only for java files
        e.getPresentation().setEnabledAndVisible(psiFile != null && psiFile.getLanguage().isKindOf(JavaLanguage.INSTANCE));
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        final Language language = e.getData(CommonDataKeys.LANGUAGE);

        final String title = "@RequestMapping Replacement";
        final Project project = e.getProject();

        if (language == null || !language.is(JavaLanguage.INSTANCE)) {
            notifyInfo(project, title, "Not java file", NotificationType.INFORMATION);
            return;
        }

        if (editor != null) {
            final Document document = editor.getDocument();
            final String text = document.getText();
            final String[] methods = {"Get", "Post", "Put", "Delete", "Patch"};
            String newText = text;
            for (String method : methods) {
                newText = getReplacementString(newText, method);
            }
            final String result = newText;
            runWriteCommandAction(project, () ->
                    document.setText(result)
            );
            notifyInfo(project, title, text.equals(result) ? "Nothing has been replaced" : "Replacement complete", NotificationType.INFORMATION);
        } else {
            notifyInfo(project, title, "Empty editor!", NotificationType.WARNING);
        }
    }

    @NotNull
    private String getReplacementString(final String text, final String method) {
        return text.replaceAll("@RequestMapping\\(((.*),)?\\s*method\\s*=\\s*RequestMethod." +
                        method.toUpperCase(Locale.ROOT) + "\\s*\\)",
                "@" + method + "Mapping\\($2\\)");
    }


    @Override
    public boolean isDumbAware() {
        return false;
    }
}
