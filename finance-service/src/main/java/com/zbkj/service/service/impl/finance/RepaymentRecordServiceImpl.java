package com.zbkj.service.service.impl.finance;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.zbkj.common.model.finance.RepaymentRecord;
import com.zbkj.common.model.user.User;
import com.zbkj.service.dao.finance.RepaymentRecordDao;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.finance.CreditPaymentOrderService;
import com.zbkj.service.service.finance.CreditTransactionService;
import com.zbkj.service.service.finance.RepaymentRecordService;
import com.zbkj.common.vo.finance.RepaymentAuditVO;
import com.zbkj.common.vo.finance.RepaymentCreateVO;
import com.zbkj.common.vo.finance.RepaymentPageVO;
import com.zbkj.common.vo.finance.RepaymentRecordVO;
import com.zbkj.common.vo.finance.RepaymentRecordExportVO;
import com.zbkj.common.vo.finance.RepaymentStatisticsVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.page.CommonPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 还款记录服务实现
 */
@Slf4j
@Service
public class RepaymentRecordServiceImpl extends ServiceImpl<RepaymentRecordDao, RepaymentRecord> implements RepaymentRecordService {

    @Autowired
    private CreditTransactionService creditTransactionService;
    @Autowired
    private UserService userService;
    @Autowired
    private CreditPaymentOrderService creditPaymentOrderService;
    
