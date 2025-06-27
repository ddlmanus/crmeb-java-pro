package com.zbkj.front.controller.finance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zbkj.common.model.finance.*;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.*;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.finance.DividendManagementService;
import com.zbkj.service.service.finance.ShareChangeRecordService;
import com.zbkj.service.service.finance.ShareManagementService;
import com.zbkj.service.service.finance.DividendDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社员股份管理接口
 */
@RestController
@Api(tags = "社员股份管理接口")
@RequestMapping("/api/front/finance/share")
public class BuyerShareManagementController {

    @Autowired
    private ShareManagementService shareManagementService;

    @Autowired
    private DividendManagementService dividendManagementService;

    @Autowired
    private ShareChangeRecordService shareChangeRecordService;

    @ApiOperation(value = "股份变更")
    @PostMapping("/change")
    public CommonResult<Boolean> change(@RequestBody @Validated ShareUpdateRequest request) {
       return  CommonResult.success(shareManagementService.addShare(request));
    }
    /**
     * 股份你变更列表
     */
    @ApiOperation(value = "股份变更列表")
    @GetMapping("/change/list")
    public CommonResult<List<ShareManagement>> changeList(@Validated ShareManagementPageVO pageParamRequest) {
        return CommonResult.success(shareManagementService.changeList(pageParamRequest));
    }
    /**
     * 变更日期分页查询
     */
    @ApiOperation(value = "变更日期分页查询")
    @GetMapping("/change/date/list")
    public CommonResult<CommonPage<ShareManagementDate>> changeDateList(@Validated ShareManagementDatePageVO pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(shareManagementService.changeDateList(pageParamRequest)));
    }

    /**
     * 根据股份变更日期查看股份变更
     */
    @ApiOperation(value = "根据股份变更日期查看股份变更")
    @GetMapping("/change/date/detail/{id}")
    public CommonResult<List<ShareManagement>> changeDateDetail(@Validated @PathVariable("id") String id) {
        return CommonResult.success(shareManagementService.changeDateDetail(id));
    }
    /**
     * 根据股份变更ID查询变更记录
     */
    @ApiOperation(value = "根据股份变更ID查询变更记录")
    @GetMapping("/change/record/list/{id}")
    public CommonResult<List<ShareChangeRecord>> changeRecordList(@Validated @PathVariable("id") String id) {
        return CommonResult.success(shareManagementService.changeRecordList(id));
    }

    /**
     * 新增分红
     */
    @ApiOperation(value = "新增分红")
    @PostMapping("/dividend/add")
    public CommonResult<Boolean> dividendAdd(@RequestBody @Validated DividendCreateRequest request) {
        return CommonResult.success(dividendManagementService.addDividend(request));
    }
    /**
     * 分红列表
     */
    @ApiOperation(value = "分红列表")
    @GetMapping("/dividend/list")
    public CommonResult<List<DividendManagementVO>> dividendList(@Validated DividendManagementPageVO pageParamRequest) {
        return CommonResult.success(dividendManagementService.dividendList(pageParamRequest));
    }

    /**
     * 获取社员股份变更记录
     */
    @ApiOperation(value = "获取当前登录人股份变更记录")
    @GetMapping("/share/change/list")
    public CommonResult<List<ShareChangeRecord>> shareChangeList() {
        return CommonResult.success(shareChangeRecordService.shareChangeList());
    }
} 