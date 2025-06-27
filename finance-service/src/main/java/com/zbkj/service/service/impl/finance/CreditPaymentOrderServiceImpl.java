package com.zbkj.service.service.impl.finance;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.OrderConstants;
import com.zbkj.common.enums.AuditStatus;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.bill.MerchantMonthStatement;
import com.zbkj.common.model.finance.CreditApplication;
import com.zbkj.common.model.finance.CreditPaymentOrder;
import com.zbkj.common.model.finance.RepaymentRecord;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.ExportUtil;
import com.zbkj.common.vo.finance.CreditBillExportVO;
import com.zbkj.common.vo.finance.CreditBillPageVO;
import com.zbkj.common.vo.finance.CreditBillStatisticsVO;
import com.zbkj.common.vo.finance.CreditBillVO;
import com.zbkj.service.dao.finance.CreditPaymentOrderDao;
import com.zbkj.service.service.finance.CreditApplicationService;
import com.zbkj.service.service.finance.CreditPaymentOrderService;
import com.zbkj.service.service.finance.CreditTransactionService;
import com.zbkj.service.service.finance.RepaymentRecordService;
import com.zbkj.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 授信支付订单服务实现类
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
@Service
public class CreditPaymentOrderServiceImpl extends ServiceImpl<CreditPaymentOrderDao, CreditPaymentOrder> 
        implements CreditPaymentOrderService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CreditTransactionService creditTransactionService;
    
    @Autowired
    private RepaymentRecordService repaymentRecordService;
    
    /**
     * 创建授信支付订单
     * @param userId 用户ID
     * @param originalOrderNo 原始订单号
     * @param paymentAmount 支付金额
     * @return 授信支付订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreditPaymentOrder createCreditPaymentOrder(Integer userId, String originalOrderNo, BigDecimal paymentAmount) {
        try {
            // 获取用户信息
            User user = userService.getById(userId);
            if (ObjectUtil.isNull(user)) {
                throw new CrmebException("用户不存在");
            }
            
            // 获取用户授信申请信息

            
            // 计算利息 - 将千分比利率转换为小数进行计算
            BigDecimal interestRate = user.getCreditRatio().divide(new BigDecimal("1000"), 6, RoundingMode.HALF_UP);
            BigDecimal interestAmount = paymentAmount.multiply(interestRate);
            BigDecimal totalRepaymentAmount = paymentAmount.add(interestAmount);
            
            // 创建授信支付订单
            CreditPaymentOrder creditPaymentOrder = new CreditPaymentOrder();
            creditPaymentOrder.setUserId(userId);
            creditPaymentOrder.setUserName(user.getNickname());
            creditPaymentOrder.setOriginalOrderNo(originalOrderNo);
            creditPaymentOrder.setCreditOrderNo(CrmebUtil.getOrderNo("HT"));
            creditPaymentOrder.setPaymentAmount(paymentAmount);
            creditPaymentOrder.setInterestAmount(interestAmount);
            creditPaymentOrder.setTotalRepaymentAmount(totalRepaymentAmount);
            creditPaymentOrder.setPaidAmount(BigDecimal.ZERO);
            creditPaymentOrder.setRemainingAmount(totalRepaymentAmount);
            creditPaymentOrder.setCreditRatio(user.getCreditRatio());
            creditPaymentOrder.setRepaymentStatus(0); // 未还款
            
            // 计算还款期限（假设为30天）
            creditPaymentOrder.setRepaymentDeadline(user.getRepaymentDays());
            creditPaymentOrder.setStatus(0); // 待还款
            creditPaymentOrder.setCreateTime(new Date());
            creditPaymentOrder.setUpdateTime(new Date());
            creditPaymentOrder.setDeleteFlag(0);
            
            // 保存授信支付订单
            this.save(creditPaymentOrder);
            return creditPaymentOrder;
        } catch (Exception e) {
            log.error("创建授信支付订单异常", e);
            throw new CrmebException("创建授信支付订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据原始订单号获取授信支付订单
     * @param originalOrderNo 原始订单号
     * @return 授信支付订单
     */
    @Override
    public CreditPaymentOrder getByOriginalOrderNo(String originalOrderNo) {
        LambdaQueryWrapper<CreditPaymentOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CreditPaymentOrder::getOriginalOrderNo, originalOrderNo);
        queryWrapper.eq(CreditPaymentOrder::getDeleteFlag, 0);
        queryWrapper.last("LIMIT 1");
        return this.getOne(queryWrapper);
    }
    
    /**
     * 根据授信支付订单号获取订单
     * @param creditOrderNo 授信支付订单号
     * @return 授信支付订单
     */
    @Override
    public CreditPaymentOrder getByCreditOrderNo(String creditOrderNo) {
        LambdaQueryWrapper<CreditPaymentOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CreditPaymentOrder::getCreditOrderNo, creditOrderNo);
        queryWrapper.eq(CreditPaymentOrder::getDeleteFlag, 0);
        queryWrapper.last("LIMIT 1");
        return this.getOne(queryWrapper);
    }
    
    /**
     * 获取用户的待还款订单列表
     * @param userId 用户ID
     * @return 待还款订单列表
     */
    @Override
    public List<CreditPaymentOrder> getUserPendingRepaymentOrders(Integer userId) {
        LambdaQueryWrapper<CreditPaymentOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CreditPaymentOrder::getUserId, userId);
        queryWrapper.in(CreditPaymentOrder::getStatus, 0, 1); // 待还款、逾期
        queryWrapper.eq(CreditPaymentOrder::getDeleteFlag, 0);
        queryWrapper.orderByAsc(CreditPaymentOrder::getRepaymentDeadline);
        return this.list(queryWrapper);
    }
    
    /**
     * 创建还款申请记录（用户发起还款）
     * @param creditOrderNo 授信支付订单号
     * @param repaymentAmount 还款金额
     * @param repaymentProof 还款凭证
     * @return 还款申请是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean processRepayment(String creditOrderNo, BigDecimal repaymentAmount,String repaymentProof) {
        try {
            User info = userService.getInfo();
            CreditPaymentOrder creditOrder = getByCreditOrderNo(creditOrderNo);
            if (ObjectUtil.isNull(creditOrder)) {
                throw new CrmebException("授信支付订单不存在");
            }
            
            if (creditOrder.getStatus() == 2) {
                throw new CrmebException("订单已还清");
            }
            
            if (repaymentAmount.compareTo(creditOrder.getRemainingAmount()) > 0) {
                throw new CrmebException("还款金额超过剩余应还金额");
            }
            
            // 检查是否已有待审核的还款记录
            boolean hasPendingRepayment = repaymentRecordService.hasPendingRepaymentRecord(creditOrderNo);
            if (hasPendingRepayment) {
                throw new CrmebException("该订单已有待审核的还款记录，请等待审核完成");
            }
            
            // 创建还款记录，状态为待审核
            RepaymentRecord repaymentRecord = new RepaymentRecord();
            repaymentRecord.setUserId(info.getId());
            repaymentRecord.setUserName(info.getNickname());
            repaymentRecord.setCreditOrderNo(creditOrderNo);
            repaymentRecord.setOriginalOrderNo(creditOrder.getOriginalOrderNo());
            repaymentRecord.setRepaymentAmount(repaymentAmount);
            repaymentRecord.setRepaymentProof(repaymentProof);
            repaymentRecord.setRepaymentTime(new Date());
            repaymentRecord.setRepaymentMethod(1); // 1-主动还款
            repaymentRecord.setRepaymentStatus(AuditStatus.PENDING.getCode()); // 0-待审核
            repaymentRecord.setApplyTime(new Date());
            repaymentRecord.setCreateTime(new Date());
            repaymentRecord.setUpdateTime(new Date());
            repaymentRecord.setDeleteFlag(0);
            repaymentRecord.setOrganizationId(info.getOrganizationId());
            
            // 保存还款记录
            boolean saveResult = repaymentRecordService.save(repaymentRecord);
            if (!saveResult) {
                throw new CrmebException("创建还款记录失败");
            }
            
            log.info("用户{}创建还款申请成功，订单号：{}，还款金额：{}", 
                    creditOrder.getUserName(), creditOrderNo, repaymentAmount);
            
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("创建还款申请异常", e);
            throw new CrmebException("创建还款申请失败：" + e.getMessage());
        }
    }
    
    /**
     * 审核通过后的实际还款处理（恢复额度、更新订单状态）
     * @param repaymentRecord 还款记录
     * @return 处理是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean processApprovedRepayment(RepaymentRecord repaymentRecord) {
        try {
            CreditPaymentOrder creditOrder = getByCreditOrderNo(repaymentRecord.getCreditOrderNo());
            if (ObjectUtil.isNull(creditOrder)) {
                throw new CrmebException("授信支付订单不存在");
            }
            
            BigDecimal repaymentAmount = repaymentRecord.getRepaymentAmount();
            
            // 获取用户信息并更新授信额度
            User user = userService.getById(creditOrder.getUserId());
            if (ObjectUtil.isNull(user)) {
                throw new CrmebException("用户不存在");
            }
            
            // 计算本金还款部分（优先还本金的逻辑）
            BigDecimal principalRepayment;
            BigDecimal interestPortion;
            
            // 获取当前剩余的本金和利息
            BigDecimal originalPrincipal = creditOrder.getPaymentAmount(); // 原始本金
            BigDecimal originalInterest = creditOrder.getInterestAmount() != null ? creditOrder.getInterestAmount() : BigDecimal.ZERO; // 原始利息
            BigDecimal paidAmount = creditOrder.getPaidAmount() != null ? creditOrder.getPaidAmount() : BigDecimal.ZERO; // 已还金额
            
            // 计算已还的本金和利息（按优先还本金的原则）
            BigDecimal paidPrincipal = paidAmount.min(originalPrincipal); // 已还本金不能超过原始本金
            BigDecimal paidInterest = paidAmount.subtract(paidPrincipal); // 剩余的是已还利息
            
            // 计算剩余的本金和利息
            BigDecimal remainingPrincipal = originalPrincipal.subtract(paidPrincipal);
            BigDecimal remainingInterest = originalInterest.subtract(paidInterest);
            
            // 按优先还本金的原则分配当前还款
            if (repaymentAmount.compareTo(remainingPrincipal) <= 0) {
                // 还款金额不超过剩余本金，全部用于还本金
                principalRepayment = repaymentAmount;
                interestPortion = BigDecimal.ZERO;
            } else {
                // 还款金额超过剩余本金，先还完本金，剩余还利息
                principalRepayment = remainingPrincipal;
                interestPortion = repaymentAmount.subtract(remainingPrincipal);
            }
            
            // 恢复用户授信额度（只恢复本金部分）
            BigDecimal currentRemainingCredit = user.getRemainingCredit() != null ? user.getRemainingCredit() : BigDecimal.ZERO;
            BigDecimal currentUsedCredit = user.getUsedCredit() != null ? user.getUsedCredit() : BigDecimal.ZERO;
            BigDecimal currentPendingRepayment = user.getPendingRepayment() != null ? user.getPendingRepayment() : BigDecimal.ZERO;
            
            user.setRemainingCredit(currentRemainingCredit.add(principalRepayment));
            user.setUsedCredit(currentUsedCredit.subtract(principalRepayment));
            user.setPendingRepayment(currentPendingRepayment.subtract(repaymentAmount));
            user.setUpdateTime(new Date());
            
            boolean userUpdateResult = userService.updateById(user);
            if (!userUpdateResult) {
                throw new CrmebException("更新用户授信额度失败");
            }
            
            // 更新订单还款信息
            creditOrder.setPaidAmount(creditOrder.getPaidAmount().add(repaymentAmount));
            creditOrder.setRemainingAmount(creditOrder.getRemainingAmount().subtract(repaymentAmount));
            
            // 判断是否已还清
            if (creditOrder.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
                creditOrder.setRepaymentStatus(2); // 已还清
                creditOrder.setStatus(2); // 已还清
            } else {
                creditOrder.setRepaymentStatus(1); // 部分还款
            }
            
            creditOrder.setUpdateTime(new Date());
            
            // 更新订单
            this.updateById(creditOrder);
            
            // 创建还款交易记录
            creditTransactionService.createRepaymentTransaction(creditOrder.getUserId(), repaymentRecord.getCreditOrderNo(), repaymentAmount);
            
            log.info("审核通过还款处理成功，用户{}，还款金额：{}，本金：{}，利息：{}，恢复授信额度：{}", 
                    user.getAccount(), repaymentAmount, principalRepayment, interestPortion, principalRepayment);
            
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("审核通过还款处理异常", e);
            throw new CrmebException("审核通过还款处理失败：" + e.getMessage());
        }
    }

    // ========== 后台管理方法实现 ==========

    /**
     * 后台分页查询授信账单列表
     */
    @Override
    public IPage<CreditBillVO> adminPageQuery(CreditBillPageVO pageVO) {
        try {
            // 修复分页参数处理，确保兼容前端传递的page/limit参数
            int pageNumber = pageVO.getPageNumber() != null ? pageVO.getPageNumber() : 
                           (pageVO.getPage() != null ? pageVO.getPage() : 1);
            int pageSize = pageVO.getPageSize() != null ? pageVO.getPageSize() : 
                         (pageVO.getLimit() != null ? pageVO.getLimit() : 10);
            
            // 使用PageHelper进行分页
            com.github.pagehelper.Page<CreditPaymentOrder> page = PageHelper.startPage(pageNumber, pageSize);
            
            LambdaQueryWrapper<CreditPaymentOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CreditPaymentOrder::getDeleteFlag, 0);
            
            // 关键词搜索
            if (StrUtil.isNotBlank(pageVO.getKeywords())) {
                wrapper.and(w -> w.like(CreditPaymentOrder::getUserName, pageVO.getKeywords())
                        .or().like(CreditPaymentOrder::getOriginalOrderNo, pageVO.getKeywords())
                        .or().like(CreditPaymentOrder::getCreditOrderNo, pageVO.getKeywords()));
            }
            
            // 用户ID
            if (pageVO.getUserId() != null) {
                wrapper.eq(CreditPaymentOrder::getUserId, pageVO.getUserId());
            }
            
            // 还款状态
            if (pageVO.getRepaymentStatus() != null) {
                wrapper.eq(CreditPaymentOrder::getRepaymentStatus, pageVO.getRepaymentStatus());
            }
            
            // 订单状态
            if (pageVO.getStatus() != null) {
                wrapper.eq(CreditPaymentOrder::getStatus, pageVO.getStatus());
            }
            
            // 时间范围
            if (StrUtil.isNotBlank(pageVO.getStartTime())) {
                wrapper.ge(CreditPaymentOrder::getCreateTime, pageVO.getStartTime());
            }
            if (StrUtil.isNotBlank(pageVO.getEndTime())) {
                wrapper.le(CreditPaymentOrder::getCreateTime, pageVO.getEndTime());
            }
            
            wrapper.orderByDesc(CreditPaymentOrder::getCreateTime);
            
            // 执行查询
            List<CreditPaymentOrder> orderList = this.list(wrapper);
            
            // 转换为VO
            List<CreditBillVO> voList = orderList.stream()
                    .map(this::convertToCreditBillVO)
                    .collect(Collectors.toList());
            
            // 使用CommonPage.copyPageInfo复制分页信息
            PageInfo<CreditBillVO> pageInfo = CommonPage.copyPageInfo(page, voList);
            
            // 转换为IPage格式
            Page<CreditBillVO> result = new Page<>(pageNumber, pageSize);
            result.setRecords(voList);
            result.setTotal(pageInfo.getTotal());
            result.setPages(pageInfo.getPages());
            result.setCurrent(pageInfo.getPageNum());
            result.setSize(pageInfo.getPageSize());
            
            return result;
        } catch (Exception e) {
            log.error("后台分页查询授信账单列表异常", e);
            return new Page<>();
        }
    }

    /**
     * 获取授信账单详情
     */
    @Override
    public CreditBillVO getBillDetail(String id) {
        try {
            CreditPaymentOrder order = this.getById(id);
            if (order == null || order.getDeleteFlag() == 1) {
                return null;
            }
            return convertToCreditBillVO(order);
        } catch (Exception e) {
            log.error("获取授信账单详情异常", e);
            return null;
        }
    }

    /**
     * 标记账单状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean markBillStatus(String id, Integer status, String remark) {
        try {
            CreditPaymentOrder order = this.getById(id);
            if (order == null || order.getDeleteFlag() == 1) {
                return false;
            }
            
            order.setStatus(status);
            order.setRemark(remark);
            order.setUpdateTime(new Date());
            
            return this.updateById(order);
        } catch (Exception e) {
            log.error("标记账单状态异常", e);
            return false;
        }
    }

    /**
     * 手动还款（管理员代操作）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean manualRepayment(String id, String repaymentAmount, String remark) {
        try {
            CreditPaymentOrder order = this.getById(id);
            if (order == null || order.getDeleteFlag() == 1) {
                return false;
            }
            
            BigDecimal amount = new BigDecimal(repaymentAmount);
            if (amount.compareTo(order.getRemainingAmount()) > 0) {
                return false;
            }
            
            // 获取用户信息并更新授信额度
            User user = userService.getById(order.getUserId());
            if (ObjectUtil.isNull(user)) {
                log.error("手动还款失败，用户不存在，userId: {}", order.getUserId());
                return false;
            }
            
            // 计算本金还款部分（优先还本金的逻辑）
            BigDecimal principalRepayment;
            BigDecimal interestPortion;
            
            // 获取当前剩余的本金和利息
            BigDecimal originalPrincipal = order.getPaymentAmount(); // 原始本金
            BigDecimal originalInterest = order.getInterestAmount() != null ? order.getInterestAmount() : BigDecimal.ZERO; // 原始利息
            BigDecimal paidAmount = order.getPaidAmount() != null ? order.getPaidAmount() : BigDecimal.ZERO; // 已还金额
            
            // 计算已还的本金和利息（按优先还本金的原则）
            BigDecimal paidPrincipal = paidAmount.min(originalPrincipal); // 已还本金不能超过原始本金
            BigDecimal paidInterest = paidAmount.subtract(paidPrincipal); // 剩余的是已还利息
            
            // 计算剩余的本金和利息
            BigDecimal remainingPrincipal = originalPrincipal.subtract(paidPrincipal);
            BigDecimal remainingInterest = originalInterest.subtract(paidInterest);
            
            // 按优先还本金的原则分配当前还款
            if (amount.compareTo(remainingPrincipal) <= 0) {
                // 还款金额不超过剩余本金，全部用于还本金
                principalRepayment = amount;
                interestPortion = BigDecimal.ZERO;
            } else {
                // 还款金额超过剩余本金，先还完本金，剩余还利息
                principalRepayment = remainingPrincipal;
                interestPortion = amount.subtract(remainingPrincipal);
            }
            
            // 恢复用户授信额度（只恢复本金部分）
            BigDecimal currentRemainingCredit = user.getRemainingCredit() != null ? user.getRemainingCredit() : BigDecimal.ZERO;
            BigDecimal currentUsedCredit = user.getUsedCredit() != null ? user.getUsedCredit() : BigDecimal.ZERO;
            BigDecimal currentPendingRepayment = user.getPendingRepayment() != null ? user.getPendingRepayment() : BigDecimal.ZERO;
            
            user.setRemainingCredit(currentRemainingCredit.add(principalRepayment));
            user.setUsedCredit(currentUsedCredit.subtract(principalRepayment));
            user.setPendingRepayment(currentPendingRepayment.subtract(amount));
            user.setUpdateTime(new Date());
            
            boolean userUpdateResult = userService.updateById(user);
            if (!userUpdateResult) {
                log.error("手动还款失败，更新用户授信额度失败，userId: {}", user.getId());
                return false;
            }
            
            // 更新还款信息
            order.setPaidAmount(order.getPaidAmount().add(amount));
            order.setRemainingAmount(order.getRemainingAmount().subtract(amount));
            
            // 判断是否已还清
            if (order.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
                order.setRepaymentStatus(2); // 已还清
                order.setStatus(2); // 已还清
            } else {
                order.setRepaymentStatus(1); // 部分还款
            }
            
            order.setRemark(remark);
            order.setUpdateTime(new Date());
            
            // 创建还款交易记录
            creditTransactionService.createRepaymentTransaction(order.getUserId(), order.getCreditOrderNo(), amount);
            
            log.info("管理员手动还款成功，用户：{}，还款金额：{}，本金：{}，利息：{}，恢复授信额度：{}", 
                    user.getAccount(), amount, principalRepayment, interestPortion, principalRepayment);
            
            return this.updateById(order);
        } catch (Exception e) {
            log.error("手动还款异常", e);
            return false;
        }
    }

    /**
     * 获取逾期账单列表
     */
    @Override
    public IPage<CreditBillVO> getOverdueBills(CreditBillPageVO pageVO) {
        try {
            // 修复分页参数处理，确保兼容前端传递的page/limit参数
            int pageNumber = pageVO.getPageNumber() != null ? pageVO.getPageNumber() : 
                           (pageVO.getPage() != null ? pageVO.getPage() : 1);
            int pageSize = pageVO.getPageSize() != null ? pageVO.getPageSize() : 
                         (pageVO.getLimit() != null ? pageVO.getLimit() : 10);
            
            // 使用PageHelper进行分页
            com.github.pagehelper.Page<CreditPaymentOrder> page = PageHelper.startPage(pageNumber, pageSize);
            
            LambdaQueryWrapper<CreditPaymentOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CreditPaymentOrder::getDeleteFlag, 0);
            wrapper.eq(CreditPaymentOrder::getStatus, 1); // 逾期状态
            wrapper.orderByDesc(CreditPaymentOrder::getCreateTime);
            
            // 执行查询
            List<CreditPaymentOrder> orderList = this.list(wrapper);
            
            // 转换为VO
            List<CreditBillVO> voList = orderList.stream()
                    .map(this::convertToCreditBillVO)
                    .collect(Collectors.toList());
            
            // 使用CommonPage.copyPageInfo复制分页信息
            PageInfo<CreditBillVO> pageInfo = CommonPage.copyPageInfo(page, voList);
            
            // 转换为IPage格式
            Page<CreditBillVO> result = new Page<>(pageNumber, pageSize);
            result.setRecords(voList);
            result.setTotal(pageInfo.getTotal());
            result.setPages(pageInfo.getPages());
            result.setCurrent(pageInfo.getPageNum());
            result.setSize(pageInfo.getPageSize());
            
            return result;
        } catch (Exception e) {
            log.error("获取逾期账单列表异常", e);
            return new Page<>();
        }
    }

    /**
     * 获取授信账单统计信息
     */
    @Override
    public CreditBillStatisticsVO getBillStatistics(CreditBillPageVO pageVO) {
        try {
            CreditBillStatisticsVO statistics = new CreditBillStatisticsVO();
            
            LambdaQueryWrapper<CreditPaymentOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CreditPaymentOrder::getDeleteFlag, 0);
            
            // 添加搜索条件
            if (pageVO.getKeywords() != null && !pageVO.getKeywords().trim().isEmpty()) {
                wrapper.and(w -> w.like(CreditPaymentOrder::getOriginalOrderNo, pageVO.getKeywords())
                        .or().like(CreditPaymentOrder::getCreditOrderNo, pageVO.getKeywords()));
            }
            if (pageVO.getRepaymentStatus() != null) {
                wrapper.eq(CreditPaymentOrder::getRepaymentStatus, pageVO.getRepaymentStatus());
            }
            if (pageVO.getStatus() != null) {
                wrapper.eq(CreditPaymentOrder::getStatus, pageVO.getStatus());
            }
            if (pageVO.getStartTime() != null && !pageVO.getStartTime().trim().isEmpty()) {
                wrapper.ge(CreditPaymentOrder::getCreateTime, pageVO.getStartTime());
            }
            if (pageVO.getEndTime() != null && !pageVO.getEndTime().trim().isEmpty()) {
                wrapper.le(CreditPaymentOrder::getCreateTime, pageVO.getEndTime());
            }
            
            List<CreditPaymentOrder> allOrders = this.list(wrapper);
            
            // 基础统计
            statistics.setTotalBillCount((long) allOrders.size());
            statistics.setTotalPaymentAmount(allOrders.stream()
                    .map(order -> order.getPaymentAmount() != null ? order.getPaymentAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            statistics.setTotalRepaymentAmount(allOrders.stream()
                    .map(order -> order.getTotalRepaymentAmount() != null ? order.getTotalRepaymentAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            statistics.setTotalPaidAmount(allOrders.stream()
                    .map(order -> order.getPaidAmount() != null ? order.getPaidAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            statistics.setTotalRemainingAmount(allOrders.stream()
                    .map(order -> order.getRemainingAmount() != null ? order.getRemainingAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            
            // 按还款状态统计
            long unpaidCount = allOrders.stream().mapToLong(o -> o.getRepaymentStatus() == 0 ? 1 : 0).sum();
            long partialCount = allOrders.stream().mapToLong(o -> o.getRepaymentStatus() == 1 ? 1 : 0).sum();
            long completedCount = allOrders.stream().mapToLong(o -> o.getRepaymentStatus() == 2 ? 1 : 0).sum();
            long overdueCount = allOrders.stream().mapToLong(o -> o.getStatus() == 1 ? 1 : 0).sum();
            
            statistics.setPendingBillCount(unpaidCount);
            statistics.setPartialBillCount(partialCount);
            statistics.setOverdueBillCount(overdueCount);
            statistics.setPaidBillCount(completedCount);
            
            // 计算平均值
            if (allOrders.size() > 0) {
                statistics.setAverageBillAmount(statistics.getTotalPaymentAmount()
                        .divide(BigDecimal.valueOf(allOrders.size()), 2, BigDecimal.ROUND_HALF_UP));
            }
            
            // 计算还款率和逾期率
            if (statistics.getTotalBillCount() > 0) {
                statistics.setRepaymentRate(BigDecimal.valueOf(completedCount)
                        .divide(BigDecimal.valueOf(statistics.getTotalBillCount()), 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100)));
                statistics.setOverdueRate(BigDecimal.valueOf(overdueCount)
                        .divide(BigDecimal.valueOf(statistics.getTotalBillCount()), 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100)));
            }
            
            return statistics;
        } catch (Exception e) {
            log.error("获取授信账单统计信息异常", e);
            return new CreditBillStatisticsVO();
        }
    }

    /**
     * 导出授信账单数据
     */
    @Override
    public void exportBillData(CreditBillPageVO pageVO, javax.servlet.http.HttpServletResponse response) throws Exception {
        setExcelRespProp(response, "授信账单导出");
        // 构建查询条件
        LambdaQueryWrapper<CreditPaymentOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CreditPaymentOrder::getDeleteFlag, 0);

        // 添加搜索条件
        if (StrUtil.isNotBlank(pageVO.getKeywords())) {
            wrapper.and(w -> w.like(CreditPaymentOrder::getUserName, pageVO.getKeywords())
                    .or().like(CreditPaymentOrder::getOriginalOrderNo, pageVO.getKeywords())
                    .or().like(CreditPaymentOrder::getCreditOrderNo, pageVO.getKeywords()));
        }
        if (pageVO.getRepaymentStatus() != null) {
            wrapper.eq(CreditPaymentOrder::getRepaymentStatus, pageVO.getRepaymentStatus());
        }
        if (pageVO.getStatus() != null) {
            wrapper.eq(CreditPaymentOrder::getStatus, pageVO.getStatus());
        }
        if (StrUtil.isNotBlank(pageVO.getStartTime())) {
            wrapper.ge(CreditPaymentOrder::getCreateTime, pageVO.getStartTime());
        }
        if (StrUtil.isNotBlank(pageVO.getEndTime())) {
            wrapper.le(CreditPaymentOrder::getCreateTime, pageVO.getEndTime());
        }

        wrapper.orderByDesc(CreditPaymentOrder::getCreateTime);

        List<CreditPaymentOrder> orders = this.list(wrapper);

        // 转换为导出VO
        List<CreditBillExportVO> exportList = orders.stream().map(order -> {
            CreditBillExportVO exportVO = new CreditBillExportVO();
            exportVO.setId(order.getId().toString());
            exportVO.setUserName(order.getUserName());
            exportVO.setOriginalOrderNo(order.getOriginalOrderNo());
            exportVO.setCreditOrderNo(order.getCreditOrderNo());
            exportVO.setPaymentAmount(order.getPaymentAmount());
            exportVO.setInterestAmount(order.getInterestAmount() != null ? order.getInterestAmount() : BigDecimal.ZERO);
            exportVO.setTotalRepaymentAmount(order.getTotalRepaymentAmount() != null ? order.getTotalRepaymentAmount() : BigDecimal.ZERO);
            exportVO.setPaidAmount(order.getPaidAmount() != null ? order.getPaidAmount() : BigDecimal.ZERO);
            exportVO.setRemainingAmount(order.getRemainingAmount() != null ? order.getRemainingAmount() : BigDecimal.ZERO);
            exportVO.setRepaymentStatus(getRepaymentStatusText(order.getRepaymentStatus()));
            exportVO.setStatus(getStatusText(order.getStatus()));
            exportVO.setCreateTime(order.getCreateTime() != null ?
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCreateTime()) : "");
            exportVO.setRemark(order.getRemark());
            return exportVO;
        }).collect(Collectors.toList());

        EasyExcel.write(response.getOutputStream())
                .head(CreditBillExportVO.class)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet("授信账单")
                .doWrite(exportList);

        log.info("导出授信账单数据成功，共{}条记录", orders.size());
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
     * 批量操作
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchOperation(List<String> ids, String action, String remark) {
        try {
            for (String id : ids) {
                CreditPaymentOrder order = this.getById(id);
                if (order == null || order.getDeleteFlag() == 1) {
                    continue;
                }
                
                switch (action) {
                    case "mark_overdue":
                        order.setStatus(1); // 标记逾期
                        break;
                    case "cancel":
                        order.setStatus(3); // 取消账单
                        break;
                    default:
                        continue;
                }
                
                order.setRemark(remark);
                order.setUpdateTime(new Date());
                this.updateById(order);
            }
            return true;
        } catch (Exception e) {
            log.error("批量操作异常", e);
            return false;
        }
    }

    /**
     * 生成账单报表
     */
    @Override
    public String generateBillReport(CreditBillPageVO pageVO) {
        // TODO: 实现报表生成逻辑
        return "报表生成功能待实现";
    }

    /**
     * 转换为CreditBillVO
     */
    private CreditBillVO convertToCreditBillVO(CreditPaymentOrder order) {
        CreditBillVO vo = new CreditBillVO();
        BeanUtils.copyProperties(order, vo);
        
        // 设置前端需要的字段映射，确保不为null
        vo.setOrderAmount(order.getPaymentAmount() != null ? order.getPaymentAmount() : BigDecimal.ZERO); // 订单金额 = 支付金额
        vo.setRepaidAmount(order.getPaidAmount() != null ? order.getPaidAmount() : BigDecimal.ZERO);   // 已还金额 = 已付金额
        
        // 确保其他金额字段不为null
        vo.setPaymentAmount(order.getPaymentAmount() != null ? order.getPaymentAmount() : BigDecimal.ZERO);
        vo.setPaidAmount(order.getPaidAmount() != null ? order.getPaidAmount() : BigDecimal.ZERO);
        vo.setRemainingAmount(order.getRemainingAmount() != null ? order.getRemainingAmount() : BigDecimal.ZERO);
        vo.setInterestAmount(order.getInterestAmount() != null ? order.getInterestAmount() : BigDecimal.ZERO);
        vo.setTotalRepaymentAmount(order.getTotalRepaymentAmount() != null ? order.getTotalRepaymentAmount() : BigDecimal.ZERO);
        vo.setCreditRatio(order.getCreditRatio() != null ? order.getCreditRatio() : BigDecimal.ZERO);
        
        // 设置状态文本
        vo.setRepaymentStatusText(getRepaymentStatusText(order.getRepaymentStatus()));
        vo.setStatusText(getStatusText(order.getStatus()));

        // 判断是否逾期
        if (order.getRepaymentDeadline() != null && order.getStatus() != 2) {
            Date now = new Date();
            // 这里需要将repaymentDeadline从天数转换为实际日期
            // 假设repaymentDeadline存储的是天数，需要加上创建时间
            if (order.getCreateTime() != null) {
                Date deadlineDate = new Date(order.getCreateTime().getTime() + order.getRepaymentDeadline() * 24 * 60 * 60 * 1000L);
                vo.setIsOverdue(now.after(deadlineDate));
                if (vo.getIsOverdue()) {
                    long diffTime = now.getTime() - deadlineDate.getTime();
                    vo.setOverdueDays((int) (diffTime / (24 * 60 * 60 * 1000)));
                }
            }
        }
        
        return vo;
    }

    /**
     * 获取还款状态文本
     */
    private String getRepaymentStatusText(Integer status) {
        switch (status) {
            case 0: return "未还款";
            case 1: return "部分还款";
            case 2: return "已还清";
            default: return "未知";
        }
    }

    /**
     * 获取订单状态文本
     */
    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "待还款";
            case 1: return "逾期";
            case 2: return "已还清";
            case 3: return "已取消";
            default: return "未知";
        }
    }
} 