package com.qunyi.modules.utils;

/**
 * 基础的UI视图传输DTO类
 */
public class BaseViewVO {


    /**
     * 操作是否成功和操作后的消息，如：保存成功失败等
     */
    private boolean success = false;
    private String message;

    /**
     * 数据对象，操作完成后的数据对象
     */
    private Object dataObj;

    public BaseViewVO() {
        super();
    }

    public BaseViewVO(boolean success, String message) {
        super();
        this.success = success;
        this.message = message;
    }

    public BaseViewVO(boolean success, String message, Object dataObj) {
        super();
        this.success = success;
        this.message = message;
        this.dataObj = dataObj;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getDataObj() {
        return dataObj;
    }

    public void setDataObj(Object dataObj) {
        this.dataObj = dataObj;
    }

    public void setSucceedMessage(String message) {
        this.success = Boolean.TRUE;
        this.message = message;
    }

    public void setSucceedMessage(String message, Object dataObj) {
        this.success = Boolean.FALSE;
        this.message = message;
        this.dataObj = dataObj;
    }

    public void setFailedMessage(String message) {
        this.success = Boolean.FALSE;
        this.message = message;
    }

    public void setFailedMessage(String message, Object dataObj) {
        this.success = Boolean.FALSE;
        this.message = message;
        this.dataObj = dataObj;
    }



}
