package com.qunyi.modules.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qunyi.modules.pojo.Audience;
import com.qunyi.modules.pojo.ResultMsg;
import com.qunyi.modules.utils.JwtHelper;
import com.qunyi.modules.utils.ResultStatusCode;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.fasterxml.jackson.databind.ObjectMapper;


public class HTTPBearerAuthorizeAttribute implements Filter{
    @Autowired
    private Audience audienceEntity;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                filterConfig.getServletContext());

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // TODO Auto-generated method stub
        ResultMsg resultMsg;
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        System.out.println("###################测试拦截URl："+httpRequest.getRequestURI());
        String auth = httpRequest.getHeader("Authorization");
        if ((auth != null) && (auth.length() > 7))
        {
            String HeadStr = auth.substring(0, 6).toLowerCase();
            if (HeadStr.compareTo("bearer") == 0)
            {

                auth = auth.substring(7, auth.length());
                if (JwtHelper.parseJWT(auth, audienceEntity.getBase64Secret()) != null)
                {
                    httpResponse.setHeader("Authorization","bearer "+auth);
                    chain.doFilter(request, response);
                    return;
                }
            }
        }
        //添加支持参数传ticket验证
        String ticketParm =  httpRequest.getParameter("ticket");
        if ((ticketParm != null) &&!" ".equals(ticketParm)){
            Claims claims = JwtHelper.parseJWT(ticketParm, audienceEntity.getBase64Secret());
                if (claims != null)
                {
                    httpResponse.setHeader("Authorization","bearer "+ticketParm);
                    chain.doFilter(request, response);

                }
        }
        //添加支持cookie传ticket验证
        Cookie[] cookies= httpRequest.getCookies();

        if(null!=cookies){
            String ticketCookie = null;
            for(Cookie cookie : cookies){
                cookie.getName();// get the cookie name
                if(cookie.getName().equalsIgnoreCase("ticket")){
                    ticketCookie = cookie.getValue();
                    break;
                }
            }
            if ((ticketCookie != null) &&!" ".equals(ticketCookie)){
                if (JwtHelper.parseJWT(ticketCookie, audienceEntity.getBase64Secret()) != null)
                {
                    httpResponse.setHeader("Authorization","bearer "+ticketCookie);
                    chain.doFilter(request, response);

                }
            }

        }

        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json; charset=utf-8");
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ObjectMapper mapper = new ObjectMapper();
        resultMsg = new ResultMsg(ResultStatusCode.INVALID_TOKEN.getErrcode(), ResultStatusCode.INVALID_TOKEN.getErrmsg(),null, null);
        httpResponse.getWriter().write(mapper.writeValueAsString(resultMsg));
        return;
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }
}