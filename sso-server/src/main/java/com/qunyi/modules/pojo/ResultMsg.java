package com.qunyi.modules.pojo;

public class ResultMsg {

  private  String  errcode;

  private  String errmsg;

  private  String type;

  private AccessToken accessToken;

  private String targetUrl;

    public ResultMsg() {
    }


    public ResultMsg(String errcode, String errmsg) {
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    public ResultMsg(String errcode, String errmsg,String targetUrl) {
        this.errcode = errcode;
        this.errmsg = errmsg;
        this.targetUrl = targetUrl;
    }


    public ResultMsg(String errcode, String errmsg, AccessToken accessToken) {
        this.errcode = errcode;
        this.errmsg = errmsg;
        this.accessToken = accessToken;
    }

    public ResultMsg(String errcode, String errmsg, AccessToken accessToken,String type) {
        this.errcode = errcode;
        this.errmsg = errmsg;
        this.accessToken = accessToken;
        this.type = type;

    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }


    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
}
