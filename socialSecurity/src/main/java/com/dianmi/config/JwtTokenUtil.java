package com.dianmi.config;

import com.dianmi.common.Common;
import com.dianmi.model.User;
import com.dianmi.utils.JacksonUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by www on 2017/7/14.
 */
@Component
public class JwtTokenUtil {
    private static final String CLAIM_KEY_USER = "userJson";
    private static final String CLAIM_KEY_Time = "time";

    //从token中获取对象
    public User getUserFromToken(String token) {
        User user = null;
        try {
            Claims claims = Jwts.parser().setSigningKey(Common.JWT_SECRET).parseClaimsJws(token).getBody();
            if ((new Date((Long) claims.get(CLAIM_KEY_Time))).before(new Date())) {
                user = JacksonUtil.json2Pojo((String) claims.get(CLAIM_KEY_USER), User.class);
            }
        } catch (Exception ignored) {
        }
        return user;
    }

    //生成token
    public String generateToken(String userJson) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USER, userJson);
        claims.put(CLAIM_KEY_Time, new Date());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, Common.JWT_SECRET)
                .compact();
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + Common.JWT_EXPIRATION * 1000);
    }

}
