package com.xmy.entity;

import java.io.Serializable;

public class Result implements Serializable {
    private Boolean success;
    private String message;
    private Integer code;

    public Result() {
    }

    public Result(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Result(Boolean success, Integer code, String message) {
        this.success = success;
        this.message = message;
        this.code = code;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Result{" + "success=" + success + ", code=" + code + ", message='" + message + '\'' + '}';
    }
}
