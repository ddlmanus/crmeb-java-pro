package com.zbkj.service.service;

import com.zbkj.common.model.finance.FarmInstitution;
import com.zbkj.common.model.user.User;
import com.zbkj.common.vo.finance.UserCreditInfoVO;

import java.math.BigDecimal;

/**
 * 用户授信服务接口
 * 统一管理所有用户类型的授信相关操作
 */
public interface UserCreditService {

    /**
     * 初始化用户授信信息（新员工创建时调用）
     * @param userId 用户ID
     * @param farmInstitution 养殖场信息
     * @return 操作结果
     */
    Boolean initUserCreditInfo(Integer userId, FarmInstitution farmInstitution);

    /**
     * 分配授信额度（合作社给员工分配，平台给合作社分配）
     * @param fromUserId 分配方用户ID
     * @param toUserId 接收方用户ID
     * @param amount 分配金额
     * @param auditUserId 审核人ID
     * @param remark 备注
     * @return 操作结果
     */
    Boolean allocateCredit(Integer fromUserId, Integer toUserId, BigDecimal amount, Integer auditUserId, String remark);

    /**
     * 使用授信额度
     * @param userId 用户ID
     * @param amount 使用金额
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 操作结果
     */
    Boolean useCredit(Integer userId, BigDecimal amount, String businessType, String businessId);

    /**
     * 还款
     * @param userId 用户ID
     * @param amount 还款金额
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 操作结果
     */
    Boolean repayCredit(Integer userId, BigDecimal amount, String businessType, String businessId);

    /**
     * 获取用户授信信息
     * @param userId 用户ID
     * @return 授信信息
     */
    UserCreditInfoVO getUserCreditInfo(Integer userId);

    /**
     * 冻结用户授信
     * @param userId 用户ID
     * @param reason 冻结原因
     * @param operatorId 操作人ID
     * @return 操作结果
     */
    Boolean freezeCredit(Integer userId, String reason, Integer operatorId);

    /**
     * 解冻用户授信
     * @param userId 用户ID
     * @param reason 解冻原因
     * @param operatorId 操作人ID
     * @return 操作结果
     */
    Boolean unfreezeCredit(Integer userId, String reason, Integer operatorId);

    /**
     * 检查用户是否有足够的授信额度
     * @param userId 用户ID
     * @param amount 需要的金额
     * @return 是否足够
     */
    Boolean checkCreditSufficient(Integer userId, BigDecimal amount);

    /**
     * 获取用户的可用授信额度
     * @param userId 用户ID
     * @return 可用额度
     */
    BigDecimal getAvailableCredit(Integer userId);

    /**
     * 更新用户的养殖信息（从资产评估数据同步）
     * @param userId 用户ID
     * @return 操作结果
     */
    Boolean updateUserBreedingInfo(Integer userId);
} 