    /**
     * 检查是否有待审核的还款记录
     * @param creditOrderNo 授信支付订单号
     * @return 是否存在待审核记录
     */
    @Override
    public Boolean hasPendingRepaymentRecord(String creditOrderNo) {
        try {
            LambdaQueryWrapper<RepaymentRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RepaymentRecord::getCreditOrderNo, creditOrderNo)
                   .eq(RepaymentRecord::getRepaymentStatus, 0) // 0-待审核
                   .eq(RepaymentRecord::getDeleteFlag, 0);
            
            long count = this.count(wrapper);
            return count > 0;
        } catch (Exception e) {
            log.error("检查待审核还款记录异常", e);
            return false;
        }
    }
    
    /**
     * 审核还款记录
     * @param repaymentAuditVO 还款审核VO
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean auditRepayment(RepaymentAuditVO repaymentAuditVO) {
        try {
            // 获取当前登录用户
            User currentUser = userService.getInfo();
            if (currentUser == null) {
                return false;
            }
            
            // 查询还款记录
            RepaymentRecord repaymentRecord = this.getById(repaymentAuditVO.getId());
            if (repaymentRecord == null) {
                return false;
            }
            
            // 更新审核信息
            repaymentRecord.setRepaymentStatus(repaymentAuditVO.getRepaymentStatus());
            repaymentRecord.setAuditorId(currentUser.getId());
            repaymentRecord.setAuditorName(currentUser.getRealName());
            repaymentRecord.setAuditTime(new Date());
            repaymentRecord.setAuditRemark(repaymentAuditVO.getAuditRemark());
            repaymentRecord.setUpdateTime(new Date());
            
            // 如果审核通过，调用实际的还款处理逻辑
            if (repaymentAuditVO.getRepaymentStatus() == 1) {
                // 调用授信支付订单服务的实际还款处理方法
                Boolean processResult = creditPaymentOrderService.processApprovedRepayment(repaymentRecord);
                if (!processResult) {
                    log.error("审核通过后的还款处理失败，还款记录ID：{}", repaymentRecord.getId());
                    return false;
                }
            }
            
            // 更新还款记录
            return this.updateById(repaymentRecord);
        } catch (Exception e) {
            log.error("审核还款记录异常", e);
            return false;
        }
    }

    /**
     * 分页查询还款记录
     * @param repaymentPageVO 分页查询参数
     * @return 分页结果
     */
    @Override
    public IPage<RepaymentRecordVO> pageRepayment(RepaymentPageVO repaymentPageVO) {
        try {
            // 修复分页参数处理，确保兼容前端传递的page/limit参数
            int pageNumber = repaymentPageVO.getPageNumber() != null ? repaymentPageVO.getPageNumber() : 
                           (repaymentPageVO.getPage() != null ? repaymentPageVO.getPage() : 1);
            int pageSize = repaymentPageVO.getPageSize() != null ? repaymentPageVO.getPageSize() : 
                         (repaymentPageVO.getLimit() != null ? repaymentPageVO.getLimit() : 10);
            
            // 使用PageHelper进行分页
            com.github.pagehelper.Page<RepaymentRecord> page = PageHelper.startPage(pageNumber, pageSize);
            
            // 构建查询条件
            LambdaQueryWrapper<RepaymentRecord> queryWrapper = new LambdaQueryWrapper<>();
            
            // 关键词搜索（用户名称或还款记录ID）
            if (repaymentPageVO.getKeywords() != null && !repaymentPageVO.getKeywords().trim().isEmpty()) {
                queryWrapper.and(wrapper -> wrapper
                    .like(RepaymentRecord::getUserName, repaymentPageVO.getKeywords())
                    .or().like(RepaymentRecord::getId, repaymentPageVO.getKeywords())
                );
            }
            
            // 设置查询条件
            if (repaymentPageVO.getUserId() != null ) {
                queryWrapper.eq(RepaymentRecord::getUserId, repaymentPageVO.getUserId());
            }
            
            if (repaymentPageVO.getUserName() != null && !repaymentPageVO.getUserName().isEmpty()) {
                queryWrapper.like(RepaymentRecord::getUserName, repaymentPageVO.getUserName());
            }
            
            if (repaymentPageVO.getRepaymentStatus() != null) {
                queryWrapper.eq(RepaymentRecord::getRepaymentStatus, repaymentPageVO.getRepaymentStatus());
            }
            
            if (repaymentPageVO.getRepaymentMethod() != null) {
                queryWrapper.eq(RepaymentRecord::getRepaymentMethod, repaymentPageVO.getRepaymentMethod());
            }
            
            // 设置时间范围
            if (repaymentPageVO.getStartTime() != null && !repaymentPageVO.getStartTime().isEmpty()) {
                queryWrapper.ge(RepaymentRecord::getRepaymentTime, repaymentPageVO.getStartTime());
            }
            
            if (repaymentPageVO.getEndTime() != null && !repaymentPageVO.getEndTime().isEmpty()) {
                queryWrapper.le(RepaymentRecord::getRepaymentTime, repaymentPageVO.getEndTime());
            }
            
            // 设置排序
            queryWrapper.orderByDesc(RepaymentRecord::getCreateTime);
            
            // 只查询未删除的记录
            queryWrapper.eq(RepaymentRecord::getDeleteFlag, 0);
            
            // 执行查询
            List<RepaymentRecord> recordList = this.list(queryWrapper);
            
            // 转换为VO
            List<RepaymentRecordVO> voList = recordList.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());
            
            // 使用CommonPage.copyPageInfo复制分页信息
            PageInfo<RepaymentRecordVO> pageInfo = CommonPage.copyPageInfo(page, voList);
            
            // 转换为IPage格式
            Page<RepaymentRecordVO> result = new Page<>(pageNumber, pageSize);
            result.setRecords(voList);
            result.setTotal(pageInfo.getTotal());
            result.setPages(pageInfo.getPages());
            result.setCurrent(pageInfo.getPageNum());
            result.setSize(pageInfo.getPageSize());
            
            return result;
        } catch (Exception e) {
            log.error("分页查询还款记录异常", e);
            return null;
        }
    }

    /**
     * 获取还款记录详情
     * @param id 还款记录ID
     * @return 还款记录详情
     */
    @Override
    public RepaymentRecordVO getRepaymentDetail(String id) {
        try {
            // 查询还款记录
            RepaymentRecord repaymentRecord = this.getById(id);
            if (repaymentRecord == null) {
                return null;
            }
            
            // 转换为VO
            return convertToVO(repaymentRecord);
        } catch (Exception e) {
            log.error("获取还款记录详情异常", e);
            return null;
        }
    }

    /**
     * 获取当前用户的还款记录
     * @param repaymentPageVO 分页查询参数
     * @return 分页结果
     */
    @Override
    public IPage<RepaymentRecordVO> getCurrentUserRepayments(RepaymentPageVO repaymentPageVO) {
        try {
            // 获取当前登录用户
            User currentUser = userService.getInfo();
            if (currentUser == null) {
                return null;
            }
            
            // 设置当前用户ID
            repaymentPageVO.setUserId(currentUser.getId());
            
            // 调用分页查询方法
            return pageRepayment(repaymentPageVO);
        } catch (Exception e) {
            log.error("获取当前用户的还款记录异常", e);
            return null;
        }
    }

    // ========== 后台管理方法实现 ==========

    /**
     * 后台分页查询还款记录列表
     * @param pageVO 分页查询参数
     * @return 分页结果
     */
    @Override
    public IPage<RepaymentRecordVO> adminPageQuery(RepaymentPageVO pageVO) {
        try {
            // 修复分页参数处理，确保兼容前端传递的page/limit参数
            int pageNumber = pageVO.getPageNumber() != null ? pageVO.getPageNumber() : 
                           (pageVO.getPage() != null ? pageVO.getPage() : 1);
            int pageSize = pageVO.getPageSize() != null ? pageVO.getPageSize() : 
                         (pageVO.getLimit() != null ? pageVO.getLimit() : 10);
            
            // 使用PageHelper进行分页
            com.github.pagehelper.Page<RepaymentRecord> page = PageHelper.startPage(pageNumber, pageSize);
            
            LambdaQueryWrapper<RepaymentRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RepaymentRecord::getDeleteFlag, 0);
            
            // 关键词搜索
            if (StringUtils.hasText(pageVO.getKeywords())) {
                wrapper.and(w -> w.like(RepaymentRecord::getUserName, pageVO.getKeywords())
                        .or().like(RepaymentRecord::getId, pageVO.getKeywords()));
            }
            
            // 用户筛选
            if (pageVO.getUserId() != null) {
                wrapper.eq(RepaymentRecord::getUserId, pageVO.getUserId());
            }
            
            if (StringUtils.hasText(pageVO.getUserName())) {
                wrapper.like(RepaymentRecord::getUserName, pageVO.getUserName());
            }
            
            // 状态筛选
            if (pageVO.getRepaymentStatus() != null) {
                wrapper.eq(RepaymentRecord::getRepaymentStatus, pageVO.getRepaymentStatus());
            }
            
            // 还款方式筛选
            if (pageVO.getRepaymentMethod() != null) {
                wrapper.eq(RepaymentRecord::getRepaymentMethod, pageVO.getRepaymentMethod());
            }
            
            // 时间范围筛选
            if (StringUtils.hasText(pageVO.getStartTime())) {
                wrapper.ge(RepaymentRecord::getRepaymentTime, pageVO.getStartTime());
            }
            if (StringUtils.hasText(pageVO.getEndTime())) {
                wrapper.le(RepaymentRecord::getRepaymentTime, pageVO.getEndTime());
            }
            
            wrapper.orderByDesc(RepaymentRecord::getCreateTime);
            
            // 执行查询
            List<RepaymentRecord> recordList = this.list(wrapper);
            
            // 转换为VO
            List<RepaymentRecordVO> voList = recordList.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());
            
            // 使用CommonPage.copyPageInfo复制分页信息
            PageInfo<RepaymentRecordVO> pageInfo = CommonPage.copyPageInfo(page, voList);
            
            // 转换为IPage格式
            Page<RepaymentRecordVO> result = new Page<>(pageNumber, pageSize);
            result.setRecords(voList);
            result.setTotal(pageInfo.getTotal());
            result.setPages(pageInfo.getPages());
            result.setCurrent(pageInfo.getPageNum());
            result.setSize(pageInfo.getPageSize());
            
            return result;
        } catch (Exception e) {
            log.error("后台分页查询还款记录列表失败", e);
            return null;
        }
    }

    /**
     * 批量审核还款记录
     * @param ids 还款记录ID列表
     * @param status 审核状态
     * @param auditRemark 审核备注
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchAudit(List<String> ids, Integer status, String auditRemark) {
        try {
            if (ids == null || ids.isEmpty()) {
                return false;
            }
            
            // 获取当前登录用户
            User currentUser = userService.getInfo();
            if (currentUser == null) {
                return false;
            }
            
            boolean allSuccess = true;
            for (String id : ids) {
                RepaymentAuditVO auditVO = new RepaymentAuditVO();
                auditVO.setId(id);
                auditVO.setRepaymentStatus(status);
                auditVO.setAuditRemark(auditRemark);
                
                boolean result = auditRepayment(auditVO);
                if (!result) {
                    allSuccess = false;
                    log.error("批量审核失败，ID: {}", id);
                }
            }
            
            if (allSuccess) {
                log.info("批量审核成功，审核状态: {}, 数量: {}", status, ids.size());
            }
            return allSuccess;
        } catch (Exception e) {
            log.error("批量审核还款记录异常，审核状态: {}", status, e);
            return false;
        }
    }

    /**
     * 获取还款统计信息
     * @param pageVO 查询参数
     * @return 统计信息
     */
    @Override
    public RepaymentStatisticsVO getRepaymentStatistics(RepaymentPageVO pageVO) {
        try {
            LambdaQueryWrapper<RepaymentRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RepaymentRecord::getDeleteFlag, 0);
            
            // 时间范围筛选
            if (StringUtils.hasText(pageVO.getStartTime())) {
                wrapper.ge(RepaymentRecord::getRepaymentTime, pageVO.getStartTime());
            }
            if (StringUtils.hasText(pageVO.getEndTime())) {
                wrapper.le(RepaymentRecord::getRepaymentTime, pageVO.getEndTime());
            }
            
            List<RepaymentRecord> records = this.list(wrapper);
            
            RepaymentStatisticsVO statistics = new RepaymentStatisticsVO();
            statistics.setTotalCount((long) records.size());
            statistics.setPendingCount(records.stream().filter(r -> r.getRepaymentStatus() == 0).count());
            statistics.setApprovedCount(records.stream().filter(r -> r.getRepaymentStatus() == 1).count());
            statistics.setRejectedCount(records.stream().filter(r -> r.getRepaymentStatus() == 2).count());
            
            BigDecimal totalAmount = records.stream()
                    .map(RepaymentRecord::getRepaymentAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            statistics.setTotalAmount(totalAmount);
            
            BigDecimal approvedAmount = records.stream()
                    .filter(r -> r.getRepaymentStatus() == 1)
                    .map(RepaymentRecord::getRepaymentAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            statistics.setApprovedAmount(approvedAmount);
            
            BigDecimal pendingAmount = records.stream()
                    .filter(r -> r.getRepaymentStatus() == 0)
                    .map(RepaymentRecord::getRepaymentAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            statistics.setPendingAmount(pendingAmount);
            
            BigDecimal rejectedAmount = records.stream()
                    .filter(r -> r.getRepaymentStatus() == 2)
                    .map(RepaymentRecord::getRepaymentAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            statistics.setRejectedAmount(rejectedAmount);
            
            // 计算平均还款金额
            if (statistics.getTotalCount() > 0) {
                statistics.setAverageAmount(totalAmount.divide(BigDecimal.valueOf(statistics.getTotalCount()), 2, BigDecimal.ROUND_HALF_UP));
            }
            
            // 计算审核通过率
            if (statistics.getTotalCount() > 0) {
                BigDecimal approvalRate = BigDecimal.valueOf(statistics.getApprovedCount())
                        .divide(BigDecimal.valueOf(statistics.getTotalCount()), 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                statistics.setApprovalRate(approvalRate);
            }
            
            // 计算今日统计
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            long todayCount = records.stream()
                    .filter(r -> r.getRepaymentTime() != null && 
                            r.getRepaymentTime().toString().startsWith(today))
                    .count();
            statistics.setTodayCount(todayCount);
            
            BigDecimal todayAmount = records.stream()
                    .filter(r -> r.getRepaymentTime() != null && 
                            r.getRepaymentTime().toString().startsWith(today))
                    .map(RepaymentRecord::getRepaymentAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            statistics.setTodayAmount(todayAmount);
            
            // 计算本月统计
            String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            long monthCount = records.stream()
                    .filter(r -> r.getRepaymentTime() != null && 
                            r.getRepaymentTime().toString().startsWith(currentMonth))
                    .count();
            statistics.setMonthCount(monthCount);
            
            BigDecimal monthAmount = records.stream()
                    .filter(r -> r.getRepaymentTime() != null && 
                            r.getRepaymentTime().toString().startsWith(currentMonth))
                    .map(RepaymentRecord::getRepaymentAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            statistics.setMonthAmount(monthAmount);
            
            return statistics;
        } catch (Exception e) {
            log.error("获取还款统计信息失败", e);
            return null;
        }
    }

    /**
     * 导出还款记录数据
     */
    @Override
    public void exportRepaymentData(RepaymentPageVO pageVO, HttpServletResponse response) throws Exception {
        setExcelRespProp(response, "还款记录导出");

        // 构建查询条件
        LambdaQueryWrapper<RepaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepaymentRecord::getDeleteFlag, 0);
        
        // 添加搜索条件
        if (StringUtils.hasText(pageVO.getKeywords())) {
            wrapper.and(w -> w.like(RepaymentRecord::getUserName, pageVO.getKeywords())
                    .or().like(RepaymentRecord::getId, pageVO.getKeywords()));
        }
        
        // 用户筛选
        if (pageVO.getUserId() != null) {
            wrapper.eq(RepaymentRecord::getUserId, pageVO.getUserId());
        }
        
        if (StringUtils.hasText(pageVO.getUserName())) {
            wrapper.like(RepaymentRecord::getUserName, pageVO.getUserName());
        }
        
        // 状态筛选
        if (pageVO.getRepaymentStatus() != null) {
            wrapper.eq(RepaymentRecord::getRepaymentStatus, pageVO.getRepaymentStatus());
        }
        
        // 还款方式筛选
        if (pageVO.getRepaymentMethod() != null) {
            wrapper.eq(RepaymentRecord::getRepaymentMethod, pageVO.getRepaymentMethod());
        }
        
        // 时间范围筛选
        if (StringUtils.hasText(pageVO.getStartTime())) {
            wrapper.ge(RepaymentRecord::getRepaymentTime, pageVO.getStartTime());
        }
        if (StringUtils.hasText(pageVO.getEndTime())) {
            wrapper.le(RepaymentRecord::getRepaymentTime, pageVO.getEndTime());
        }
        
        wrapper.orderByDesc(RepaymentRecord::getCreateTime);

        List<RepaymentRecord> records = this.list(wrapper);

        // 转换为导出VO
        List<RepaymentRecordExportVO> exportList = records.stream().map(record -> {
            RepaymentRecordExportVO exportVO = new RepaymentRecordExportVO();
            exportVO.setId(record.getId().toString());
            exportVO.setUserName(record.getUserName());
            exportVO.setCreditOrderNo(record.getCreditOrderNo());
            exportVO.setOriginalOrderNo(record.getOriginalOrderNo());
            exportVO.setRepaymentAmount(record.getRepaymentAmount() != null ? record.getRepaymentAmount() : BigDecimal.ZERO);
            exportVO.setRepaymentMethod(getRepaymentMethodText(record.getRepaymentMethod()));
            exportVO.setRepaymentStatus(getStatusText(record.getRepaymentStatus()));
            exportVO.setRepaymentTime(record.getRepaymentTime() != null ? 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(record.getRepaymentTime()) : "");
            exportVO.setRepaymentProof(record.getRepaymentProof());
            exportVO.setAuditorName(record.getAuditorName());
            exportVO.setAuditTime(record.getAuditTime() != null ? 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(record.getAuditTime()) : "");
            exportVO.setAuditRemark(record.getAuditRemark());
            exportVO.setCreateTime(record.getCreateTime() != null ? 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(record.getCreateTime()) : "");
            exportVO.setRemark(record.getRemark());
            return exportVO;
        }).collect(Collectors.toList());

        // 写入Excel
        EasyExcel.write(response.getOutputStream())
                .head(RepaymentRecordExportVO.class)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet("还款记录")
                .doWrite(exportList);

        log.info("导出还款记录数据成功，共{}条记录", records.size());
    }

    /**
     * 设置excel下载响应头属性
     */
    private void setExcelRespProp(HttpServletResponse response, String rawFileName) throws UnsupportedEncodingException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(rawFileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
    }

    /**
     * 根据授信订单号查询还款记录
     * @param creditOrderNo 授信订单号
     * @return 还款记录列表
     */
    @Override
    public List<RepaymentRecordVO> getRepaymentsByCreditOrder(String creditOrderNo) {
        try {
            LambdaQueryWrapper<RepaymentRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RepaymentRecord::getCreditOrderNo, creditOrderNo);
            wrapper.eq(RepaymentRecord::getDeleteFlag, 0);
            wrapper.orderByDesc(RepaymentRecord::getCreateTime);
            
            List<RepaymentRecord> records = this.list(wrapper);
            return records.stream().map(this::convertToVO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据授信订单号查询还款记录失败，creditOrderNo: {}", creditOrderNo, e);
            return new ArrayList<>();
        }
    }
    
    // ========== 私有辅助方法 ==========
    
    /**
     * 转换为VO对象
     * @param repaymentRecord 还款记录实体
     * @return 还款记录VO
     */
    private RepaymentRecordVO convertToVO(RepaymentRecord repaymentRecord) {
        RepaymentRecordVO vo = new RepaymentRecordVO();
        BeanUtils.copyProperties(repaymentRecord, vo);
        vo.setStatusText(getStatusText(repaymentRecord.getRepaymentStatus()));
        return vo;
    }
    
    /**
     * 获取状态文字描述
     */
    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待审核";
            case 1: return "审核通过";
            case 2: return "审核拒绝";
            default: return "未知";
        }
    }
    
    /**
     * 获取还款方式文字描述
     */
    private String getRepaymentMethodText(Integer method) {
        if (method == null) return "未知";
        switch (method) {
            case 0: return "银行转账";
            case 1: return "现金";
            case 2: return "其他";
            default: return "未知";
        }
    }
} 