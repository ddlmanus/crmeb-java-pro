package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 股份更新请求VO
 */
@Data
@ApiModel(value = "ShareUpdateRequest", description = "股份更新请求")
public class ShareUpdateRequest {

    private List<ShareManagementVO> items;
} 