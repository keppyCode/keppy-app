package com.qunyi.modules.pojo;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Component;

@Component(value = "audienceEntity")
public class Audience {

    private  String clientId = "123";

    private  String Base64Secret ="[B@73a28541";

    private  String name="liuqiuping";

    private  Integer expiresSecond=1800;//(单位秒)

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getBase64Secret() {
        return Base64Secret;
    }

    public void setBase64Secret(String base64Secret) {
        Base64Secret = base64Secret;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getExpiresSecond() {
        return expiresSecond;
    }

    public void setExpiresSecond(Integer expiresSecond) {
        this.expiresSecond = expiresSecond;
    }
}
