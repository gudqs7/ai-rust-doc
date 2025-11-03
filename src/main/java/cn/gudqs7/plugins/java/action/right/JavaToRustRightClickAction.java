package cn.gudqs7.plugins.java.action.right;

import cn.gudqs7.plugins.java.action.base.AbstractJavaRightClickAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

public class JavaToRustRightClickAction extends AbstractJavaRightClickAction {

    /**
     * 根据类信息判断是否应该展示
     *
     * @param psiClass 类
     * @param project  项目
     * @param e        e
     */
    @Override
    protected void checkPsiClass(PsiClass psiClass, Project project, AnActionEvent e) {

    }

    /**
     * 根据类获取展示信息
     *
     * @param project  项目
     * @param psiClass 类
     * @return 展示信息
     */
    @Override
    protected String handlePsiClass0(Project project, PsiClass psiClass) {

        return "开发中";
    }
}
