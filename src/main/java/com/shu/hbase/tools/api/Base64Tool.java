package com.shu.hbase.tools.api;

import com.shu.hbase.config.springsecurity.tokenlogin.MAPIHttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Base64Tool {
    private static Logger logger = LoggerFactory.getLogger(MAPIHttpServletRequestWrapper.class);
    private Base64Tool() {
        throw new IllegalStateException("Base64Tool class");
    }


    //进行base64编码
    public static String fileToBase64(String fileSrc) {
        String imgFile = fileSrc;// 待处理的图片
        InputStream in = null;
        byte[] data = null;
// 读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            boolean count;
            while (count = in.read(data) > 0) {
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            IOUtils.closeQuietly(in);
        }
// 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);// 返回Base64编码过的字节数组字符串
    }


    public static byte[] base64ToFile(String imgStr) {

        BASE64Decoder decoder = new BASE64Decoder();
        try {
// Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
// 生成jpg图片
            return b;
        } catch (Exception e) {
            return new byte[1];
        }
    }
}
