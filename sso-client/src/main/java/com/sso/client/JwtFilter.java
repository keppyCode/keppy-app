//package com.sso.resource;
//
//import org.slf4j.LoggerFactory;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.logging.Logger;
//
///**
// * 统一处理客户端
// */
//public class JwtFilter implements Filter {
//    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JwtFilter.class);
//    private static final String jwtTokenCookieName = "JWT-TOKEN";
//    private static final String signingKey = "signingKey";
//    private static final String logout = "logout";
//    private  String authService;
//    @Override
//    public void destroy() {
//
//    }
//    @Override
//    public void doFilter(ServletRequest srequest, ServletResponse sresponse, FilterChain
//            filterChain) throws ServletException, IOException {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) srequest;
//        HttpServletResponse httpServletResponse = (HttpServletResponse) sresponse;
//        permitCorsRequest(httpServletResponse);
//        logger.info("登录认证开始=============================authService："+authService);
//        //登出
//        if(httpServletRequest.getRequestURL().indexOf(logout)!=-1){
//            logger.info("登录出开始=============================");
//            CookieUtil.clear(httpServletResponse, jwtTokenCookieName);
//            httpServletResponse.sendRedirect(authService);
//            return;
//        }
//        String username = JwtUtil.getSubject(httpServletRequest, jwtTokenCookieName, signingKey);
//        if(username == null){
//            httpServletResponse.sendRedirect(authService + "?redirect=" + httpServletRequest.getRequestURL());
//        } else{
//            httpServletRequest.setAttribute("username", username);
//            filterChain.doFilter(httpServletRequest, httpServletResponse);
//        }
//    }
//
//
//    @Override
//    public void init(FilterConfig arg0) throws ServletException {
//        logger.info("FilterConfig>>>>init#######");
//        authService = arg0.getInitParameter("services.auth");
//
//    }
//
//    /**
//     * 跨域设置
//     * @param httpServletResponse
//     */
//    protected  void permitCorsRequest( HttpServletResponse httpServletResponse){
//
//        httpServletResponse.setHeader("Access-Control-Allow-Origin", "http://localhost");
//        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST,GET");
//        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
//        httpServletResponse.setHeader("Access-Control-Allow-Headers","*");
//    }
//
//}
