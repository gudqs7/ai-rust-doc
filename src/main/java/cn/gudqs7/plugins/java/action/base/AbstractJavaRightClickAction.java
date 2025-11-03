package cn.gudqs7.plugins.java.action.base;

import cn.gudqs7.plugins.common.action.AbstractAction;
import cn.gudqs7.plugins.common.util.jetbrain.ClipboardUtil;
import cn.gudqs7.plugins.common.util.jetbrain.DialogUtil;
import cn.gudqs7.plugins.common.util.jetbrain.ExceptionUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author wq
 */
public abstract class AbstractJavaRightClickAction extends AbstractAction {

    @Override
    public void onCheck(@NotNull AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        if (project == null) {
            notShow(e);
            return;
        }
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (psiElement == null) {
            notShow(e);
            return;
        }

        PsiMethod psiMethod = getPsiMethod(psiElement);
        PsiClass psiClass = getPsiClass(psiElement);
        boolean isRightClickOnMethod = psiMethod != null;
        boolean isRightClickOnClass = psiClass != null;

        // 啥也不是
        if (!isRightClickOnClass && !isRightClickOnMethod) {
            notShow(e);
            return;
        }

        if (isRightClickOnMethod) {
            checkPsiMethod(psiMethod, project, e);
        }
        if (isRightClickOnClass) {
            checkPsiClass(psiClass, project, e);
        }
    }

    @Override
    public void onClick(@NotNull AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        if (project == null) {
            return;
        }
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (psiElement == null) {
            return;
        }

        PsiMethod psiMethod = getPsiMethod(psiElement);
        boolean isRightClickOnMethod = psiMethod != null;
        PsiClass psiClass = getPsiClass(psiElement);
        boolean isRightClickOnClass = psiClass != null;

        if (isRightClickOnMethod) {
            handlePsiMethod(project, psiMethod);
        }

        if (isRightClickOnClass) {
            handlePsiClass(project, psiClass);
        }
    }

    protected PsiClass getPsiClass(PsiElement psiElement) {
        PsiClass psiClass = null;
        if (psiElement instanceof PsiClass) {
            psiClass = (PsiClass) psiElement;
        }
        if (psiElement instanceof PsiJavaFile psiJavaFile) {
            PsiClass[] classes = psiJavaFile.getClasses();
            for (PsiClass psiClass0 : classes) {
                PsiModifierList modifierList = psiClass0.getModifierList();
                if (modifierList != null) {
                    if (modifierList.hasModifierProperty(PsiModifier.PUBLIC)) {
                        return psiClass0;
                    }
                }
            }
        }
        return psiClass;
    }

    protected PsiMethod getPsiMethod(PsiElement psiElement) {
        PsiMethod psiMethod = null;
        if (psiElement instanceof PsiMethod) {
            psiMethod = (PsiMethod) psiElement;
        }
        return psiMethod;
    }

    protected PsiDirectory getPsiDirectory(PsiElement psiElement) {
        PsiDirectory psiDirectory = null;
        if (psiElement instanceof PsiDirectory) {
            psiDirectory = (PsiDirectory) psiElement;
        }
        return psiDirectory;
    }

    /**
     * 根据方法判断是否应该展示
     *
     * @param psiMethod 方法
     * @param project   项目
     * @param e         e
     */
    protected void checkPsiMethod(PsiMethod psiMethod, Project project, AnActionEvent e) {
        notShow(e);
    }

    /**
     * 根据类信息判断是否应该展示
     *
     * @param psiClass 类
     * @param project  项目
     * @param e        e
     */
    protected void checkPsiClass(PsiClass psiClass, Project project, AnActionEvent e) {
        notShow(e);
    }

    /**
     * 当在类上右键时, 要做的操作
     *
     * @param project  项目
     * @param psiClass 类
     */
    protected void handlePsiClass(Project project, PsiClass psiClass) {
        String showContent = handlePsiClass0(project, psiClass);
        ClipboardUtil.setSysClipboardText(showContent);
        DialogUtil.showDialog(project, getTip(), showContent);
    }

    /**
     * 当在方法上右键时, 要做的操作
     *
     * @param project   项目
     * @param psiMethod 方法
     */
    protected void handlePsiMethod(Project project, PsiMethod psiMethod) {
        PsiClass containingClass = psiMethod.getContainingClass();
        if (containingClass == null) {
            ExceptionUtil.handleSyntaxError(psiMethod.getName() + "'s Class");
        }
        String psiClassName = containingClass.getQualifiedName();
        String docByMethod = handlePsiMethod0(project, psiMethod, psiClassName);
        ClipboardUtil.setSysClipboardText(docByMethod);
        DialogUtil.showDialog(project, getTip(), docByMethod);
    }

    /**
     * 根据类获取展示信息
     *
     * @param project  项目
     * @param psiClass 类
     * @return 展示信息
     */
    protected String handlePsiClass0(Project project, PsiClass psiClass) {
        return null;
    }

    /**
     * 根据方法获取展示信息
     *
     * @param project      项目
     * @param psiMethod    方法
     * @param psiClassName 类名
     * @return 展示信息
     */
    protected String handlePsiMethod0(Project project, PsiMethod psiMethod, String psiClassName) {
        return null;
    }

    /**
     * 设置弹框中的首行提示
     *
     * @return 提示
     */
    protected String getTip() {
        return null;
    }

}
