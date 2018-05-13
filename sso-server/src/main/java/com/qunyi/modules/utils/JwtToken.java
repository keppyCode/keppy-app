package com.qunyi.modules.utils;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT生成token校验工具类
 * @author huangyufei
 * @Description
 * @date 2018-04-03  15:57
 */
public class JwtToken {
    //加密秘钥，此处使用base64转码
    private static final String SECRET = "[B@73a28541";  //使用base64转码秘钥Base64Util.decode("mpmdPlatForm")

    // jwt的过期时间，这个过期时间必须要大于签发时间
    private static final String EXP = "exp";

    //一般添加用户的相关信息或其他业务需要的必要信息.但不建议添加敏感信息，因为该部分在客户端可解密
    private static final String PAYLOAD = "payload";

    //jti: jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。
    private  static final String JTI="jti";

    /**
     * 加密生成token
     * @param object 用户对象
     * @param maxAge 设置过期时间
     * @return the jwt token
     */
    public static <T> String sign(T object, long maxAge) {
        try {
            final JWTSigner signer = new JWTSigner(SECRET);
            final Map<String, Object> claims = new HashMap<String, Object>();
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(object);
            claims.put(PAYLOAD, jsonString);
            claims.put(EXP, System.currentTimeMillis() + maxAge);
            return signer.sign(claims);
        } catch(Exception e) {
            return null;
        }
    }


    /**
     * 解析token
     * @param jwt 加密后的token
     * @return 对象
     */
    public static<T> T unsign(String jwt, Class<T> classT) {
        final JWTVerifier verifier = new JWTVerifier(SECRET);
        try {
            final Map<String,Object> claims= verifier.verify(jwt);
            if (claims.containsKey(EXP) && claims.containsKey(PAYLOAD)) {
                long exp = (Long)claims.get(EXP);
                long currentTimeMillis = System.currentTimeMillis();
                if (exp > currentTimeMillis) {
                    String json = (String)claims.get(PAYLOAD);
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readValue(json, classT);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        Map<String,String> map=new HashMap<>();
        map.put("name","fei");
        map.put("pwd","111");
        String s= sign(map,30L * 24L * 3600L * 1000L);
        System.out.println("生成加密token--------->>"+s);

        System.out.println("解密token:");
        map=unsign(s,map.getClass());
        System.out.println(map.get("name"));
        System.out.println(map.get("pwd"));
    }


}
