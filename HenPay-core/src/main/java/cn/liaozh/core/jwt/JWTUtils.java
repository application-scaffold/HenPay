package cn.liaozh.core.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/*
 * JWT工具包
 */
public class JWTUtils {

    /**
     * 通过PBKDF2生成256位密钥
     */
    private static SecretKey generateKeyFromPassword(String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 使用PBKDF2算法
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        // 密钥规格（密码、盐、迭代次数、长度）
        KeySpec spec = new PBEKeySpec(password.toCharArray(), "your-static-salt".getBytes(StandardCharsets.UTF_8), 10000, 256);
        // 生成密钥字节
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        // 转换为HMAC-SHA256专用密钥
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    /**
     * 生成token
     **/
    public static String generateToken(JWTPayload jwtPayload, String jwtSecret) {
        try {
            // 确保使用一个 256 位的密钥来生成 HS256
            SecretKey key = generateKeyFromPassword(jwtSecret);
            return Jwts.builder()
                    .setClaims(jwtPayload.toMap())
                    //过期时间 = 当前时间 + （设置过期时间[单位 ：s ] ）  token放置redis 过期时间无意义
                    //.setExpiration(new Date(System.currentTimeMillis() + (jwtExpiration * 1000) ))
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("token 生成失败", e);
        }

    }

    /**
     * 根据token与秘钥 解析token并转换为 JWTPayload
     **/
    public static JWTPayload parseToken(String token, String secret) {
        try {
            // 将字符串密钥转换为安全的 SecretKey 实例
            SecretKey key = generateKeyFromPassword(secret);

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
            throw new RuntimeException("token 解析失败", e);
        }
    }

}
