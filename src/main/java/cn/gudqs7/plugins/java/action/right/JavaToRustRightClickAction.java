package cn.gudqs7.plugins.java.action.right;

import cn.gudqs7.plugins.java.action.base.AbstractJavaRightClickAction;
import cn.gudqs7.plugins.java.util.PsiAnnotationUtil;
import cn.gudqs7.plugins.rust.util.StringTool;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        StringBuilder structSbf = new StringBuilder();
        Set<String> hasGenSet = new HashSet<>();
        generateByPsiClass(psiClass, structSbf, hasGenSet);
        return structSbf.toString();
    }

    private static void generateByPsiClass(PsiClass psiClass, StringBuilder structSbf, Set<String> hasGenSet) {
        String className = psiClass.getName();
        if (className == null) {
            return;
        }
        if (hasGenSet.contains(className)) {
            return;
        }
        hasGenSet.add(className);

        structSbf.append("\n#[derive(Serialize, Deserialize, Clone, Data, Reflect)]\n");
        structSbf.append("#[serde(rename_all = \"camelCase\")]\n");
        structSbf.append("pub struct ").append(className).append(" {\n");

        List<PsiClass> waitList = new ArrayList<>();

        PsiField[] allFields = psiClass.getAllFields();
        for (PsiField psiField : allFields) {
            String fieldName = psiField.getName();
            PsiType psiType = psiField.getType();
            String typeName = psiType.getPresentableText();

            PsiModifierList modifierList = psiField.getModifierList();
            if (modifierList != null) {
                if (modifierList.hasModifierProperty(PsiModifier.STATIC)) {
                    continue;
                }
            }

            if (psiType instanceof PsiClassReferenceType psiClassReferenceType) {
                PsiClass resolveClass = psiClassReferenceType.resolve();
                waitList.add(resolveClass);
            }

            String reflectFieldInfo = "";
            PsiAnnotation jsonAnnotation = psiField.getAnnotation("com.alibaba.fastjson.annotation.JSONField");
            if (jsonAnnotation != null) {
                String jsonFieldName = PsiAnnotationUtil.getAnnotationValue(jsonAnnotation, "name", null);
                if (StringUtils.isNotBlank(jsonFieldName)) {
                    reflectFieldInfo = "    #[reflect(@LuaField::new(\"" + jsonFieldName + "\"))]\n";
                }
            }

            String dbFieldInfo = "";
            PsiAnnotation dbAnnotation = psiField.getAnnotation("a.a.a.ExcelProperty");
            if (dbAnnotation != null) {
                List<String> dbValueList = PsiAnnotationUtil.getAnnotationListValue(dbAnnotation, "value");
                if (dbValueList != null && !dbValueList.isEmpty()) {
                    dbFieldInfo = "    #[reflect(@DbField::new(\"" + dbValueList.get(0) + "\"))]\n";
                }
            }

            System.out.println("fieldName = " + fieldName);
            System.out.println("typeName = " + typeName);

            String rustType = "Option<" + typeName + ">";
            switch (typeName) {
                case "Byte":
                    rustType = "Option<i8>";
                    break;
                case "Short":
                    rustType = "Option<i16>";
                    break;
                case "Integer":
                    rustType = "Option<i32>";
                    break;
                case "Long":
                    rustType = "Option<i64>";
                    break;
                case "Float":
                    rustType = "Option<f32>";
                    break;
                case "Double":
                    rustType = "Option<f64>";
                    break;
                case "Boolean":
                    rustType = "Option<bool>";
                    break;
                case "String":
                case "Date":
                    rustType = "Option<String>";
                    break;
                case "byte":
                    rustType = "i8";
                    break;
                case "short":
                    rustType = "i16";
                    break;
                case "int":
                    rustType = "i32";
                    break;
                case "long":
                    rustType = "i64";
                    break;
                case "float":
                    rustType = "f32";
                    break;
                case "double":
                    rustType = "f64";
                    break;
                case "boolean":
                    rustType = "bool";
                    break;
                default:
                    break;
            }

            structSbf.append(reflectFieldInfo).append(dbFieldInfo).append("    ").append(StringTool.camelCaseToLine(fieldName))
                    .append(": ").append(rustType).append(",\n");
        }
        structSbf.append("}\n");

        for (PsiClass aClass : waitList) {
            if (isNotSystemClass(aClass)) {
                generateByPsiClass(aClass, structSbf, hasGenSet);
            }
        }
    }

    public static boolean isNotSystemClass(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }
        String qualifiedName = psiClass.getQualifiedName();
        if (StringUtils.isBlank(qualifiedName)) {
            return false;
        }
        return !qualifiedName.startsWith("java.");
    }
}
