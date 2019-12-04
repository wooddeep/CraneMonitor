package com.wooddeep.crane.net.crypto;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Aes {

    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[cipher.getBlockSize()];
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), ivParams);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] encrypted, int off, int len, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] ivByte = new byte[cipher.getBlockSize()];
        IvParameterSpec ivParamsSpec = new IvParameterSpec(ivByte);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), ivParamsSpec);
        return cipher.doFinal(encrypted, off, len);
    }

    public static Cipher getECipher(String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
            kgen.init(128, new SecureRandom(password.getBytes()));// 利用用户密码作为随机数初始化出
            SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
            byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥，如果此密钥不支持编码，则返回
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance("AES/ECB/ZeroBytePadding");// 创建密码器
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化为加密模式的密码器
            return cipher;
        } catch (NoSuchPaddingException e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        } catch (InvalidKeyException e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        }
        return null;
    }

    public static Cipher getDCipher(String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
            byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES/ECB/ZeroBytePadding");// 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化为解密模式的密码器
            return cipher;
        } catch (NoSuchPaddingException e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        } catch (InvalidKeyException e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        }
        return null;
    }


    public static byte[] encrypt(String content) {
        try {
            byte[] byteContent = content.getBytes("utf-8");

            Cipher cipher = getECipher("BDE1236987450ACF");

            byte[] result = cipher.doFinal(byteContent);// 加密

            return result;

        } catch (UnsupportedEncodingException e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        } catch (IllegalBlockSizeException e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        } catch (BadPaddingException e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        }
        return null;
    }


    /**
     * AES加密字符串
     *
     * @param content  需要被加密的字符串
     * @param password 加密需要的密码
     * @return 密文
     */

    /*
    public static byte[] encrypt(String content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
            kgen.init(128, new SecureRandom(password.getBytes()));// 利用用户密码作为随机数初始化出
            SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
            byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥，如果此密钥不支持编码，则返回
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化为加密模式的密码器

            byte[] result = cipher.doFinal(byteContent);// 加密
            return result;

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
    */

    /**
     * 解密AES加密过的字符串
     *
     * @param content  AES加密过过的内容
     * @param password 加密时的密码
     * @return 明文
     */
    public static byte[] decrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
            byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化为解密模式的密码器

            byte[] result = cipher.doFinal(content);
            return result; // 明文

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(byte[] content) {
        try {
            Cipher cipher = getDCipher("BDE1236987450ACF");
            byte[] result = cipher.doFinal(content);
            return result; // 明文

        } catch (IllegalBlockSizeException e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        } catch (BadPaddingException e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        }
        return null;
    }

//    public static void main(String[] args) {
//        String content = "美女，约吗？";
//        String password = "BDE1236987450ACF";
//        System.out.println("加密之前：" + content);
//
//        // 加密
//        byte[] encrypt = Aes.encrypt(content);
//        String AES_encode = new String(new BASE64Encoder().encode(encrypt));
//        System.out.println("加密后的内容：" + AES_encode);
//
//        // 解密
//        byte[] byte_content = new byte[0];
//        try {
//            byte_content = new BASE64Decoder().decodeBuffer(AES_encode);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        byte[] decrypt = Aes.decrypt(byte_content);
//        System.out.println("解密后的内容：" + new String(decrypt));
//    }


}
