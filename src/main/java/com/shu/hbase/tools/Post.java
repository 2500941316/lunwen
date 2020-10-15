package com.shu.hbase.tools;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shu.hbase.pojo.UploadFileVO;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class Post {
    private static ObjectMapper objectMapper = new ObjectMapper();


    public static void Post(String filePath,int chunks,int chunk,String fileMD5) throws Exception {


        File file = new File(filePath);
        System.out.println("文件大小为：" + file.length());

        InputStream in = new FileInputStream(file);
        byte[] data = new byte[in.available()];
        in.read(data);
        in.close();
        System.out.println("data数组大小为：" + data.length);
        String fileStr = new String(Base64.getEncoder().encode(data));

        UploadFileVO uploadFileVO = new UploadFileVO();
        uploadFileVO.setFileMD5(fileMD5);
        uploadFileVO.setFileStr(fileStr);
        uploadFileVO.setAppId("19721631");
        uploadFileVO.setChunk(chunk);
        uploadFileVO.setChunks(chunks);
        uploadFileVO.setFileName("testMerge.jpg");
        String json = objectMapper.writeValueAsString(uploadFileVO);
        sendPost("http://localhost:8080/upload", json);

    }

    public static void writeOutput(String str, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            Writer out = new OutputStreamWriter(fos, "GBK");
            out.write(str);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readInput(File file) {
        StringBuffer buffer = new StringBuffer();
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "GBK");
            Reader in = new BufferedReader(isr);
            int i;
            while ((i = in.read()) > -1) {
                buffer.append((char) i);
            }
            in.close();
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String sendPost(String url, String json) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestMethod("POST");
            // 发送POST请求必须设置下面的属性
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            //设置请求属性
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(json);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            while ((line = in.readLine()) != null) {
                result += line;
            }
            //将返回结果转换为字符串
        } catch (Exception e) {
            throw new RuntimeException("远程通路异常" + e.toString());
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println(result);
        return result;
    }

}
