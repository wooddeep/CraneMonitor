package com.wooddeep.crane.tookit;

import java.lang.reflect.Field;

/**
 * Created by niuto on 2019/8/15.
 */

public class StringTool {

    private static char[] floatArray = new char[10];
    private static int floatArrayLen = 0;
    private static String formatString = "HelloWorldXXXXX YYYYY";
    private static Replace replace = new Replace(5); // 这个一定要做出单例模式
    private static Replace replaceY = new Replace(5); // 这个一定要做出单例模式

    private static void findAndReplace(char[] array, Replace repl) {
        char[] temp = repl.getTemplate();
        char[] replace = repl.getReplacement();

        if (repl.getIndex() == -1) {
            for (int i = 0; i <= (array.length - temp.length); i++) {
                int j = 0;
                for (; j < temp.length; j++) {
                    if (array[i + j] != temp[j]) {
                        break;
                    }
                }

                if (j == temp.length) {
                    repl.setIndex(i);
                }
            }
        }

        if (repl.getIndex() != -1) {
            for (int k = repl.getIndex(); k < repl.getIndex() + temp.length; k++) {
                array[k] = replace[k - repl.getIndex()];
            }
        }
    }

    public static void stringModify(String string, Replace... replaces) {
        try {

            Field valueField = String.class.getDeclaredField("value");
            valueField.setAccessible(true);
            char[] value = (char[]) valueField.get(string); // 得到字符串对应的byte数组

            for (int i = 0; i < replaces.length; i++) {
                findAndReplace(value, replaces[i]);
            }

            System.out.println(formatString);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stringModify(String string, char... repl) {
        try {

            if (string.length() < repl.length) return;
            Field valueField = String.class.getDeclaredField("value");
            valueField.setAccessible(true);
            char[] value = (char[]) valueField.get(string); // 得到字符串对应的byte数组

            for (int i = 0; i < value.length; i++) {
                value[i] = ' ';
            }

            for (int i = 0; i < repl.length; i++) {
                value[i] = repl[i];
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stringModify(String string, int idata) {

        for (int i = 0; i < 10; i++) {
            int remainder = (idata % 10);
            idata = idata / 10;
            floatArray[i] = (char) (remainder + ('0'));
            if (idata == 0) {
                floatArrayLen = i;  // 线程不安全
                break;
            }
        }

        try {
            if (string.length() < floatArrayLen) return;
            Field valueField = String.class.getDeclaredField("value");
            valueField.setAccessible(true);
            char[] value = (char[]) valueField.get(string); // 得到字符串对应的byte数组

            for (int i = 0; i < value.length; i++) {
                value[i] = ' ';
            }

            for (int i = floatArrayLen - 1, j = 0; i >= 0; i--, j++) {
                value[value.length - 1 - j] = floatArray[i];
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        replace.setTemplate('X', 'X', 'X', 'X', 'X');
        replace.setReplacement('A', 'B', 'C');
        stringModify(formatString, replace);

        replace.setReplacement('C', 'D', 'E');
        stringModify(formatString, replace);

        replace.setReplacement(1.15f, 2);
        stringModify(formatString, replace);

        replaceY.setTemplate('Y', 'Y', 'Y', 'Y', 'Y');
        replaceY.setReplacement('C', 'D', 'E');

        stringModify(formatString, replace, replaceY);
    }

}
