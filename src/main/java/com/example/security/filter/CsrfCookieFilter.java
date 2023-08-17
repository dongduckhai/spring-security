package com.example.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CsrfCookieFilter extends OncePerRequestFilter {
    //csrfToken sẽ được đính kèm trong header của response, sau đó
    //SpringBoot tự tạo ra csrfCookie giống vậy và gửi cho trình duyệt lưu
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //lấy csrfToken trong http request
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if(null != csrfToken.getHeaderName()){
            //gán token vào header của response
            response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());
        }
        //bàn giao response cho filterChain
        filterChain.doFilter(request, response);
    }

}
