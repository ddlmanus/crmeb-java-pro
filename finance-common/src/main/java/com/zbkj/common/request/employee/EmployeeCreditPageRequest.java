package com.zbkj.common.request.employee;

import com.zbkj.common.request.PageParamRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 员工授信分页查询请求
 * </p>
 *
 * @author zbkj
 * @since 2024-12-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "EmployeeCreditPageRequest对象", description = "员工授信分页查询请求")
public class EmployeeCreditPageRequest extends PageParamRequest {

    @ApiModelProperty(value = "员工姓名")
    private String name;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "养殖机构ID")
    private Integer farmInstitutionId;

    @ApiModelProperty(value = "授信状态：0未授信，1已授信")
    private String creditStatus;
} 