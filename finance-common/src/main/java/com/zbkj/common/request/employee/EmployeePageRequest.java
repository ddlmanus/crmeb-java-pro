package com.zbkj.common.request.employee;

import com.zbkj.common.request.PageParamRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 员工分页查询请求类
 * </p>
 *
 * @author zbkj
 * @since 2024-12-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "EmployeePageRequest对象", description = "员工分页查询请求类")
public class EmployeePageRequest extends PageParamRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "关键词搜索：员工姓名、手机号、工号")
    private String keywords;

    @ApiModelProperty(value = "员工状态：0离职，1在职，2试用期")
    private Integer status;

    @ApiModelProperty(value = "部门")
    private String department;

    @ApiModelProperty(value = "职位")
    private String position;

    @ApiModelProperty(value = "性别：0未知，1男，2女")
    private Integer gender;

    @ApiModelProperty(value = "养殖类型：0:游客，1:种植户/种植企业，2:合作社，3:经纪人，4:供应商，5:平台")
    private Integer farmType;

    @ApiModelProperty(value = "养殖场名称")
    private String farmName;

    @ApiModelProperty(value = "企业名称")
    private String companyName;

    @ApiModelProperty(value = "养殖品种")
    private String breedingVariety;
} 