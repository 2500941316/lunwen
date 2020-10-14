package com.shu.hbase.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class Get {
    private static Logger logger = LoggerFactory.getLogger(Get.class);

    /**
     * 向指定 URL 发送 GET请求
     *
     * @param strUrl        发送请求的 URL
     * @param requestParams 请求参数
     * @return 远程资源的响应结果
     */
    public static String sendGet(String strUrl, String requestParams, String token) {
        String responseParams = "";
        BufferedReader bufferedReader = null;
        try {
            String strRequestUrl = "";
            if (!requestParams.isEmpty()) {
                strRequestUrl = strUrl + "?" + requestParams;
            } else strRequestUrl = strUrl;

            URL url = new URL(strRequestUrl);
            URLConnection urlConnection = url.openConnection();    // 打开与 URL 之间的连接

            // 设置通用的请求属性
            urlConnection.setRequestProperty("accept", "*/*");
            if (!token.equals("")) {
                urlConnection.setRequestProperty("Authorization", token);
            }
            urlConnection.connect();    // 建立连接

            Map<String, List<String>> map = urlConnection.getHeaderFields();    // 获取所有响应头字段

            // 使用BufferedReader输入流来读取URL的响应
            bufferedReader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            String strLine;
            while ((strLine = bufferedReader.readLine()) != null) {
                responseParams += strLine;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e2) {
                logger.error(e2.getMessage());
            }
        }
        return responseParams;
    }
}
