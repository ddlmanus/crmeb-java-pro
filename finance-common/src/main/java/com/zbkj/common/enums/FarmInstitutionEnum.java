package com.zbkj.common.enums;

public enum FarmInstitutionEnum {
    //养殖场 01030101
    FARM(1, "养殖场", "01030101"),
    // 动物交易市场 01030102
    MARKET(2, "动物交易市场", "01030102"),
    // 屠宰场 01030103
    SLaughterhouse(3, "屠宰场", "01030103"),
    // 动物产品交易市场 01030104
    PRODUCT_MARKET(4, "动物产品交易市场", "01030104"),
    // 动物隔离场 01030105
    ISOLATION_HOUSE(5, "动物隔离场", "01030105"),
    // 无害化处理场 01030106
    SAFE_PROCESSING_HOUSE(6, "无害化处理场", "01030106"),
    // 兽药生产企业 01030107
    PET_DRUG_PRODUCTION_ENTERPRISE(7, "兽药生产企业", "01030107"),
    // 兽药经营企业 01030108
    PET_DRUG_OPERATING_ENTERPRISE(8, "兽药经营企业", "01030108"),
    // 饲料生产企业 01030109
    FEED_PRODUCTION_ENTERPRISE(9, "饲料生产企业", "01030109"),
    // 生物安全实验室 01030110
    BIOMEDICAL_SAFETY_LABORATORY(10, "生物安全实验室", "01030110"),
    // 动物诊疗机构 01030111
    PET_DIAGNOSTIC_INSTITUTION(11, "动物诊疗机构", "01030111"),
    // 动物及动物产品经纪人 01030112
    PET_PRODUCT_BROKER(12, "动物及动物产品经纪人", "01030112"),
    //养殖合作社 01030113
    COOPERATIVE(13, "养殖合作社", "01030113");
    private Integer code;
    private String desc;
    private String farmCode;
    public static final int DEFAULT_BATCH_SIZE
            = 100;
    FarmInstitutionEnum(Integer code, String desc, String farmCode) {
        this.code = code;
        this.desc = desc;
        this.farmCode = farmCode;
    }
    public Integer getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }
    public String getFarmCode() {
        return farmCode;
    }
    public static FarmInstitutionEnum getByCode(Integer code) {
        for (FarmInstitutionEnum value : FarmInstitutionEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }


}
