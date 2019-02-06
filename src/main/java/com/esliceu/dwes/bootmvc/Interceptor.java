package com.esliceu.dwes.bootmvc;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class Interceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String header = request.getHeader("Authorization");

        if (header == null){
            response.addHeader("WWW-Authenticate","Basic realm='Send credentials.'");
            response.setStatus(401);
            return false;
        } else {

            header = header.split(" ")[1];

            String credentials = new String(Base64Utils.decode(header.getBytes()));

            String user = credentials.split(":")[0];
            String pass = credentials.split(":")[1];


        }

        return true;
    }
}
