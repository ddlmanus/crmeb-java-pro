package com.zbkj.service.service.impl.finance;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.enums.FarmTypeEnum;
import com.zbkj.common.enums.UserTypeEnum;
import com.zbkj.common.model.finance.CreditTransaction;
import com.zbkj.common.model.finance.RepaymentRecord;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.service.dao.finance.CreditTransactionDao;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.finance.CreditTransactionService;
import com.zbkj.common.vo.finance.CreditTransactionPageVO;
import com.zbkj.common.vo.finance.CreditTransactionVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CreditTransactionServiceImpl extends ServiceImpl<CreditTransactionDao, CreditTransaction> implements CreditTransactionService {
    
    @Autowired
    private UserService userService;
    
    /**
     * 创建授信消费交易记录
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param amount 交易金额
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createConsumptionTransaction(Integer userId, String orderId, BigDecimal amount) {
        try {
            // 获取用户信息
            User user = userService.getById(userId);
            if (ObjectUtil.isNull(user)) {
                log.error("创建授信消费交易记录失败，用户不存在，userId: {}", userId);
                return false;
            }
            
            // 创建交易记录
            CreditTransaction transaction = new CreditTransaction();
            transaction.setUserId(userId.toString());
            transaction.setUserName(user.getNickname());
            transaction.setOrderSn(orderId);
            transaction.setOrderType("CREDIT_PAY");
            transaction.setTransactionType(0); // 授信消费
            transaction.setTransactionAmount(amount);
            transaction.setRepaymentAmount(amount);
            transaction.setTotalRepaymentAmount(amount);
            
            // 计算交易前后可用额度
            BigDecimal beforeAmount = user.getRemainingCredit() != null ? user.getRemainingCredit().add(amount) : amount;
            BigDecimal afterAmount = user.getRemainingCredit() != null ? user.getRemainingCredit() : BigDecimal.ZERO;
            
            transaction.setBeforeAvailableAmount(beforeAmount);
            transaction.setAfterAvailableAmount(afterAmount);
            transaction.setInterestAmount(BigDecimal.ZERO); // 暂时设置为0，后续可以根据利率计算
            transaction.setTransactionDescription(StrUtil.format("订单{}授信支付，消费金额{}元", orderId, amount));
            transaction.setTransactionTime(new Date());
            transaction.setCreateTime(new Date());
            transaction.setUpdateTime(new Date());
            transaction.setDeleteFlag(0);
            transaction.setOrganizationId(user.getOrganizationId());
            // 保存交易记录
            this.save(transaction);
            
            log.info("创建授信消费交易记录成功，userId: {}, orderId: {}, amount: {}", userId, orderId, amount);
            return true;
        } catch (Exception e) {
            log.error("创建授信消费交易记录异常，userId: {}, orderId: {}, amount: {}", userId, orderId, amount, e);
            return false;
        }
    }

    /**
     * 创建还款交易记录
     * @param userId 用户ID
     * @param repaymentId 还款记录ID
     * @param amount 还款金额
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createRepaymentTransaction(Integer userId, String repaymentId, BigDecimal amount) {
        try {
            // 获取用户信息
            User user = userService.getById(userId);
            if (ObjectUtil.isNull(user)) {
                log.error("创建还款交易记录失败，用户不存在，userId: {}", userId);
                return false;
            }
            
            // 创建交易记录
            CreditTransaction transaction = new CreditTransaction();
            transaction.setId(IdUtil.randomUUID());
            transaction.setUserId(userId.toString());
            transaction.setUserName(user.getNickname());
            transaction.setOrderSn(repaymentId);
            transaction.setOrderType("REPAYMENT");
            transaction.setTransactionType(1); // 还款
            transaction.setTransactionAmount(amount);
            transaction.setRepaymentAmount(amount.negate()); // 还款为负数
            transaction.setTotalRepaymentAmount(amount);
            
            // 计算交易前后可用额度
            BigDecimal beforeAmount = user.getRemainingCredit() != null ? user.getRemainingCredit().subtract(amount) : amount.negate();
            BigDecimal afterAmount = user.getRemainingCredit() != null ? user.getRemainingCredit() : BigDecimal.ZERO;
            
            transaction.setBeforeAvailableAmount(beforeAmount);
            transaction.setAfterAvailableAmount(afterAmount);
            transaction.setInterestAmount(BigDecimal.ZERO);
            transaction.setTransactionDescription(StrUtil.format("还款{}元，恢复授信额度", amount));
            transaction.setTransactionTime(new Date());
            transaction.setCreateTime(new Date());
            transaction.setUpdateTime(new Date());
            transaction.setDeleteFlag(0);
            
            // 保存交易记录
            this.save(transaction);
            
            log.info("创建还款交易记录成功，userId: {}, repaymentId: {}, amount: {}", userId, repaymentId, amount);
            return true;
        } catch (Exception e) {
            log.error("创建还款交易记录异常，userId: {}, repaymentId: {}, amount: {}", userId, repaymentId, amount, e);
            return false;
        }
    }

    /**
     * 创建退款交易记录
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param amount 退款金额
     * @return 是否成功
     */
    public Boolean createRefundTransaction(Integer userId, String orderId, BigDecimal amount) {
        try {
            // 获取用户信息
            User user = userService.getById(userId);
            if (ObjectUtil.isNull(user)) {
                log.error("创建退款交易记录失败，用户不存在，userId: {}", userId);
                return false;
            }
            
            // 创建交易记录
            CreditTransaction transaction = new CreditTransaction();
            transaction.setId(IdUtil.randomUUID());
            transaction.setUserId(userId.toString());
            transaction.setUserName(user.getNickname());
            transaction.setOrderSn(orderId);
            transaction.setOrderType("REFUND");
            transaction.setTransactionType(3); // 退款（新增类型）
            transaction.setTransactionAmount(amount);
            transaction.setRepaymentAmount(amount.negate()); // 退款为负数
            transaction.setTotalRepaymentAmount(amount);
            
            // 计算交易前后可用额度
            BigDecimal beforeAmount = user.getRemainingCredit() != null ? user.getRemainingCredit().subtract(amount) : amount.negate();
            BigDecimal afterAmount = user.getRemainingCredit() != null ? user.getRemainingCredit() : BigDecimal.ZERO;
            
            transaction.setBeforeAvailableAmount(beforeAmount);
            transaction.setAfterAvailableAmount(afterAmount);
            transaction.setInterestAmount(BigDecimal.ZERO);
            transaction.setTransactionDescription(StrUtil.format("订单{}退款，恢复授信额度{}元", orderId, amount));
            transaction.setTransactionTime(new Date());
            transaction.setCreateTime(new Date());
            transaction.setUpdateTime(new Date());
            transaction.setDeleteFlag(0);
            
            // 保存交易记录
            this.save(transaction);
            
            log.info("创建退款交易记录成功，userId: {}, orderId: {}, amount: {}", userId, orderId, amount);
            return true;
        } catch (Exception e) {
            log.error("创建退款交易记录异常，userId: {}, orderId: {}, amount: {}", userId, orderId, amount, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createAdjustmentTransaction(Integer userId, BigDecimal amount, String description) {
        try {
            // 获取用户信息
            User user = userService.getById(userId);
            if (ObjectUtil.isNull(user)) {
                log.error("创建授信额度调整交易记录失败，用户不存在，userId: {}", userId);
                return false;
            }
            
            // 创建交易记录
            CreditTransaction transaction = new CreditTransaction();
            transaction.setId(IdUtil.randomUUID());
            transaction.setUserId(userId.toString());
            transaction.setUserName(user.getNickname());
            transaction.setOrderSn("ADJ_" + System.currentTimeMillis());
            transaction.setOrderType("ADJUSTMENT");
            transaction.setTransactionType(2); // 授信额度调整
            transaction.setTransactionAmount(amount);
            transaction.setRepaymentAmount(BigDecimal.ZERO);
            transaction.setTotalRepaymentAmount(BigDecimal.ZERO);
            
            // 计算交易前后可用额度
            BigDecimal beforeAmount = user.getRemainingCredit() != null ? user.getRemainingCredit() : BigDecimal.ZERO;
            BigDecimal afterAmount = beforeAmount.add(amount);
            
            transaction.setBeforeAvailableAmount(beforeAmount);
            transaction.setAfterAvailableAmount(afterAmount);
            transaction.setInterestAmount(BigDecimal.ZERO);
            transaction.setTransactionDescription(description);
            transaction.setTransactionTime(new Date());
            transaction.setCreateTime(new Date());
            transaction.setUpdateTime(new Date());
            transaction.setDeleteFlag(0);
            
            // 保存交易记录
            this.save(transaction);
            
            log.info("创建授信额度调整交易记录成功，userId: {}, amount: {}, description: {}", userId, amount, description);
            return true;
        } catch (Exception e) {
            log.error("创建授信额度调整交易记录异常，userId: {}, amount: {}, description: {}", userId, amount, description, e);
            return false;
        }
    }

    @Override
    public PageInfo<CreditTransactionVO> pageTransaction(CreditTransactionPageVO pageVO) {
        com.github.pagehelper.Page<CreditTransaction> page = PageHelper.startPage(pageVO.getPage(), pageVO.getLimit());
        // 构建查询条件
        LambdaQueryWrapper<CreditTransaction> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CreditTransaction::getDeleteFlag, 0);
        
        if (StrUtil.isNotBlank(pageVO.getUserId())) {
            queryWrapper.eq(CreditTransaction::getUserId, pageVO.getUserId());
        }
        if (StrUtil.isNotBlank(pageVO.getUserName())) {
            queryWrapper.eq(CreditTransaction::getUserName, pageVO.getUserName());
        }
        if (StrUtil.isNotBlank(pageVO.getOrderSn())) {
            queryWrapper.like(CreditTransaction::getOrderSn, pageVO.getOrderSn());
        }
        if (ObjectUtil.isNotNull(pageVO.getTransactionType())) {
            queryWrapper.eq(CreditTransaction::getTransactionType, pageVO.getTransactionType());
        }
        if (StrUtil.isNotBlank(pageVO.getStartTime())) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = dateFormat.parse(pageVO.getStartTime());
                queryWrapper.ge(CreditTransaction::getTransactionTime, startDate);
            } catch (Exception e) {
                // 尝试使用完整的日期时间格式
                try {
                    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date startDate = dateTimeFormat.parse(pageVO.getStartTime());
                    queryWrapper.ge(CreditTransaction::getTransactionTime, startDate);
                } catch (Exception ex) {
                    log.warn("解析开始时间失败: {}", pageVO.getStartTime());
                }
            }
        }
        if (StrUtil.isNotBlank(pageVO.getEndTime())) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date endDate = dateFormat.parse(pageVO.getEndTime());
                // 结束时间设置为当天的23:59:59
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endDate);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                queryWrapper.le(CreditTransaction::getTransactionTime, calendar.getTime());
            } catch (Exception e) {
                // 尝试使用完整的日期时间格式
                try {
                    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date endDate = dateTimeFormat.parse(pageVO.getEndTime());
                    queryWrapper.le(CreditTransaction::getTransactionTime, endDate);
                } catch (Exception ex) {
                    log.warn("解析结束时间失败: {}", pageVO.getEndTime());
                }
            }
        }
        
        queryWrapper.orderByDesc(CreditTransaction::getTransactionTime);
        
        // 分页查询
        List<CreditTransaction> list = this.list(queryWrapper);
        List<CreditTransactionVO> transactionVOList = list.stream().map(transaction -> convertToCreditTransactionVO(transaction)).collect(Collectors.toList());
        PageInfo<CreditTransactionVO> creditTransactionPageInfo = CommonPage.copyPageInfo(page, transactionVOList);
        return creditTransactionPageInfo;
    }

    @Override
    public CreditTransactionVO getTransactionDetail(String id) {
        CreditTransaction transaction = this.getById(id);
        if (ObjectUtil.isNull(transaction)) {
            return null;
        }
        return convertToCreditTransactionVO(transaction);
    }

    @Override
    public IPage<CreditTransactionVO> getCurrentUserTransactions(CreditTransactionPageVO pageVO) {
        try {
            // 获取当前登录用户
            User currentUser = userService.getInfo();
            if (currentUser == null) {
                throw new RuntimeException("用户未登录");
            }

            // 创建分页对象
            Page<CreditTransaction> page = new Page<>(pageVO.getPage(), pageVO.getLimit());
            
            // 构建查询条件
            LambdaQueryWrapper<CreditTransaction> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CreditTransaction::getDeleteFlag, 0);
            
            // 根据用户类型查询不同的数据
            if(!Objects.isNull(currentUser)){
                //判断是合作社管理员还是社员
                if(currentUser.getFarmType().equals(FarmTypeEnum.TENANT_TYPE_COOPERATIVE.getCode())&&currentUser.getUserType().equals(UserTypeEnum.USER_TYPE_ADMIN.getCode())){
                    queryWrapper.eq(CreditTransaction::getOrganizationId, currentUser.getOrganizationId());
                }else {
                    queryWrapper.eq(CreditTransaction::getUserId, currentUser.getId());
                }
            }
            
            // 添加其他查询条件
            if (pageVO.getTransactionType() != null) {
                queryWrapper.eq(CreditTransaction::getTransactionType, pageVO.getTransactionType());
            }
            if (StrUtil.isNotBlank(pageVO.getOrderSn())) {
                queryWrapper.like(CreditTransaction::getOrderSn, pageVO.getOrderSn());
            }
            if (StrUtil.isNotBlank(pageVO.getStartTime())) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date startDate = sdf.parse(pageVO.getStartTime());
                    queryWrapper.ge(CreditTransaction::getTransactionTime, startDate);
                } catch (ParseException e) {
                    log.warn("开始时间格式错误: {}", pageVO.getStartTime());
                }
            }
            if (StrUtil.isNotBlank(pageVO.getEndTime())) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date endDate = sdf.parse(pageVO.getEndTime());
                    // 结束时间设置为当天的23:59:59
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(endDate);
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    queryWrapper.le(CreditTransaction::getTransactionTime, calendar.getTime());
                } catch (ParseException e) {
                    log.warn("结束时间格式错误: {}", pageVO.getEndTime());
                }
            }
            
            // 按时间倒序排列
            queryWrapper.orderByDesc(CreditTransaction::getTransactionTime);
            
            // 执行分页查询
            IPage<CreditTransaction> transactionPage = this.page(page, queryWrapper);
            
            // 转换为VO
            IPage<CreditTransactionVO> resultPage = transactionPage.convert(this::convertToCreditTransactionVO);
            
            log.info("查询用户交易记录成功，用户ID: {}, 用户类型: {}-{}, 查询到{}条记录", 
                    currentUser.getId(), currentUser.getFarmType(), currentUser.getUserType(), resultPage.getRecords().size());
            
            return resultPage;
        } catch (Exception e) {
            log.error("查询当前用户交易记录失败", e);
            throw new RuntimeException("查询交易记录失败：" + e.getMessage());
        }
    }

    @Override
    public CreditTransaction getByOrderId(String id) {
        LambdaQueryWrapper<CreditTransaction> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CreditTransaction::getOrderSn, id);
        queryWrapper.eq(CreditTransaction::getDeleteFlag, 0);
        queryWrapper.last("LIMIT 1");
        return getOne(queryWrapper);
    }

    @Override
    public void recordRepayment(String id, Double totalAmount, String description) {
        // 这个方法的逻辑似乎与createRepaymentTransaction重复
        // 可以调用createRepaymentTransaction或者在这里实现具体逻辑
        try {
            CreditTransaction transaction = this.getById(id);
            if (ObjectUtil.isNotNull(transaction)) {
                transaction.setTotalRepaymentAmount(new BigDecimal(totalAmount.toString()));
                transaction.setTransactionDescription(description);
                transaction.setUpdateTime(new Date());
                this.updateById(transaction);
            }
        } catch (Exception e) {
            log.error("记录还款异常，id: {}, totalAmount: {}, description: {}", id, totalAmount, description, e);
        }
    }
    
    /**
     * 转换为CreditTransactionVO
     * @param transaction 交易记录
     * @return CreditTransactionVO
     */
    private CreditTransactionVO convertToCreditTransactionVO(CreditTransaction transaction) {
        CreditTransactionVO vo = new CreditTransactionVO();
        BeanUtils.copyProperties(transaction, vo);
        
        // 手动设置特殊字段
        vo.setOrderSn(transaction.getOrderSn());
        vo.setOrderId(transaction.getOrderSn()); // 兼容前端可能使用的orderId字段
        
        // 设置交易类型文本
        switch (transaction.getTransactionType()) {
            case 0:
                vo.setTransactionTypeText("授信消费");
                break;
            case 1:
                vo.setTransactionTypeText("还款");
                break;
            case 2:
                vo.setTransactionTypeText("额度调整");
                break;
            case 3:
                vo.setTransactionTypeText("退款");
                break;
            default:
                vo.setTransactionTypeText("未知");
                break;
        }
        
        return vo;
    }

    // ========== 新增统计和导出功能实现 ==========

    @Override
    public Map<String, Object> getTransactionStatistics(CreditTransactionPageVO pageVO) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 构建查询条件
            LambdaQueryWrapper<CreditTransaction> queryWrapper = buildQueryWrapper(pageVO);
            
            // 获取所有符合条件的交易记录
            List<CreditTransaction> transactions = this.list(queryWrapper);
            
            // 总交易数
            result.put("totalTransactions", transactions.size());
            
            // 总交易金额
            BigDecimal totalAmount = transactions.stream()
                    .map(CreditTransaction::getTransactionAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.put("totalAmount", totalAmount);
            
            // 按类型统计
            Map<Integer, List<CreditTransaction>> typeGroups = transactions.stream()
                    .collect(Collectors.groupingBy(CreditTransaction::getTransactionType));
            
            // 消费金额
            BigDecimal consumptionAmount = typeGroups.getOrDefault(0, Collections.emptyList())
                    .stream()
                    .map(CreditTransaction::getTransactionAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.put("consumptionAmount", consumptionAmount);
            result.put("consumptionCount", typeGroups.getOrDefault(0, Collections.emptyList()).size());
            
            // 还款金额
            BigDecimal repaymentAmount = typeGroups.getOrDefault(1, Collections.emptyList())
                    .stream()
                    .map(CreditTransaction::getTransactionAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.put("repaymentAmount", repaymentAmount);
            result.put("repaymentCount", typeGroups.getOrDefault(1, Collections.emptyList()).size());
            
            // 调整金额
            BigDecimal adjustmentAmount = typeGroups.getOrDefault(2, Collections.emptyList())
                    .stream()
                    .map(CreditTransaction::getTransactionAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.put("adjustmentAmount", adjustmentAmount);
            result.put("adjustmentCount", typeGroups.getOrDefault(2, Collections.emptyList()).size());
            
            // 退款金额
            BigDecimal refundAmount = typeGroups.getOrDefault(3, Collections.emptyList())
                    .stream()
                    .map(CreditTransaction::getTransactionAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.put("refundAmount", refundAmount);
            result.put("refundCount", typeGroups.getOrDefault(3, Collections.emptyList()).size());
            
            // 平均交易金额
            BigDecimal avgAmount = transactions.isEmpty() ? BigDecimal.ZERO : 
                    totalAmount.divide(new BigDecimal(transactions.size()), 2, BigDecimal.ROUND_HALF_UP);
            result.put("avgAmount", avgAmount);
            
        } catch (Exception e) {
            log.error("获取交易统计信息失败", e);
            result.put("error", "获取统计信息失败");
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getTransactionTypeStatistics(CreditTransactionPageVO pageVO) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            LambdaQueryWrapper<CreditTransaction> queryWrapper = buildQueryWrapper(pageVO);
            List<CreditTransaction> transactions = this.list(queryWrapper);
            
            Map<String, Object> consumption = new HashMap<>();
            Map<String, Object> repayment = new HashMap<>();
            Map<String, Object> adjustment = new HashMap<>();
            Map<String, Object> refund = new HashMap<>();
            
            // 按类型分组统计
            Map<Integer, List<CreditTransaction>> typeGroups = transactions.stream()
                    .collect(Collectors.groupingBy(CreditTransaction::getTransactionType));
            
            // 消费统计
            List<CreditTransaction> consumptionList = typeGroups.getOrDefault(0, Collections.emptyList());
            consumption.put("count", consumptionList.size());
            consumption.put("amount", consumptionList.stream()
                    .map(CreditTransaction::getTransactionAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            
            // 还款统计
            List<CreditTransaction> repaymentList = typeGroups.getOrDefault(1, Collections.emptyList());
            repayment.put("count", repaymentList.size());
            repayment.put("amount", repaymentList.stream()
                    .map(CreditTransaction::getTransactionAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            
            // 调整统计
            List<CreditTransaction> adjustmentList = typeGroups.getOrDefault(2, Collections.emptyList());
            adjustment.put("count", adjustmentList.size());
            adjustment.put("amount", adjustmentList.stream()
                    .map(CreditTransaction::getTransactionAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            
            // 退款统计
            List<CreditTransaction> refundList = typeGroups.getOrDefault(3, Collections.emptyList());
            refund.put("count", refundList.size());
            refund.put("amount", refundList.stream()
                    .map(CreditTransaction::getTransactionAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            
            result.put("consumption", consumption);
            result.put("repayment", repayment);
            result.put("adjustment", adjustment);
            result.put("refund", refund);
            
        } catch (Exception e) {
            log.error("获取交易类型统计失败", e);
            result.put("error", "获取统计信息失败");
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getUserTransactionStatistics(Integer userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            LambdaQueryWrapper<CreditTransaction> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CreditTransaction::getUserId, userId.toString());
            queryWrapper.eq(CreditTransaction::getDeleteFlag, 0);
            
            List<CreditTransaction> transactions = this.list(queryWrapper);
            
            // 用户总交易数
            result.put("totalTransactions", transactions.size());
            
            // 用户总交易金额
            BigDecimal totalAmount = transactions.stream()
                    .map(CreditTransaction::getTransactionAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.put("totalAmount", totalAmount);
            
            // 按类型统计
            Map<Integer, List<CreditTransaction>> typeGroups = transactions.stream()
                    .collect(Collectors.groupingBy(CreditTransaction::getTransactionType));
            
            result.put("consumptionCount", typeGroups.getOrDefault(0, Collections.emptyList()).size());
            result.put("repaymentCount", typeGroups.getOrDefault(1, Collections.emptyList()).size());
            result.put("adjustmentCount", typeGroups.getOrDefault(2, Collections.emptyList()).size());
            result.put("refundCount", typeGroups.getOrDefault(3, Collections.emptyList()).size());
            
            // 最近交易时间
            Optional<Date> lastTransactionTime = transactions.stream()
                    .map(CreditTransaction::getTransactionTime)
                    .max(Date::compareTo);
            result.put("lastTransactionTime", lastTransactionTime.orElse(null));
            
            // 获取用户信息
            User user = userService.getById(userId);
            if (user != null) {
                result.put("userName", user.getNickname());
                result.put("remainingCredit", user.getRemainingCredit());
                result.put("usedCredit", user.getUsedCredit());
                result.put("pendingRepayment", user.getPendingRepayment());
            }
            
        } catch (Exception e) {
            log.error("获取用户交易统计失败，userId: {}", userId, e);
            result.put("error", "获取用户统计信息失败");
        }
        
        return result;
    }

    @Override
    public String exportTransactionRecords(CreditTransactionPageVO pageVO) {
        try {
            // 构建查询条件
            LambdaQueryWrapper<CreditTransaction> queryWrapper = buildQueryWrapper(pageVO);
            queryWrapper.orderByDesc(CreditTransaction::getTransactionTime);
            
            // 获取所有符合条件的记录
            List<CreditTransaction> transactions = this.list(queryWrapper);
            
            // 转换为导出数据
            List<Map<String, Object>> exportData = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            for (CreditTransaction transaction : transactions) {
                Map<String, Object> row = new HashMap<>();
                row.put("交易ID", transaction.getId());
                row.put("用户ID", transaction.getUserId());
                row.put("用户名称", transaction.getUserName());
                row.put("订单号", transaction.getOrderSn());
                row.put("交易类型", getTransactionTypeText(transaction.getTransactionType()));
                row.put("交易金额", transaction.getTransactionAmount());
                row.put("交易前额度", transaction.getBeforeAvailableAmount());
                row.put("交易后额度", transaction.getAfterAvailableAmount());
                row.put("交易描述", transaction.getTransactionDescription());
                row.put("交易时间", dateFormat.format(transaction.getTransactionTime()));
                exportData.add(row);
            }
            
            // 创建Excel文件
            String fileName = "credit_transactions_" + System.currentTimeMillis() + ".xlsx";
            String filePath = System.getProperty("java.io.tmpdir") + File.separator + fileName;
            
            ExcelWriter writer = ExcelUtil.getWriter(filePath);
            writer.write(exportData, true);
            writer.close();
            
            log.info("导出授信交易记录成功，文件路径: {}", filePath);
            return filePath;
            
        } catch (Exception e) {
            log.error("导出授信交易记录失败", e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getTransactionTrend(CreditTransactionPageVO pageVO) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            LambdaQueryWrapper<CreditTransaction> queryWrapper = buildQueryWrapper(pageVO);
            List<CreditTransaction> transactions = this.list(queryWrapper);
            
            // 按日期分组统计
            Map<String, List<CreditTransaction>> dateGroups = transactions.stream()
                    .collect(Collectors.groupingBy(transaction -> {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        return sdf.format(transaction.getTransactionTime());
                    }));
            
            List<Map<String, Object>> trendData = new ArrayList<>();
            for (Map.Entry<String, List<CreditTransaction>> entry : dateGroups.entrySet()) {
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", entry.getKey());
                dayData.put("count", entry.getValue().size());
                
                BigDecimal dayAmount = entry.getValue().stream()
                        .map(CreditTransaction::getTransactionAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                dayData.put("amount", dayAmount);
                
                trendData.add(dayData);
            }
            
            // 按日期排序
            trendData.sort((a, b) -> ((String) a.get("date")).compareTo((String) b.get("date")));
            
            result.put("trendData", trendData);
            
        } catch (Exception e) {
            log.error("获取交易趋势数据失败", e);
            result.put("error", "获取趋势数据失败");
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getTransactionSummaryReport(CreditTransactionPageVO pageVO) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取基础统计
            Map<String, Object> basicStats = getTransactionStatistics(pageVO);
            result.putAll(basicStats);
            
            // 获取类型统计
            Map<String, Object> typeStats = getTransactionTypeStatistics(pageVO);
            result.put("typeStatistics", typeStats);
            
            // 获取趋势数据
            Map<String, Object> trendData = getTransactionTrend(pageVO);
            result.put("trendData", trendData);
            
            // 添加报表生成时间
            result.put("reportGenerateTime", new Date());
            
            // 添加查询条件摘要
            Map<String, Object> queryConditions = new HashMap<>();
            if (StrUtil.isNotBlank(pageVO.getUserId())) {
                queryConditions.put("userId", pageVO.getUserId());
            }
            if (ObjectUtil.isNotNull(pageVO.getTransactionType())) {
                queryConditions.put("transactionType", pageVO.getTransactionType());
            }
            if (ObjectUtil.isNotNull(pageVO.getStartTime())) {
                queryConditions.put("startTime", pageVO.getStartTime());
            }
            if (ObjectUtil.isNotNull(pageVO.getEndTime())) {
                queryConditions.put("endTime", pageVO.getEndTime());
            }
            result.put("queryConditions", queryConditions);
            
        } catch (Exception e) {
            log.error("获取交易汇总报表失败", e);
            result.put("error", "获取汇总报表失败");
        }
        
        return result;
    }

    @Override
    public String batchExportUserTransactions(List<Integer> userIds, CreditTransactionPageVO pageVO) {
        try {
            List<Map<String, Object>> allExportData = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            for (Integer userId : userIds) {
                // 为每个用户构建查询条件
                CreditTransactionPageVO userPageVO = new CreditTransactionPageVO();
                BeanUtils.copyProperties(pageVO, userPageVO);
                userPageVO.setUserId(userId.toString());
                
                LambdaQueryWrapper<CreditTransaction> queryWrapper = buildQueryWrapper(userPageVO);
                List<CreditTransaction> transactions = this.list(queryWrapper);
                
                // 转换为导出数据
                for (CreditTransaction transaction : transactions) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("交易ID", transaction.getId());
                    row.put("用户ID", transaction.getUserId());
                    row.put("用户名称", transaction.getUserName());
                    row.put("订单号", transaction.getOrderSn());
                    row.put("交易类型", getTransactionTypeText(transaction.getTransactionType()));
                    row.put("交易金额", transaction.getTransactionAmount());
                    row.put("交易前额度", transaction.getBeforeAvailableAmount());
                    row.put("交易后额度", transaction.getAfterAvailableAmount());
                    row.put("交易描述", transaction.getTransactionDescription());
                    row.put("交易时间", dateFormat.format(transaction.getTransactionTime()));
                    allExportData.add(row);
                }
            }
            
            // 创建Excel文件
            String fileName = "batch_credit_transactions_" + System.currentTimeMillis() + ".xlsx";
            String filePath = System.getProperty("java.io.tmpdir") + File.separator + fileName;
            
            ExcelWriter writer = ExcelUtil.getWriter(filePath);
            writer.write(allExportData, true);
            writer.close();
            
            log.info("批量导出用户交易记录成功，文件路径: {}", filePath);
            return filePath;
            
        } catch (Exception e) {
            log.error("批量导出用户交易记录失败", e);
            return null;
        }
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<CreditTransaction> buildQueryWrapper(CreditTransactionPageVO pageVO) {
        LambdaQueryWrapper<CreditTransaction> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CreditTransaction::getDeleteFlag, 0);
        
        if (StrUtil.isNotBlank(pageVO.getUserId())) {
            queryWrapper.eq(CreditTransaction::getUserId, pageVO.getUserId());
        }
        if (StrUtil.isNotBlank(pageVO.getOrderSn())) {
            queryWrapper.like(CreditTransaction::getOrderSn, pageVO.getOrderSn());
        }
        if (ObjectUtil.isNotNull(pageVO.getTransactionType())) {
            queryWrapper.eq(CreditTransaction::getTransactionType, pageVO.getTransactionType());
        }
        if (StrUtil.isNotBlank(pageVO.getStartTime())) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = dateFormat.parse(pageVO.getStartTime());
                queryWrapper.ge(CreditTransaction::getTransactionTime, startDate);
            } catch (Exception e) {
                // 尝试使用完整的日期时间格式
                try {
                    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date startDate = dateTimeFormat.parse(pageVO.getStartTime());
                    queryWrapper.ge(CreditTransaction::getTransactionTime, startDate);
                } catch (Exception ex) {
                    log.warn("解析开始时间失败: {}", pageVO.getStartTime());
                }
            }
        }
        if (StrUtil.isNotBlank(pageVO.getEndTime())) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date endDate = dateFormat.parse(pageVO.getEndTime());
                // 结束时间设置为当天的23:59:59
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endDate);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                queryWrapper.le(CreditTransaction::getTransactionTime, calendar.getTime());
            } catch (Exception e) {
                // 尝试使用完整的日期时间格式
                try {
                    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date endDate = dateTimeFormat.parse(pageVO.getEndTime());
                    queryWrapper.le(CreditTransaction::getTransactionTime, endDate);
                } catch (Exception ex) {
                    log.warn("解析结束时间失败: {}", pageVO.getEndTime());
                }
            }
        }
        
        return queryWrapper;
    }

    /**
     * 获取交易类型文本
     */
    private String getTransactionTypeText(Integer transactionType) {
        switch (transactionType) {
            case 0:
                return "授信消费";
            case 1:
                return "还款";
            case 2:
                return "额度调整";
            case 3:
                return "退款";
            default:
                return "未知";
        }
    }
}
