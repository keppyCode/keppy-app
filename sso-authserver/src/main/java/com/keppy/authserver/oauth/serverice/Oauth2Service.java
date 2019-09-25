package com.keppy.authserver.oauth.serverice;

import com.keppy.authserver.oauth.dao.Oauth2Dao;
import com.keppy.authserver.oauth.model.Oauth2Client;
import com.keppy.authserver.oauth.model.Oauth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class Oauth2Service {
    private final Oauth2Dao oauth2Dao;

    @Autowired
    public Oauth2Service(Oauth2Dao oauth2Dao) {
        this.oauth2Dao = oauth2Dao;
    }

    public List<Oauth2Client> getOauth2ClientByClientId(String clientId) {
        return oauth2Dao.getOauth2ClientByClientId(clientId);
    }

    public List<Oauth2User> getOauth2UserByUsername(String username) {
        return oauth2Dao.getOauth2UserByUsername(username);
    }
}
