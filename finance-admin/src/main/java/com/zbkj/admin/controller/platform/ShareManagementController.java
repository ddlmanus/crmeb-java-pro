package com.zbkj.admin.controller.platform;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.ShareChangeRecord;
import com.zbkj.common.model.finance.ShareManagement;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.ShareManagementPageVO;
import com.zbkj.common.vo.finance.ShareStatisticsVO;
import com.zbkj.common.vo.finance.ShareUpdateRequest;
import com.zbkj.service.service.finance.ShareChangeRecordService;
import com.zbkj.service.service.finance.ShareManagementService;
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
 * 平台端股份管理控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/finance/share")
@Api(tags = "平台端股份管理")
public class ShareManagementController {

    @Autowired
    private ShareManagementService shareManagementService;

    @Autowired
    private ShareChangeRecordService shareChangeRecordService;

    /**
     * 分页查询股份管理列表
     */
  //  @PreAuthorize("hasAuthority('platform:finance:share:page')")
    @ApiOperation(value = "分页查询股份管理列表")
    @GetMapping("/page")
    public CommonResult<CommonPage<ShareManagement>> getSharePage(
            @ModelAttribute @Validated ShareManagementPageVO request,
            @ModelAttribute PageParamRequest pageParamRequest) {
        List<ShareManagement> list = shareManagementService.getAdminList(request);
        return CommonResult.success(CommonPage.restPage(list));
    }

    /**
     * 获取股份详情
     */
  //  @PreAuthorize("hasAuthority('platform:finance:share:detail')")
    @ApiOperation(value = "获取股份详情")
    @GetMapping("/detail/{id}")
    public CommonResult<ShareManagement> getShareDetail(@PathVariable String id) {
        ShareManagement shareManagement = shareManagementService.getById(id);
        return CommonResult.success(shareManagement);
    }

    /**
     * 新增/更新股份
     */
   // @PreAuthorize("hasAuthority('platform:finance:share:save')")
    @ApiOperation(value = "新增/更新股份")
    @PostMapping("/save")
    public CommonResult<Boolean> saveShare(@RequestBody @Validated ShareUpdateRequest request) {
        Boolean result = shareManagementService.addShare(request);
        return CommonResult.success(result);
    }

    /**
     * 删除股份
     */
   // @PreAuthorize("hasAuthority('platform:finance:share:delete')")
    @ApiOperation(value = "删除股份")
    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> deleteShare(@PathVariable String id) {
        Boolean result = shareManagementService.removeById(id);
        return CommonResult.success(result);
    }

    /**
     * 分页查询股份变更记录
     */
   // @PreAuthorize("hasAuthority('platform:finance:share:change:record:page')")
    @ApiOperation(value = "分页查询股份变更记录")
    @GetMapping("/change/record/page")
    public CommonResult<CommonPage<ShareChangeRecord>> getShareChangeRecordPage(
            @ApiParam(value = "关键词", required = false) @RequestParam(required = false) String keywords,
            @ModelAttribute PageParamRequest pageParamRequest) {
        PageInfo<ShareChangeRecord> page = shareChangeRecordService.getShareChangeRecordPage(pageParamRequest, keywords);
        return CommonResult.success(CommonPage.restPage(page));
    }

    /**
     * 根据用户ID获取股份变更记录
     */
  //  @PreAuthorize("hasAuthority('platform:finance:share:change:record:user')")
    @ApiOperation(value = "根据用户ID获取股份变更记录")
    @GetMapping("/change/record/user/{userId}")
    public CommonResult<List<ShareChangeRecord>> getShareChangeRecordByUserId(@PathVariable Integer userId) {
        List<ShareChangeRecord> list = shareChangeRecordService.getByUserId(userId);
        return CommonResult.success(list);
    }

    /**
     * 获取股份统计信息
     */
 //   @PreAuthorize("hasAuthority('platform:finance:share:statistics')")
    @ApiOperation(value = "获取股份统计信息")
    @GetMapping("/statistics")
    public CommonResult<ShareStatisticsVO> getShareStatistics() {
        // TODO: 实现股份统计逻辑
        ShareStatisticsVO statistics = new ShareStatisticsVO();
        return CommonResult.success(statistics);
    }

    /**
     * 导出股份数据
     */
 //   @PreAuthorize("hasAuthority('platform:finance:share:export')")
    @ApiOperation(value = "导出股份数据")
    @PostMapping("/export")
    public void exportShareData(@RequestBody @Validated ShareManagementPageVO request, HttpServletResponse response) {
        try {
            shareManagementService.exportShareData(request, response);
        } catch (Exception e) {
            log.error("导出股份数据失败", e);
        }
    }

    /**
     * 导出股份变更记录数据
     */
   // @PreAuthorize("hasAuthority('platform:finance:share:change:record:export')")
    @ApiOperation(value = "导出股份变更记录数据")
    @PostMapping("/change/record/export")
    public void exportShareChangeRecordData(
            @ApiParam(value = "关键词", required = false) @RequestParam(required = false) String keywords,
            HttpServletResponse response) {
        try {
            shareChangeRecordService.exportShareChangeRecordData(keywords, response);
        } catch (Exception e) {
            log.error("导出股份变更记录数据失败", e);
        }
    }
} 