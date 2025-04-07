package cn.liaozh.core.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/*
 * JWT工具包
 */
public class JWTUtils {

    /**
     * 生成token
     **/
    public static String generateToken(JWTPayload jwtPayload, String jwtSecret) {
        return Jwts.builder()
                .setClaims(jwtPayload.toMap())
                //过期时间 = 当前时间 + （设置过期时间[单位 ：s ] ）  token放置redis 过期时间无意义
                //.setExpiration(new Date(System.currentTimeMillis() + (jwtExpiration * 1000) ))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * 根据token与秘钥 解析token并转换为 JWTPayload
     **/
    public static JWTPayload parseToken(String token, String secret) {
        try {
            // 将字符串密钥转换为安全的 SecretKey 实例
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

//            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

            // 使用新版 API 解析 Token
            Claims claims = Jwts.parser()
                    .verifyWith(key)          // 替换旧版 setSigningKey()
                    .build()
                    .parseSignedClaims(token) // 替换旧版 parseClaimsJws()
                    .getPayload();

            JWTPayload result = new JWTPayload();
            result.setSysUserId(claims.get("sysUserId", Long.class));
            result.setCreated(claims.get("created", Long.class));
            result.setCacheKey(claims.get("cacheKey", String.class));
            return result;

        } catch (Exception e) {
            return null; //解析失败
        }
    }

}
