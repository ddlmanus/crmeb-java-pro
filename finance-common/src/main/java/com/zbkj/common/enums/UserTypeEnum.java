package com.zbkj.common.enums;

public enum UserTypeEnum {
    //用户类型：0:游客，1:管理员,2,员工
    USER_TYPE_GUEST(0, "游客"),
    USER_TYPE_ADMIN(1, "管理员"),
    USER_TYPE_EMPLOYEE(2, "员工");

    private Integer code;
    private String desc;

    UserTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
