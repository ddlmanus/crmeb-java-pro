package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.CreditTransaction;
import com.zbkj.common.vo.finance.CreditTransactionPageVO;
import com.zbkj.common.vo.finance.CreditTransactionVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 授信交易记录服务接口
 */
public interface CreditTransactionService extends IService<CreditTransaction> {
    
    /**
     * 创建授信消费交易记录
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param amount 交易金额
     * @return 是否成功
     */
    Boolean createConsumptionTransaction(Integer userId, String orderId, BigDecimal amount);
    
    /**
     * 创建还款交易记录
     * @param userId 用户ID
     * @param repaymentId 还款记录ID
     * @param amount 还款金额
     * @return 是否成功
     */
    Boolean createRepaymentTransaction(Integer userId, String repaymentId, BigDecimal amount);
    
    /**
     * 创建退款交易记录
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param amount 退款金额
     * @return 是否成功
     */
    Boolean createRefundTransaction(Integer userId, String orderId, BigDecimal amount);
    
    /**
     * 创建授信额度调整交易记录
     * @param userId 用户ID
     * @param amount 调整金额
     * @param description 调整描述
     * @return 是否成功
     */
    Boolean createAdjustmentTransaction(Integer userId, BigDecimal amount, String description);
    
    /**
     * 分页查询授信交易记录
     * @param pageVO 分页查询参数
     * @return 分页结果
     */
    PageInfo<CreditTransactionVO> pageTransaction(CreditTransactionPageVO pageVO);
    
    /**
     * 获取授信交易记录详情
     * @param id 交易记录ID
     * @return 交易记录详情
     */
    CreditTransactionVO getTransactionDetail(String id);
    
    /**
     * 获取当前用户的授信交易记录
     * @param pageVO 分页查询参数
     * @return 分页结果
     */
    IPage<CreditTransactionVO> getCurrentUserTransactions(CreditTransactionPageVO pageVO);

    /**
     * 通过订单号获取交易记录
     * @param sn
     * @return
     */
    CreditTransaction getByOrderId(String sn);

    /**
     * 还款记录
     * @param id
     * @param totalAmount
     * @param s
     */
    void recordRepayment(String id, Double totalAmount, String s);

    // ========== 新增统计和导出功能 ==========

    /**
     * 获取交易统计信息
     * @param pageVO 查询条件
     * @return 统计信息
     */
    Map<String, Object> getTransactionStatistics(CreditTransactionPageVO pageVO);

    /**
     * 按交易类型统计
     * @param pageVO 查询条件
     * @return 按类型统计结果
     */
    Map<String, Object> getTransactionTypeStatistics(CreditTransactionPageVO pageVO);

    /**
     * 获取用户交易统计
     * @param userId 用户ID
     * @return 用户统计信息
     */
    Map<String, Object> getUserTransactionStatistics(Integer userId);

    /**
     * 导出交易记录
     * @param pageVO 查询条件
     * @return 导出文件路径
     */
    String exportTransactionRecords(CreditTransactionPageVO pageVO);

    /**
     * 获取交易趋势数据
     * @param pageVO 查询条件
     * @return 趋势数据
     */
    Map<String, Object> getTransactionTrend(CreditTransactionPageVO pageVO);

    /**
     * 获取交易汇总报表
     * @param pageVO 查询条件
     * @return 汇总报表数据
     */
    Map<String, Object> getTransactionSummaryReport(CreditTransactionPageVO pageVO);

    /**
     * 批量导出用户交易记录
     * @param userIds 用户ID列表
     * @param pageVO 查询条件
     * @return 导出文件路径
     */
    String batchExportUserTransactions(List<Integer> userIds, CreditTransactionPageVO pageVO);
}
