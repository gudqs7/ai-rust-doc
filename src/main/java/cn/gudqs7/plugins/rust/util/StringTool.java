package cn.gudqs7.plugins.rust.util;

/**
 * @author WQ
 */
public class StringTool {

    public static String rename(String origin, String renameType) {
        if ("camelCase".equals(renameType)) {
            origin = StringTool.lineToCamelCase(origin);
        }
        // todo 实现其他重命名规则
        return origin;
    }

    public static String lineToCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;

        for (int i = 0; i < str.length(); i++) {
            char currentChar = str.charAt(i);

            if (currentChar == '_') {
                // 遇到下划线，标记下一个字符需要大写
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    // 当前字符需要大写（驼峰位置）
                    result.append(Character.toUpperCase(currentChar));
                    nextUpperCase = false;
                } else {
                    // 第一个单词保持小写，后续单词首字母已在上面的逻辑中处理
                    if (result.isEmpty()) {
                        // 第一个字符保持小写
                        result.append(Character.toLowerCase(currentChar));
                    } else {
                        result.append(currentChar);
                    }
                }
            }
        }

        return result.toString();
    }

    public static String camelCaseToLine(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            char currentChar = str.charAt(i);

            // 如果当前字符是大写字母
            if (Character.isUpperCase(currentChar)) {
                // 如果不是第一个字符，在前面添加下划线
                if (i > 0) {
                    result.append('_');
                }
                // 将大写字母转换为小写
                result.append(Character.toLowerCase(currentChar));
            } else {
                // 小写字母或数字，直接添加
                result.append(currentChar);
            }
        }

        return result.toString();
    }
}
