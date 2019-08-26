package com.wooddeep.crane.tookit;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by niuto on 2019/8/15.
 */

public class StringTool {

    /*
    private final char[] s;
    private final int n;
    private char current;
    public int pos;

    public ParserHelper(String str, int pos) {
        try {
            s = new char[str.length()];
            str.getChars(0, str.length(), this.s, 0); //<-- here
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.pos = pos;
        n = s.length;
        current = s[pos];
    }
    */

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

    public static void stringModify(String string, char[] value, Replace... replaces) {
        if (string.length() != value.length) return;
        string.getChars(0, string.length(), value, 0);
        for (int i = 0; i < replaces.length; i++) {
            findAndReplace(value, replaces[i]);
        }
    }

    public static void stringModify(char[] value, Replace... replaces) {
        for (int i = 0; i < replaces.length; i++) {
            findAndReplace(value, replaces[i]);
        }
    }

    public static void stringModify(String string, char[] value, char... repl) {
        if (string.length() != value.length) return;

        string.getChars(0, string.length(), value, 0);

        for (int i = 0; i < value.length; i++) {
            value[i] = ' ';
        }

        for (int i = 0; i < repl.length; i++) {
            value[i] = repl[i];
        }

    }

    public static void stringModify(String string, char[] value, int idata) {
        if (string.length() != value.length) return;

        for (int i = 0; i < 10; i++) {
            int remainder = (idata % 10);
            idata = idata / 10;
            floatArray[i] = (char) (remainder + ('0'));
            if (idata == 0) {
                floatArrayLen = i;  // 线程不安全
                break;
            }
        }

        string.getChars(0, string.length(), value, 0);

        for (int i = 0; i < value.length; i++) {
            value[i] = ' ';
        }

        for (int i = floatArrayLen - 1, j = 0; i >= 0; i--, j++) {
            value[value.length - 1 - j] = floatArray[i];
        }

    }

    public static BufferedReader stringReader(byte[] src) {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(src)));
        BufferedReader reader = new BufferedReader(new InputStreamReader(dis));
        //dis.reset();
        //Geometry gcc = wKTReader.read(new BufferedReader(new InputStreamReader(dis)));
        return reader;
    }

    public static void showCharArray(char [] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.printf("%c", array[i]);
        }
        System.out.println("");
    }

    public static Reader byteArrayReader(byte[] initialArray) {
        Reader targetReader = new InputStreamReader(new ByteArrayInputStream(initialArray));
        return targetReader;
    }

    public static void main(String[] args) {

        String mod = "LINESTRING (AAAAAA BBBBBB, CCCCCC DDDDDD)";
        byte [] modBytes = new byte[mod.length()];
        char [] modChars = new char[mod.length()];

        Replace a = new Replace(6);
        Replace b = new Replace(6);
        Replace c = new Replace(6);
        Replace d = new Replace(6);

        a.setTemplate('A', 'A', 'A', 'A', 'A', 'A');
        b.setTemplate('B', 'B', 'B', 'B', 'B', 'B');
        c.setTemplate('C', 'C', 'C', 'C', 'C', 'C');
        d.setTemplate('D', 'D', 'D', 'D', 'D', 'D');

        try {
            String line1 = String.format("LINESTRING (%f %f, %f %f)", 0f, 0f, 100f, 0f);
            Geometry g1 = new WKTReader().read(line1);
            //String line2 = String.format("LINESTRING (%f %f, %f %f)", 0f, 10f, 100f, 10f);

            a.setReplacement(0, 3, 1);
            b.setReplacement(10, 3, 1);
            c.setReplacement(100, 3,  1);
            d.setReplacement(10, 3,  1);
            mod.getChars(0, mod.length(), modChars, 0);
            stringModify(modChars, a, b, c, d);

            for (int i = 0; i < mod.length(); i++) {
                modBytes[i] = (byte)modChars[i];
            }
            Geometry g2 = new WKTReader().read(byteArrayReader(modBytes));
            System.out.println(g1.distance(g2));

            a.setReplacement(0, 3, 1);
            b.setReplacement(20, 3,  1);
            c.setReplacement(100, 3, 1);
            d.setReplacement(20, 3, 1);
            mod.getChars(0, mod.length(), modChars, 0);
            stringModify(modChars, a, b, c, d);

            for (int i = 0; i < mod.length(); i++) {
                modBytes[i] = (byte)modChars[i];
            }
            g2 = new WKTReader().read(byteArrayReader(modBytes));
            System.out.println(g1.distance(g2));

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
