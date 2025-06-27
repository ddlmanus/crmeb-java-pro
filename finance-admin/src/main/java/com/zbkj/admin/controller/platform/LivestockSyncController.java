package com.zbkj.admin.controller.platform;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zbkj.common.model.finance.LivestockSyncConfig;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.finance.LivestockSyncService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 存栏数据同步配置管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/finance/livestockSync")
@Api(tags = "存栏数据同步配置管理")
public class LivestockSyncController {

    @Autowired
    private LivestockSyncService livestockSyncService;

    /**
     * 分页查询同步配置
     */
    @PostMapping("/page")
    @ApiOperation(value = "分页查询同步配置")
    @PreAuthorize("hasAuthority('admin:finance:livestockSync:page')")
    public CommonResult<IPage<LivestockSyncConfig>> page(@RequestParam(value = "farmCode", required = false) String farmCode,
                                                          @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(livestockSyncService.pageList(pageParamRequest, farmCode));
    }

    /**
     * 新增同步配置
     */
    @PostMapping("/add")
    @ApiOperation(value = "新增同步配置")
    @PreAuthorize("hasAuthority('admin:finance:livestockSync:add')")
    public CommonResult<Boolean> add(@RequestBody @Validated LivestockSyncConfig config) {
        return CommonResult.success(livestockSyncService.addConfig(config));
    }

    /**
     * 编辑同步配置
     */
    @PostMapping("/edit")
    @ApiOperation(value = "编辑同步配置")
    @PreAuthorize("hasAuthority('admin:finance:livestockSync:edit')")
    public CommonResult<Boolean> edit(@RequestBody @Validated LivestockSyncConfig config) {
        return CommonResult.success(livestockSyncService.updateConfig(config));
    }

    /**
     * 删除同步配置
     */
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除同步配置")
    @PreAuthorize("hasAuthority('admin:finance:livestockSync:delete')")
    public CommonResult<Boolean> delete(@PathVariable("id") Integer id) {
        return CommonResult.success(livestockSyncService.deleteConfig(id));
    }

    /**
     * 获取同步配置详情
     */
    @GetMapping("/detail/{id}")
    @ApiOperation(value = "获取同步配置详情")
    @PreAuthorize("hasAuthority('admin:finance:livestockSync:detail')")
    public CommonResult<LivestockSyncConfig> detail(@PathVariable("id") Integer id) {
        return CommonResult.success(livestockSyncService.getById(id));
    }

    /**
     * 启用/禁用同步配置
     */
    @PostMapping("/toggleSync/{id}")
    @ApiOperation(value = "启用/禁用同步配置")
    @PreAuthorize("hasAuthority('admin:finance:livestockSync:edit')")
    public CommonResult<Boolean> toggleSync(@PathVariable("id") Integer id, @RequestParam("enableSync") Integer enableSync) {
        return CommonResult.success(livestockSyncService.toggleSync(id, enableSync));
    }

    /**
     * 手动触发同步
     */
    @PostMapping("/manualSync/{id}")
    @ApiOperation(value = "手动触发同步")
    @PreAuthorize("hasAuthority('admin:finance:livestockSync:sync')")
    public CommonResult<Boolean> manualSync(@PathVariable("id") Integer id) {
        return CommonResult.success(livestockSyncService.manualSync(id));
    }

    /**
     * 获取所有启用的同步配置
     */
    @GetMapping("/enabledList")
    @ApiOperation(value = "获取所有启用的同步配置")
    @PreAuthorize("hasAuthority('admin:finance:livestockSync:list')")
    public CommonResult<List<LivestockSyncConfig>> enabledList() {
        return CommonResult.success(livestockSyncService.getEnabledConfigs());
    }

    /**
     * 立即执行所有启用的同步任务
     */
    @PostMapping("/syncAll")
    @ApiOperation(value = "立即执行所有启用的同步任务")
    @PreAuthorize("hasAuthority('admin:finance:livestockSync:syncAll')")
    public CommonResult<String> syncAll() {
        try {
            // 异步执行同步任务
            new Thread(() -> {
                livestockSyncService.syncAllEnabledFarms();
            }).start();
            return CommonResult.success("同步任务已启动，请稍后查看同步状态");
        } catch (Exception e) {
            log.error("启动同步任务失败", e);
            return CommonResult.failed("启动同步任务失败: " + e.getMessage());
        }
    }
} 