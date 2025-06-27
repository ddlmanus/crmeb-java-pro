package com.zbkj.admin.controller.platform;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.CreditTransactionPageVO;
import com.zbkj.common.vo.finance.CreditTransactionVO;
import com.zbkj.service.service.finance.CreditTransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台管理 - 授信交易记录控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/credit/transaction")
@Api(tags = "后台管理 - 授信交易记录")
public class CreditTransactionController {

    @Autowired
    private CreditTransactionService creditTransactionService;

    @PreAuthorize("hasAuthority('platform:credit:transaction:list')")
    @ApiOperation(value = "授信交易记录分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public CommonResult<CommonPage<CreditTransactionVO>> list(@RequestBody @Valid CreditTransactionPageVO pageVO) {
        PageInfo<CreditTransactionVO> page = creditTransactionService.pageTransaction(pageVO);
        return CommonResult.success(CommonPage.restPage( page));
    }

    @PreAuthorize("hasAuthority('platform:credit:transaction:detail')")
    @ApiOperation(value = "授信交易记录详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<CreditTransactionVO> detail(@PathVariable String id) {
        CreditTransactionVO transaction = creditTransactionService.getTransactionDetail(id);
        if (transaction == null) {
            return CommonResult.failed("交易记录不存在");
        }
        return CommonResult.success(transaction);
    }

    @PreAuthorize("hasAuthority('platform:credit:transaction:adjustment')")
    @ApiOperation(value = "创建授信额度调整记录")
    @RequestMapping(value = "/adjustment", method = RequestMethod.POST)
    public CommonResult<String> createAdjustment(
            @ApiParam(value = "用户ID", required = true) @RequestParam Integer userId,
            @ApiParam(value = "调整金额", required = true) @RequestParam BigDecimal amount,
            @ApiParam(value = "调整描述", required = true) @RequestParam String description) {
        Boolean result = creditTransactionService.createAdjustmentTransaction(userId, amount, description);
        if (result) {
            return CommonResult.success("授信额度调整记录创建成功");
        } else {
            return CommonResult.failed("授信额度调整记录创建失败");
        }
    }

    @PreAuthorize("hasAuthority('platform:credit:transaction:export')")
    @ApiOperation(value = "导出授信交易记录")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void export(@Valid CreditTransactionPageVO pageVO, HttpServletResponse response) {
        try {
            String filePath = creditTransactionService.exportTransactionRecords(pageVO);
            if (filePath == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            // 下载文件
            downloadFile(filePath, "授信交易记录.xlsx", response);

        } catch (Exception e) {
            log.error("导出授信交易记录失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('platform:credit:transaction:statistics')")
    @ApiOperation(value = "授信交易统计")
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> statistics(@Valid CreditTransactionPageVO pageVO) {
        Map<String, Object> statistics = creditTransactionService.getTransactionStatistics(pageVO);
        return CommonResult.success(statistics);
    }

    @PreAuthorize("hasAuthority('platform:credit:transaction:type')")
    @ApiOperation(value = "按交易类型统计")
    @RequestMapping(value = "/type/statistics", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> typeStatistics(@Valid CreditTransactionPageVO pageVO) {
        Map<String, Object> typeStats = creditTransactionService.getTransactionTypeStatistics(pageVO);
        return CommonResult.success(typeStats);
    }

    @PreAuthorize("hasAuthority('platform:credit:transaction:user:statistics')")
    @ApiOperation(value = "获取用户交易统计")
    @RequestMapping(value = "/user/{userId}/statistics", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> getUserStatistics(
            @ApiParam(value = "用户ID", required = true) @PathVariable Integer userId) {
        Map<String, Object> userStats = creditTransactionService.getUserTransactionStatistics(userId);
        return CommonResult.success(userStats);
    }

    @PreAuthorize("hasAuthority('platform:credit:transaction:trend')")
    @ApiOperation(value = "获取交易趋势数据")
    @RequestMapping(value = "/trend", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> getTrend(@Valid CreditTransactionPageVO pageVO) {
        Map<String, Object> trendData = creditTransactionService.getTransactionTrend(pageVO);
        return CommonResult.success(trendData);
    }

    @PreAuthorize("hasAuthority('platform:credit:transaction:report')")
    @ApiOperation(value = "获取交易汇总报表")
    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> getSummaryReport(@Valid CreditTransactionPageVO pageVO) {
        Map<String, Object> reportData = creditTransactionService.getTransactionSummaryReport(pageVO);
        return CommonResult.success(reportData);
    }

    @PreAuthorize("hasAuthority('platform:credit:transaction:batch:export')")
    @ApiOperation(value = "批量导出用户交易记录")
    @RequestMapping(value = "/batch/export", method = RequestMethod.POST)
    public void batchExport(
            @ApiParam(value = "用户ID列表", required = true) @RequestParam String userIds,
            @Valid CreditTransactionPageVO pageVO,
            HttpServletResponse response) {
        try {
            // 解析用户ID列表
            List<Integer> userIdList = Arrays.stream(userIds.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(java.util.stream.Collectors.toList());
            
            String filePath = creditTransactionService.batchExportUserTransactions(userIdList, pageVO);
            if (filePath == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            
            // 下载文件
            downloadFile(filePath, "批量用户交易记录.xlsx", response);
            
        } catch (Exception e) {
            log.error("批量导出用户交易记录失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('platform:credit:transaction:dashboard')")
    @ApiOperation(value = "获取交易仪表板数据")
    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 获取今日统计
        CreditTransactionPageVO todayPageVO = new CreditTransactionPageVO();
        todayPageVO.setStartTime(dateFormat.format(getTodayStart()));
        todayPageVO.setEndTime(dateFormat.format(getTodayEnd()));
        Map<String, Object> todayStats = creditTransactionService.getTransactionStatistics(todayPageVO);
        dashboard.put("today", todayStats);

        // 获取本月统计
        CreditTransactionPageVO monthPageVO = new CreditTransactionPageVO();
        monthPageVO.setStartTime(dateFormat.format(getMonthStart()));
        monthPageVO.setEndTime(dateFormat.format(getMonthEnd()));
        Map<String, Object> monthStats = creditTransactionService.getTransactionStatistics(monthPageVO);
        dashboard.put("month", monthStats);

        // 获取总体统计
        CreditTransactionPageVO allPageVO = new CreditTransactionPageVO();
        Map<String, Object> allStats = creditTransactionService.getTransactionStatistics(allPageVO);
        dashboard.put("all", allStats);

        // 获取类型统计
        Map<String, Object> typeStats = creditTransactionService.getTransactionTypeStatistics(allPageVO);
        dashboard.put("typeStatistics", typeStats);

        return CommonResult.success(dashboard);
    }
    /**
     * 文件下载
     */
    private void downloadFile(String filePath, String fileName, HttpServletResponse response) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.setContentLength((int) file.length());
        
        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }
        
        // 删除临时文件
        file.delete();
    }

    /**
     * 获取今日开始时间
     */
    private java.util.Date getTodayStart() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取今日结束时间
     */
    private java.util.Date getTodayEnd() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
        calendar.set(java.util.Calendar.MINUTE, 59);
        calendar.set(java.util.Calendar.SECOND, 59);
        calendar.set(java.util.Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 获取本月开始时间
     */
    private java.util.Date getMonthStart() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取本月结束时间
     */
    private java.util.Date getMonthEnd() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.DAY_OF_MONTH, calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
        calendar.set(java.util.Calendar.MINUTE, 59);
        calendar.set(java.util.Calendar.SECOND, 59);
        calendar.set(java.util.Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
} 