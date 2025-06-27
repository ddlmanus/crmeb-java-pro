package com.zbkj.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ThirdLogin implements Serializable {
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "用户手机号")
    private String mobile;
    @ApiModelProperty(value = "用户名称")
    private String realName;
    @ApiModelProperty(value = "性别")
    private String gender;
    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "是否拥有店铺")
    private String haveStore;

    @ApiModelProperty(value = "机构")
    private String orgName;
    @ApiModelProperty(value = "机构分类编码")
     private String orgCategory;
    @ApiModelProperty(value = "机构编码")
    private String orgCode;
    @ApiModelProperty(value = "部门")
    private String deptName;
    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "商品名称")
    private String keyword;
    @ApiModelProperty(value = "角色")
    private String roles;
}
