package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.finance.RepaymentRecord;
import com.zbkj.common.vo.finance.RepaymentAuditVO;
import com.zbkj.common.vo.finance.RepaymentCreateVO;
import com.zbkj.common.vo.finance.RepaymentPageVO;
import com.zbkj.common.vo.finance.RepaymentRecordVO;
import com.zbkj.common.vo.finance.RepaymentStatisticsVO;
import java.util.List;

/**
 * 还款记录服务接口
 */
public interface RepaymentRecordService extends IService<RepaymentRecord> {

    /**
     * 检查是否有待审核的还款记录
     * @param creditOrderNo 授信支付订单号
     * @return 是否存在待审核记录
     */
    Boolean hasPendingRepaymentRecord(String creditOrderNo);
    
    /**
     * 审核还款记录
     * @param repaymentAuditVO 还款审核VO
     * @return 是否成功
     */
    Boolean auditRepayment(RepaymentAuditVO repaymentAuditVO);
    
    /**
     * 分页查询还款记录
     * @param repaymentPageVO 分页查询参数
     * @return 分页结果
     */
    IPage<RepaymentRecordVO> pageRepayment(RepaymentPageVO repaymentPageVO);
    
    /**
     * 获取还款记录详情
     * @param id 还款记录ID
     * @return 还款记录详情
     */
    RepaymentRecordVO getRepaymentDetail(String id);
    
    /**
     * 获取当前用户的还款记录
     * @param repaymentPageVO 分页查询参数
     * @return 分页结果
     */
    IPage<RepaymentRecordVO> getCurrentUserRepayments(RepaymentPageVO repaymentPageVO);

    // ========== 后台管理方法 ==========
    
    /**
     * 后台分页查询还款记录列表
     * @param pageVO 分页查询参数
     * @return 分页结果
     */
    IPage<RepaymentRecordVO> adminPageQuery(RepaymentPageVO pageVO);
    
    /**
     * 批量审核还款记录
     * @param ids 还款记录ID列表
     * @param status 审核状态
     * @param auditRemark 审核备注
     * @return 是否成功
     */
    Boolean batchAudit(List<String> ids, Integer status, String auditRemark);
    
    /**
     * 获取还款统计信息
     * @param pageVO 查询参数
     * @return 统计信息
     */
    RepaymentStatisticsVO getRepaymentStatistics(RepaymentPageVO pageVO);
    
    /**
     * 导出还款记录数据
     * @param pageVO 查询参数
     * @param response HTTP响应对象
     * @throws Exception 导出异常
     */
    void exportRepaymentData(RepaymentPageVO pageVO, javax.servlet.http.HttpServletResponse response) throws Exception;

    /**
     * 根据授信订单号查询还款记录
     * @param creditOrderNo 授信订单号
     * @return 还款记录列表
     */
    List<RepaymentRecordVO> getRepaymentsByCreditOrder(String creditOrderNo);
} 