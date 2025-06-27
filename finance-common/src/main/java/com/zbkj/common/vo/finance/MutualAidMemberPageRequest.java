package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "社员互助金分页查询请求", description = "社员互助金分页查询请求")
public class MutualAidMemberPageRequest {

    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNo = 1;

    @ApiModelProperty(value = "每页数量", required = true)
    private Integer pageSize = 20;

    @ApiModelProperty(value = "社员名称")
    private String memberName;

    @ApiModelProperty(value = "社员账号")
    private String memberAccount;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    @ApiModelProperty(value = "状态")
    private Integer repaymentStatus;
} 