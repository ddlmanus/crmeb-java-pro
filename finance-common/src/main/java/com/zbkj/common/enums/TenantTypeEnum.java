package com.zbkj.common.enums;

public enum TenantTypeEnum {
    //租户类型：0:游客，1:种植户/种植企业，2:合作社，3:经纪人，4:供应商，5:平台
    TENANT_TYPE_GUEST(0, "游客"),
    TENANT_TYPE_PLANTING_HOUSE(1, "种植户/种植企业"),
    TENANT_TYPE_COOPERATIVE(2, "合作社"),
    TENANT_TYPE_BROKER(3, "经纪人"),
    TENANT_TYPE_SUPPLIER(4, "供应商"),
    TENANT_TYPE_PLATFORM(5, "平台");

    private Integer code;
    private String desc;
    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    private TenantTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
