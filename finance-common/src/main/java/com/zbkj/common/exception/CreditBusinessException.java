package com.zbkj.common.exception;

/**
 * 授信业务异常
 */
public class CreditBusinessException extends RuntimeException {

    private String errorCode;
    
    public CreditBusinessException(String message) {
        super(message);
    }
    
    public CreditBusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public CreditBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public CreditBusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
} 