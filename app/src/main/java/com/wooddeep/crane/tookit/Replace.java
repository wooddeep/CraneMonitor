package com.wooddeep.crane.tookit;

import java.lang.reflect.Field;

/**
 * Created by niuto on 2019/8/15.
 */

public class Replace {

    private int size;
    private int index = -1; // 该替换在 模板字符串中的其实位置
    private char[] template;
    private char[] replacement;

    private char[] floatArray;
    private int floatArrayLen = 0;

    public Replace(int size) {
        this.size = size;
        this.template = new char[size];
        this.replacement = new char[size];
        this.floatArray = new char[10]; // 最多10位的浮点数
    }

    public void setTemplate(char... cells) {
        if (cells.length > size) return;
        for (int i = 0; i < size; i++) {
            template[i] = ' ';
        }

        for (int i = cells.length - 1; i >= 0; i--) {
            template[i] = cells[i];
        }
    }

    public void setReplacement(char... cells) {
        if (cells.length > size) return;

        for (int i = 0; i < size - cells.length; i++) {
            replacement[i] = ' ';
        }

        for (int i = size - cells.length; i < size; i++) {
            replacement[i] = cells[i - (size - cells.length)];
        }
    }

    /**
     * @param data:    浮点数的值
     * @param decsize: 浮点数小数的位数
     * @description 小数 123.45 -> ['5', '4', '3', '2', '1']
     **/
    public void setReplacement(float data, int decsize) {

        int idata = (int) (data * (int) Math.pow(10, decsize));
        for (int i = 0; i < 10; i++) {
            int remainder = (idata % 10);
            idata = idata / 10;
            floatArray[i] = (char) (remainder + ('0'));
            //System.out.println(remainder);
            if (idata == 0) {
                setFloatArrayLen(i + 1); // 浮点数转数组的长度
                break;
            }
        }

        for (int k = 0; k < size; k++) {
            replacement[k] = ' ';
        }

        for (int j = 0; j < decsize; j++) {
            replacement[replacement.length - j - 1] = floatArray[j];
        }

        replacement[replacement.length - decsize - 1] = '.'; // 还原小数点


        for (int j = decsize; j < getFloatArrayLen(); j++) {
            replacement[replacement.length - j - 2] = floatArray[j];
        }

    }

    public void setReplacement(String string) {
        try {
            if (string.length() > replacement.length) return;
            Field valueField = String.class.getDeclaredField("value");
            valueField.setAccessible(true);
            char[] value = (char[]) valueField.get(string); // 得到字符串对应的byte数组

            for (int i = 0; i < replacement.length; i++) {
                replacement[i] = ' ';
            }

            for (int i = replacement.length - string.length(), j = 0; i < replacement.length; i++, j++) {
                replacement[i] = value[j];
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public char[] getTemplate() {
        return template;
    }

    public char[] getReplacement() {
        return replacement;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getFloatArrayLen() {
        return floatArrayLen;
    }

    public void setFloatArrayLen(int floatArrayLen) {
        this.floatArrayLen = floatArrayLen;
    }
}
