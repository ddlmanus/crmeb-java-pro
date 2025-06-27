package com.zbkj.common.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class FileReq {

    private String fileName;
    private String fileUrl;
    private String fileType;
    private String fileData;
    @ApiModelProperty(value = "模块 用户user,商品product,微信wechat,news文章", required = true)
    @NotBlank(message = "model不能为空")
    private String model;

    @ApiModelProperty(value = "分类ID 0编辑器,1商品图片,2拼团图片,3砍价图片,4秒杀图片,5文章图片,6组合数据图,7前台用户,8微信系列", required = true)
    @NotNull(message = "请选择图片分类")
    private Integer pid;

}