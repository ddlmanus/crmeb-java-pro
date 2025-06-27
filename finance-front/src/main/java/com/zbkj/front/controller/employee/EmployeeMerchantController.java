package com.zbkj.front.controller.employee;

import com.zbkj.common.result.CommonResult;
import com.zbkj.common.token.FrontTokenComponent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: 大粽子
 * @Date: 2024/5/29 11:14
 * @Description: 描述对应的业务场景
 */
@Slf4j
@RestController
@RequestMapping("api/front/employee/merchant")
@Api(tags = "移动端商家管理 - 商户基础操作") //配合swagger使用
public class EmployeeMerchantController {

    @Autowired
    private FrontTokenComponent tokenComponent;

    @ApiOperation(value = "激活当前正在操作的商户")
    @RequestMapping(value = "/active/{id}", method = RequestMethod.GET)
    public CommonResult<String> activeMerchant(@PathVariable(value = "id") Integer id) {
        tokenComponent.activeMerchantEmployee(id);
        return CommonResult.success();
    }
}
