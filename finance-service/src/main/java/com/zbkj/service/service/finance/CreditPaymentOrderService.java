package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.finance.CreditPaymentOrder;
import com.zbkj.common.model.finance.RepaymentRecord;
import com.zbkj.common.vo.finance.CreditBillPageVO;
import com.zbkj.common.vo.finance.CreditBillStatisticsVO;
import com.zbkj.common.vo.finance.CreditBillVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 授信支付订单服务接口
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
public interface CreditPaymentOrderService extends IService<CreditPaymentOrder> {
    
    /**
     * 创建授信支付订单
     * @param userId 用户ID
     * @param originalOrderNo 原始订单号
     * @param paymentAmount 支付金额
     * @return 授信支付订单
     */
    CreditPaymentOrder createCreditPaymentOrder(Integer userId, String originalOrderNo, BigDecimal paymentAmount);
    
    /**
     * 根据原始订单号获取授信支付订单
     * @param originalOrderNo 原始订单号
     * @return 授信支付订单
     */
    CreditPaymentOrder getByOriginalOrderNo(String originalOrderNo);
    
    /**
     * 根据授信支付订单号获取订单
     * @param creditOrderNo 授信支付订单号
     * @return 授信支付订单
     */
    CreditPaymentOrder getByCreditOrderNo(String creditOrderNo);
    
    /**
     * 获取用户的待还款订单列表
     * @param userId 用户ID
     * @return 待还款订单列表
     */
    List<CreditPaymentOrder> getUserPendingRepaymentOrders(Integer userId);
    
    /**
     * 创建还款申请记录（用户发起还款）
     * @param creditOrderNo 授信支付订单号
     * @param repaymentAmount 还款金额
     * @param repaymentProof 还款凭证
     * @return 还款申请是否成功
     */
    Boolean processRepayment(String creditOrderNo, BigDecimal repaymentAmount,  String repaymentProof);
    
    /**
     * 审核通过后的实际还款处理（恢复额度、更新订单状态）
     * @param repaymentRecord 还款记录
     * @return 处理是否成功
     */
    Boolean processApprovedRepayment(RepaymentRecord repaymentRecord);

    // ========== 后台管理新增方法 ==========

    /**
     * 后台分页查询授信账单列表
     * @param pageVO 分页查询参数
     * @return 分页结果
     */
    IPage<CreditBillVO> adminPageQuery(CreditBillPageVO pageVO);

    /**
     * 获取授信账单详情
     * @param id 账单ID
     * @return 账单详情
     */
    CreditBillVO getBillDetail(String id);

    /**
     * 标记账单状态
     * @param id 账单ID
     * @param status 状态
     * @param remark 备注
     * @return 是否成功
     */
    Boolean markBillStatus(String id, Integer status, String remark);

    /**
     * 手动还款（管理员代操作）
     * @param id 账单ID
     * @param repaymentAmount 还款金额
     * @param remark 备注
     * @return 是否成功
     */
    Boolean manualRepayment(String id, String repaymentAmount, String remark);

    /**
     * 获取逾期账单列表
     * @param pageVO 分页查询参数
     * @return 分页结果
     */
    IPage<CreditBillVO> getOverdueBills(CreditBillPageVO pageVO);

    /**
     * 获取授信账单统计信息
     * @param pageVO 查询条件
     * @return 统计信息
     */
    CreditBillStatisticsVO getBillStatistics(CreditBillPageVO pageVO);

    /**
     * 导出授信账单数据
     * @param pageVO 查询条件
     * @param response HTTP响应对象
     */
    void exportBillData(CreditBillPageVO pageVO, javax.servlet.http.HttpServletResponse response) throws Exception;

    /**
     * 批量操作
     * @param ids 账单ID列表
     * @param action 操作类型
     * @param remark 备注
     * @return 是否成功
     */
    Boolean batchOperation(List<String> ids, String action, String remark);

    /**
     * 生成账单报表
     * @param pageVO 查询条件
     * @return 报表结果
     */
    String generateBillReport(CreditBillPageVO pageVO);
} 