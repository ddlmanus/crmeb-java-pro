package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 养殖场机构信息更新请求VO
 */
@Data
@ApiModel(value = "FarmInstitutionUpdateRequest", description = "养殖场机构信息更新请求")
public class FarmInstitutionUpdateRequest {

    @ApiModelProperty(value = "机构ID", required = true)
    @NotBlank(message = "机构ID不能为空")
    private String id;

    @ApiModelProperty(value = "机构类型")
    private String institutionType;

    @ApiModelProperty(value = "机构标识代码", required = true)
    @NotBlank(message = "机构标识代码不能为空")
    private String farmCode;

    @ApiModelProperty(value = "养殖场名称", required = true)
    @NotBlank(message = "养殖场名称不能为空")
    private String farmName;

    @ApiModelProperty(value = "养殖场负责人电话")
    private String managerPhone;

    @ApiModelProperty(value = "养殖场负责人姓名")
    private String managerName;
} 