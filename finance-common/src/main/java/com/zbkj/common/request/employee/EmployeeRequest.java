package com.zbkj.common.request.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 员工请求类
 * </p>
 *
 * @author zbkj
 * @since 2024-12-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "EmployeeRequest对象", description = "员工请求类")
public class EmployeeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "员工ID，新增时为空")
    private Integer id;

    @ApiModelProperty(value = "养殖场ID")
    private Integer farmId;
    @ApiModelProperty(value = "员工姓名")
    @NotBlank(message = "员工姓名不能为空")
    private String name;
    @ApiModelProperty(value = "员工手机号")
    @NotBlank(message = "员工手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    @ApiModelProperty(value = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;
    @ApiModelProperty(value = "身份证号")
    @NotBlank(message = "身份证号不能为空")
    private String idCard;
} 