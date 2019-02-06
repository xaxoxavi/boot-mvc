package com.esliceu.dwes.bootmvc;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.esliceu.dwes.authentication.AuthClient;
import com.esliceu.dwes.authentication.model.Authenticate;
import com.esliceu.dwes.authentication.model.Status;
import com.esliceu.dwes.authentication.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class Interceptor extends HandlerInterceptorAdapter {

    @Autowired
    private AuthClient authClient;

    @Autowired
    private Algorithm algorithm;

    @Autowired
    private JWTVerifier jwtVerifier;



    private final long expireTime = (1 * 60 * 1000);

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String header = request.getHeader("Authorization");

        if (header == null){
            response.addHeader("WWW-Authenticate","Basic realm='Send credentials.'");
            response.setStatus(401);
            return false;
        } else {

            if (header.contains("Basic")) {

                header = header.split(" ")[1];

                String credentials = new String(Base64Utils.decode(header.getBytes()));

                String user = credentials.split(":")[0];
                String pass = credentials.split(":")[1];

                User userApi = new User();
                userApi.setUserId(user);
                userApi.setPassword(pass);

                Authenticate authenticate = authClient.authenticate(userApi);

                if (Status.failed == authenticate.getStatus()) {

                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.addHeader("WWW-Authenticate", "Basic realm=''");
                    return false;

                }

                String token = "";
                try {

                    token = JWT.create()
                            .withExpiresAt(new Date(System.currentTimeMillis() + expireTime))
                            .withIssuedAt(new Date())
                            .withSubject(authenticate.getUser().getUserId())
                            .sign(algorithm);
                } catch (JWTCreationException exception) {
                    //Invalid Signing configuration / Couldn't convert Claims.
                }

                response.addHeader("Authentication", "Bearer " + token);
            }
            else if (header.contains("Bearer")){

                String token = getToken(header);

                DecodedJWT jwt;
                try {
                    jwt = jwtVerifier.verify(token);
                } catch (JWTVerificationException exception){

                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.addHeader("WWW-Authenticate", "Basic realm=''");
                    return false;
                }

                Date tokenExpiresAt = jwt.getExpiresAt();

                if (System.currentTimeMillis() > tokenExpiresAt.getTime()) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.addHeader("WWW-Authenticate", "Basic realm=''");
                    return false;
                }
            }

        }

        return true;
    }

    private String getToken(String header) {
        //Bearer alskdjlasd.asdjlasdlas.alksjdklajsdls
        return header.split(" ")[1];
    }
}
