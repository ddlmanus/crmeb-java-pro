package com.zbkj.front.controller.finance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zbkj.common.model.finance.ActiveTransaction;
import com.zbkj.common.model.finance.FarmBreedType;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.*;
import com.zbkj.service.service.finance.ActiveTransactionService;
import com.zbkj.service.service.finance.FarmBreedTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 活体交易记录管理接口
 */
@RestController
@Api(tags = "管理端-活体交易记录接口")
@RequestMapping("/api/front/finance/activeTransaction")
public class ActiveTransactionController {

    @Autowired
    private ActiveTransactionService activeTransactionService;
    /**
     * 分页查询活体交易记录
     */
    @PostMapping("/page")
    @ApiOperation(value = "分页查询活体交易记录")
    public CommonResult<CommonPage<ActiveTransaction>> page(@RequestBody ActiveTransactionPageVO pageVO) {
        return CommonResult.success(CommonPage.restPage(activeTransactionService.pageActiveTransaction(pageVO)));
    }

    /**
     * 获取活体交易记录详情
     */
    @GetMapping("/get/{id}")
    @ApiOperation(value = "获取活体交易记录详情")
    public CommonResult<ActiveTransactionVO> get(@PathVariable String id) {
        return CommonResult.success(activeTransactionService.getActiveTransactionDetail(id));
    }

    /**
     * 新增活体交易记录
     */
    @PostMapping("/add")
    @ApiOperation(value = "新增活体交易记录")
    public CommonResult<String> add(@RequestBody ActiveTransactionRequestVO requestVO) {
        return activeTransactionService.add(requestVO) ? CommonResult.success("新增成功") : CommonResult.failed("新增失败");
    }

    /**
     * 获取养殖品种类型
     */
    @GetMapping("/getBreedingType")
    @ApiOperation(value = "获取养殖品种类型")
    public CommonResult<List<FarmBreedType>> getBreedingType() {

        return CommonResult.success(activeTransactionService.getBreedingType());
    }
} 