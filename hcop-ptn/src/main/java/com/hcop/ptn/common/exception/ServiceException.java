package com.hcop.ptn.common.exception;

import com.hcop.ptn.restful.model.ErrorInfo;

public class ServiceException extends Exception {
    private static final String CODE_REGEX = "\\d+(\\.\\d+)?";
    protected int code;

    public ServiceException() {}

    public ServiceException(String msg) {
        super(msg);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(int errorCode, String msg) {
        super(msg);
        this.code = errorCode;
    }

    public ServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ServiceException(ErrorInfo info){
        super(info.getMessage());

        if(info.getCode() != null && info.getCode().matches(CODE_REGEX)) {
            code = Integer.parseInt(info.getCode());
        } else {
            code = 500;
        }
    }

    public ServiceException(int errorCode, Throwable cause) {
        super(cause);
        this.code = errorCode;
    }

    public ServiceException(int errorCode, String msg, Throwable cause) {
        super(msg, cause);
        this.code = errorCode;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int errorCode) {
        this.code = errorCode;
    }

    @Override
    public String toString() {
        if (getCause() == null || getCause() == this) {
            return super.toString();
        } else if (code > 0) {
            return super.toString() + "[error code: " + code + "]";
        } else {
            return super.toString() + "[exception: " + getCause() + "]";
        }
    }

}
