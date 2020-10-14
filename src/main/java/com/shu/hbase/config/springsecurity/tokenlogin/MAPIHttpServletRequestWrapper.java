package com.shu.hbase.config.springsecurity.tokenlogin;

import com.alibaba.fastjson.JSONObject;
import com.shu.hbase.tools.api.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MAPIHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] body;
    private static Logger logger = LoggerFactory.getLogger(MAPIHttpServletRequestWrapper.class);

    public MAPIHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        InputStream is = request.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int read;
        while ((read = is.read(buff)) > 0) {
            baos.write(buff, 0, read);
        }
        body = baos.toByteArray();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // Do nothing
            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }

    //获取request请求body中参数
    public static Map<String, Object> getBodyMap(InputStream in) {
        String param = null;
        BufferedReader streamReader = null;
        try {
            streamReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            if (!JsonUtil.getInstance().validate(responseStrBuilder.toString())) {
                return new HashMap<>();
            }
            JSONObject jsonObject = JSONObject.parseObject(responseStrBuilder.toString());
            if (jsonObject == null) {
                return new HashMap<>();
            }
            param = jsonObject.toJSONString();
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return JSONObject.parseObject(param, Map.class);
    }
}
