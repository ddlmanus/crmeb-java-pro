package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.CreditPaymentOrder;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.finance.*;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 社员互助金服务接口
 */
public interface MutualAidService {

    /**
     * 分页查询社员列表
     * 如果是合作社，查询合作社下的员工信息
     * 如果是养殖户，只查询自己的信息
     */
    IPage<MutualAidMemberVO> getMembersList(MutualAidMemberPageRequest pageRequest);

    /**
     * 设置授信条件（利率和期限）
     */
    Boolean setCreditTerm(CreditTermSettingVO request);

    /**
     * 根据用户ID查询担保人列表
     */
    List<GuarantorVO> getGuarantorsByUserId(Integer userId);

    /**
     * 添加担保人
     */
    Boolean addGuarantor(EmployeeGuarantorRequestVO request);

    /**
     * 根据用户ID分页查询还款记录
     */
    IPage<RepaymentRecordVO> getRepaymentRecords(MutualAidMemberPageRequest pageRequest);

    /**
     * 根据用户ID分页查询交易记录
     */
    IPage<TransactionRecordVO> getTransactionRecords(MutualAidMemberPageRequest pageRequest);

    /**
     * 授信还款
     * @param creditOrderNo
     * @param repaymentAmount
     * @param repaymentProof
     * @return
     */
    Boolean processRepayment(@NotBlank(message = "授信支付订单号不能为空") String creditOrderNo, @NotNull(message = "还款金额不能为空") @DecimalMin(value = "0.01", message = "还款金额必须大于0") BigDecimal repaymentAmount, String repaymentProof);

    /**
     * 获取还款详情
     * @param id
     * @return
     */
    RepaymentRecordVO getRepaymentDetail(String id);

    Boolean auditRepayment(RepaymentAuditVO repaymentAuditVO);

    PageInfo<CreditPaymentOrder> pageRepaymentOrder(CreditOrderRequest repaymentPageVO);
}