package com.shu.hbase.tools.api;

import org.apache.commons.codec.digest.DigestUtils;

public class Md5 {
    private Md5() {
        throw new IllegalStateException("Utility class");
    }

    public static final String md5Key = "CXY05QUeJct3iGn9";

    /**
     * MD5方法
     *
     * @param text 明文
     * @param key  密钥
     * @return 密文
     * @throws Exception
     */
    public static String md5(String text, String key) {
        //加密后的字符串
        return DigestUtils.md5Hex(text + key);
    }

    /**
     * MD5验证方法
     *
     * @param text 明文
     * @param key  密钥
     * @param md5  密文
     * @return true/false
     * @throws Exception
     */
    public static boolean verify(String text, String key, String md5) {
        //根据传入的密钥进行验证
        String md5Text = md5(text, key);
        return md5Text.equalsIgnoreCase(md5);
    }
}