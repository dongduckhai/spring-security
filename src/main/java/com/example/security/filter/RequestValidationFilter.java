package com.example.security.filter;

import jakarta.servlet.*;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class RequestValidationFilter implements Filter {
    //tự custom 1 filter không cho phép email bao gồm chữ "test"
    public static final String AUTHENTICATION_SCHEME_BASIC = "Basic";
    private Charset credentialsCharset = StandardCharsets.UTF_8;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        //đọc header tên Authorization
        //Authorization là 1 header chứa username, pwd mà ng dùng cuối nhập vào để gửi lên serve
        //giá trị của header Authorization có dạng Basic <Base64Endcode {username:pwd}>
        String header = req.getHeader(AUTHORIZATION);
        if (header != null) {
            header = header.trim(); //xóa khoảng trắng đầu và cuối chuỗi
            if (StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BASIC)) {
                //nếu header bắt đầu bằng "Basic" (AUTHENTICATION_SCHEME_BASIC = Basic đ/n ở trên) k quan tâm uppercase
                //lấy substring từ header bắt đầu từ index = 6 (bỏ chữ Basic và khoảng trắng đi)
                //rồi lấy giá trị Base64 của token
                byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
                byte[] decoded;
                try {
                    //giải mã Base64 token
                    decoded = Base64.getDecoder().decode(base64Token);
                    //tạo 1 đối tượng String từ Base64 token theo quy chuẩn credentialsCharset (UTF_8)
                    String token = new String(decoded, credentialsCharset);
                    //token giờ có dạng ....(email) : ....(password)
                    //xác định vị trí của ":" rồi lấy đoạn token email
                    int delim = token.indexOf(":");
                    if (delim == -1) {
                        throw new BadCredentialsException("Invalid basic authentication token");
                    }
                    String email = token.substring(0, delim);
                    if (email.toLowerCase().contains("test")) { //nếu đoạn token email có chữ "test" thì báo lỗi
                        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }
                } catch (IllegalArgumentException e) {
                    throw new BadCredentialsException("Failed to decode basic authentication token");
                }
            }
        }
        chain.doFilter(request, response);
    }
}
