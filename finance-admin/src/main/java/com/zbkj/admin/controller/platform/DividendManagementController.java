package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.finance.DividendDetail;
import com.zbkj.common.model.finance.DividendManagement;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.DividendCreateRequest;
import com.zbkj.common.vo.finance.DividendManagementPageVO;
import com.zbkj.common.vo.finance.DividendManagementVO;
import com.zbkj.common.vo.finance.DividendStatisticsVO;
import com.zbkj.service.service.finance.DividendDetailService;
import com.zbkj.service.service.finance.DividendManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 平台端分红管理控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/finance/dividend")
@Api(tags = "平台端分红管理")
public class DividendManagementController {

    @Autowired
    private DividendManagementService dividendManagementService;

    @Autowired
    private DividendDetailService dividendDetailService;

    /**
     * 分页查询分红管理列表
     */
   // @PreAuthorize("hasAuthority('platform:finance:dividend:page')")
    @ApiOperation(value = "分页查询分红管理列表")
    @GetMapping("/page")
    public CommonResult<CommonPage<DividendManagementVO>> getDividendPage(
            @ModelAttribute @Validated DividendManagementPageVO request) {
        List<DividendManagementVO> list = dividendManagementService.dividendAdminList(request);
        return CommonResult.success(CommonPage.restPage(list));
    }

    /**
     * 获取分红详情
     */
 //   @PreAuthorize("hasAuthority('platform:finance:dividend:detail')")
    @ApiOperation(value = "获取分红详情")
    @GetMapping("/detail/{id}")
    public CommonResult<DividendManagement> getDividendDetail(@PathVariable String id) {
        DividendManagement dividendManagement = dividendManagementService.getById(id);
        return CommonResult.success(dividendManagement);
    }

    /**
     * 新增分红
     */
  //  @PreAuthorize("hasAuthority('platform:finance:dividend:add')")
    @ApiOperation(value = "新增分红")
    @PostMapping("/add")
    public CommonResult<Boolean> addDividend(@RequestBody @Validated DividendCreateRequest request) {
        Boolean result = dividendManagementService.addDividend(request);
        return CommonResult.success(result);
    }

    /**
     * 编辑分红
     */
 //   @PreAuthorize("hasAuthority('platform:finance:dividend:edit')")
    @ApiOperation(value = "编辑分红")
    @PutMapping("/edit")
    public CommonResult<Boolean> editDividend(@RequestBody @Validated DividendManagement request) {
        Boolean result = dividendManagementService.updateById(request);
        return CommonResult.success(result);
    }

    /**
     * 删除分红
     */
  //  @PreAuthorize("hasAuthority('platform:finance:dividend:delete')")
    @ApiOperation(value = "删除分红")
    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> deleteDividend(@PathVariable String id) {
        Boolean result = dividendManagementService.removeById(id);
        return CommonResult.success(result);
    }

    /**
     * 发布分红
     */
   // @PreAuthorize("hasAuthority('platform:finance:dividend:publish')")
    @ApiOperation(value = "发布分红")
    @PostMapping("/publish/{id}")
    public CommonResult<Boolean> publishDividend(@PathVariable String id) {
        DividendManagement dividend = dividendManagementService.getById(id);
        if (dividend != null) {
            dividend.setStatus(1); // 1:已发布
            Boolean result = dividendManagementService.updateById(dividend);
            return CommonResult.success(result);
        }
        return CommonResult.failed("分红记录不存在");
    }

    /**
     * 完成分红
     */
   // @PreAuthorize("hasAuthority('platform:finance:dividend:complete')")
    @ApiOperation(value = "完成分红")
    @PostMapping("/complete/{id}")
    public CommonResult<Boolean> completeDividend(@PathVariable String id) {
        DividendManagement dividend = dividendManagementService.getById(id);
        if (dividend != null) {
            dividend.setStatus(2); // 2:已完成
            Boolean result = dividendManagementService.updateById(dividend);
            return CommonResult.success(result);
        }
        return CommonResult.failed("分红记录不存在");
    }

    /**
     * 获取分红明细列表
     */
   // @PreAuthorize("hasAuthority('platform:finance:dividend:detail:list')")
    @ApiOperation(value = "获取分红明细列表")
    @GetMapping("/detail/list/{dividendId}")
    public CommonResult<List<DividendDetail>> getDividendDetailList(@PathVariable String dividendId) {
        List<DividendDetail> list = dividendDetailService.getByDividendId(dividendId);
        return CommonResult.success(list);
    }

    /**
     * 根据用户ID获取分红明细
     */
  //  @PreAuthorize("hasAuthority('platform:finance:dividend:detail:user')")
    @ApiOperation(value = "根据用户ID获取分红明细")
    @GetMapping("/detail/user/{userId}")
    public CommonResult<List<DividendDetail>> getDividendDetailByUserId(@PathVariable Integer userId) {
        List<DividendDetail> list = dividendDetailService.getByUserId(userId);
        return CommonResult.success(list);
    }

    /**
     * 获取分红统计信息
     */
   // @PreAuthorize("hasAuthority('platform:finance:dividend:statistics')")
    @ApiOperation(value = "获取分红统计信息")
    @GetMapping("/statistics")
    public CommonResult<DividendStatisticsVO> getDividendStatistics(
            @ApiParam(value = "开始时间", required = false) @RequestParam(required = false) String startTime,
            @ApiParam(value = "结束时间", required = false) @RequestParam(required = false) String endTime) {
        // TODO: 实现分红统计逻辑
        DividendStatisticsVO statistics = new DividendStatisticsVO();
        return CommonResult.success(statistics);
    }

    /**
     * 导出分红数据
     */
    //@PreAuthorize("hasAuthority('platform:finance:dividend:export')")
    @ApiOperation(value = "导出分红数据")
    @PostMapping("/export")
    public void exportDividendData(@RequestBody @Validated DividendManagementPageVO request, HttpServletResponse response) {
        try {
            dividendManagementService.exportDividendData(request, response);
        } catch (Exception e) {
            log.error("导出分红数据失败", e);
        }
    }

    /**
     * 导出分红明细数据
     */
   // @PreAuthorize("hasAuthority('platform:finance:dividend:detail:export')")
    @ApiOperation(value = "导出分红明细数据")
    @PostMapping("/detail/export")
    public void exportDividendDetailData(
            @ApiParam(value = "分红ID", required = false) @RequestParam(required = false) String dividendId,
            HttpServletResponse response) {
        try {
            dividendDetailService.exportDividendDetailData(dividendId, response);
        } catch (Exception e) {
            log.error("导出分红明细数据失败", e);
        }
    }

    /**
     * 批量操作
     */
  //  @PreAuthorize("hasAuthority('platform:finance:dividend:batch')")
    @ApiOperation(value = "批量操作")
    @PostMapping("/batch")
    public CommonResult<Boolean> batchOperation(
            @ApiParam(value = "ID列表", required = true) @RequestParam String ids,
            @ApiParam(value = "操作类型", required = true) @RequestParam String action) {
        // TODO: 实现批量操作逻辑
        return CommonResult.success(true);
    }
} 