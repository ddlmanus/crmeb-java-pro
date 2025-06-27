package com.zbkj.common.response.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 养殖机构简单响应类
 */
@Data
@ApiModel(value = "FarmInstitutionSimpleResponse", description = "养殖机构简单响应")
public class FarmInstitutionSimpleResponse {

    @ApiModelProperty(value = "机构ID")
    private Integer id;

    @ApiModelProperty(value = "机构名称")
    private String name;

    public FarmInstitutionSimpleResponse() {}

    public FarmInstitutionSimpleResponse(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
} 