package com.esliceu.dwes.bootmvc.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;

@Configuration
public class BeansConfiguration {

    @Value("${api.url}")
    private String url;

    @Value("${jwt.secret}")
    private String secret;


    @Bean
    public RestTemplate restTemplate(){

        return new RestTemplate();
    }

    @Bean
    public Algorithm algorithm(){
        try {
            return  Algorithm.HMAC256(secret);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Bean
    public JWTVerifier jwtVerifier(){
        return JWT.require(algorithm()).build();
    }





}
