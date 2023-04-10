package com.idaymay.dzt.common.utils.web;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.idaymay.dzt.common.exception.TokenUnavailable;

import java.util.Base64;

/**
 * @Description TODO
 * @ClassName JwtUtils
 * @Author littlehui
 * @Date 2021/6/26 21:02
 * @Version 1.0
 **/
public class JwtUtils {

    /**
     * 检验合法性，其中secret参数就应该传入的是用户的id
     * @param token
     * @throws TokenUnavailable
     */
    public static void verifyToken(String token, String secret) throws TokenUnavailable {
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            jwt = verifier.verify(token);
        } catch (Exception e) {
            //效验失败
            //这里抛出的异常是我自定义的一个异常，你也可以写成别的
            throw new TokenUnavailable();
        }
    }

    /**
     * 获取签发对象
     */
    public static String getAudience(String token) throws TokenUnavailable {
        String audience = null;
        try {
            audience = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            //这里是token解析失败
            throw new TokenUnavailable();
        }
        return audience;
    }

    private DecodedJWT decode(String accessToken) {
        return JWT.decode(accessToken);
    }

    /**
     * 通过载荷名字获取载荷的值
     */
    public static Claim getClaimByName(String token, String name){
        return JWT.decode(token).getClaim(name);
    }

    public static void main(String[] args) {
        //String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL3Bhc3Nwb3J0LjF5LmNvbS9hcGkvbW9iaWxlL2xvZ2luIiwiaWF0IjoxNjU5MzQyMzkwLCJleHAiOjE2NjE5MzQzOTAsIm5iZiI6MTY1OTM0MjM5MCwianRpIjoiQ3BTdDJObVVOZEFwM2hkcSIsInN1YiI6IjIxMzEyMzg3MDkxMjE4MyIsInBydiI6IjIzYmQ1Yzg5NDlmNjAwYWRiMzllNzAxYzQwMDg3MmRiN2E1OTc2ZjcifQ.DMG2L4Vrt-7NR3vhhffSxz0Q4eSgiVR0vO2aHTLpFmtdPaP1wZHwbE9pGbK1Wwyw0Z0EYJv52IsosPSibRJVvN4CdHnLkNQ4CC6mSIBBmrwXB_zCgLOZGczaasfuLH-Vh8QX50GN7geBiB4TaqDWiZMCXXF88_kWJvIOfUHfrAczEh2Y4YACM7Ioj386H3aO6Xpu_j_95jeYtjGBxq1hNu0O1PIRieJ1ziork6CD8TBc8Xk639CXZ6LN1rI8SLZ-31ph8xlYuPET-tMnW6o94zo0DQPYlxCUqS9IJ_UD3y0cepF0ikmFFl2RvilVCzblDfEkz3ooJD3CSqylpzQxKA";
        String token = "1eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL3Bhc3Nwb3J0LjF5LmNvbS9hcGkvbW9iaWxlL2xvZ2luIiwiaWF0IjoxNjU5MzQyMzkwLCJleHAiOjE2NjE5MzQzOTAsIm5iZiI6MTY1OTM0MjM5MCwianRpIjoiQ3BTdDJObVVOZEFwM2hkcSIsInN1YiI6IjIxMzEyMzg3MDkxMjE4MyIsInBydiI6IjIzYmQ1Yzg5NDlmNjAwYWRiMzllNzAxYzQwMDg3MmRiN2E1OTc2ZjcifQ.DMG2L4Vrt-7NR3vhhffSxz0Q4eSgiVR0vO2aHTLpFmtdPaP1wZHwbE9pGbK1Wwyw0Z0EYJv52IsosPSibRJVvN4CdHnLkNQ4CC6mSIBBmrwXB_zCgLOZGczaasfuLH-Vh8QX50GN7geBiB4TaqDWiZMCXXF88_kWJvIOfUHfrAczEh2Y4YACM7Ioj386H3aO6Xpu_j_95jeYtjGBxq1hNu0O1PIRieJ1ziork6CD8TBc8Xk639CXZ6LN1rI8SLZ-31ph8xlYuPET-tMnW6o94zo0DQPYlxCUqS9IJ_UD3y0cepF0ikmFFl2RvilVCzblDfEkz3ooJD3CSqylpzQxKA";

        DecodedJWT decodedJWT = JWT.decode(token);
        Base64.getDecoder().decode(token);
    }

}
