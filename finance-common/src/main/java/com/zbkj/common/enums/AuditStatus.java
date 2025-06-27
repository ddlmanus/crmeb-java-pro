package com.zbkj.common.enums;

public enum AuditStatus {
    PENDING(0, "待审核"),
    PASS(2, "审核通过"),
    REJECT(1, "审核拒绝");

    private Integer code;
    private String desc;

    AuditStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }
}
