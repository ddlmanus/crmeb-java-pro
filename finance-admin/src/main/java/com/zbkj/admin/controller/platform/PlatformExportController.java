package com.zbkj.admin.controller.platform;

import cn.hutool.core.collection.CollUtil;
import com.zbkj.common.request.OrderSearchRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ExportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


/**
 *  平台端导出控制器
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/export")
@Api(tags = "平台端导出控制器")
public class PlatformExportController {

    @Autowired
    private ExportService exportService;

    @PreAuthorize("hasAuthority('platform:export:order')")
    @ApiOperation(value = "订单导出")
    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public void exportOrder(OrderSearchRequest request, HttpServletResponse response) {
        exportService.exportOrder(request, response);
    }

}



