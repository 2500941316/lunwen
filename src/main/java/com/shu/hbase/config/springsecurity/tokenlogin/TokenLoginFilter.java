package com.shu.hbase.config.springsecurity.tokenlogin;


import com.shu.hbase.config.springsecurity.MyAuthenticationFailHandler;
import com.shu.hbase.exceptions.BusinessException;
import com.shu.hbase.exceptions.Exceptions;
import com.shu.hbase.tools.api.Md5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class TokenLoginFilter extends OncePerRequestFilter {
    private static Logger logger = LoggerFactory.getLogger(MAPIHttpServletRequestWrapper.class);
    private MyAuthenticationFailHandler myAuthenticationFaiureHandler = new MyAuthenticationFailHandler();

    public MyAuthenticationFailHandler getMyAuthenticationFaiureHandler() {
        return myAuthenticationFaiureHandler;
    }

    public void setMyAuthenticationFaiureHandler(MyAuthenticationFailHandler myAuthenticationFaiureHandler) {
        this.myAuthenticationFaiureHandler = myAuthenticationFaiureHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ServletRequest requestWrapper = null;
        if (request.getRequestURI().contains("commonAPI")) {
            try {

                requestWrapper = new MAPIHttpServletRequestWrapper(request);
                Map<String, Object> resultMap = MAPIHttpServletRequestWrapper.getBodyMap(requestWrapper.getInputStream());
                if (resultMap != null && !resultMap.isEmpty() && resultMap.get("userId") != null) {
                    String userId = (String) resultMap.get("userId");
                    String key = (String) resultMap.get("key");
                    String time = (String) resultMap.get("time");
                    String salt = "C6K02DUeJct3VGn7";
                    String text = userId + time + salt;
                    if (!Md5.verify(text, salt, key)) {
                        throw new BusinessException(Exceptions.SERVER_AUTH_ERROR.getEcode());
                    }
                    filterChain.doFilter(requestWrapper, response);
                    return;
                }
            } catch (AuthenticationException e) {
                myAuthenticationFaiureHandler.onAuthenticationFailure(request, response, e);
                return;
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        filterChain.doFilter(request, response);

    }
}

