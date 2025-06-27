package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.CreditApplication;
import com.zbkj.common.model.finance.CreditApplicationAuditHistory;
import com.zbkj.common.model.finance.CreditApplicationInfo;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.finance.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

public interface CreditApplicationService extends IService<CreditApplication> {
    /**
     * 获取用户的授信总金额
     * @param userId
     * @return
     */
    Double getAvailableCreditAmount(String userId);

    /**
     * 获取用户授信余额
     * @param userId
     * @param price
     */
    void deductCreditAmount(String userId, BigDecimal price);

    /**
     * 获取已使用的授信金额
     * @param memberId
     * @return
     */
    Double getUsedCreditAmount(String memberId);

    /**
     * 审核授信额度申请
     * @param request 审核请求参数
     * @return 是否成功
     */
    Boolean auditCreditApplication(CreditApplicationAuditRequest request);

    /**
     * 分页查询授信申请列表
     * @param pageInfo 分页查询参数
     * @return 授信申请分页列表
     */
    PageInfo<CreditApplicationPage> pageInfo(CreditApplicationPageInfo pageInfo);

    /**
     * 获取授信申请详情
     * @param id 授信申请ID
     * @return 授信申请详情
     */
    CreditApplicationPage get(String id);

    /**
     * 获取当前登录人的授信额度
     * @return 当前登录人的授信额度信息
     */
    CreditApplicationInfo getCreditAmount();

    /**
     * 获取授信申请统计信息
     * @return 统计信息
     */
    CreditApplicationStatistics getStatistics();
    /**
     * 创建资产评估
     * @param request 资产评估请求参数
     * @return 评估ID
     */
    String createAssetAssessment(AssetAssessmentRequestVO request);

    /**
     * 获取资产评估详情
     * @param assessmentId 评估ID
     * @return 评估详情
     */
    AssetAssessmentDetailVO getAssetAssessmentDetail(String assessmentId);

    /**
     * 分页查询资产评估记录
     * @param pageRequest 分页请求参数
     * @return 评估记录分页列表
     */
    IPage<AssetAssessmentPageVO> pageAssetAssessment(AssetAssessmentPageRequest pageRequest);

    /**
     * 获取当前用户最新的资产评估记录
     * @return 最新的资产评估记录
     */
    AssetAssessmentPageVO getLatestAssetAssessment();

    /**
     * 申请授信额度（基于资产评估）
     * @param request 申请请求参数
     * @return 申请ID
     */
    String applyCreditAmount(CreditAmountApplicationVO request);

    /**
     * 检查用户是否可以申请额度
     * @return 申请状态信息
     */
    CreditApplicationStatusVO checkUserCanApply();
    /**
     * 合作社审核员工授信申请
     * @param request 审核参数
     * @return 是否成功
     */
    Boolean cooperativeAuditEmployeeCredit(CooperativeCreditAuditVO request);

    CreditApplicationPage getByCurrentUser();

    List<CreditApplicationAuditHistory> getAuditHistory(AuditStatusRequest auditStatusRequest);

    PageInfo<CreditApplicationPage> pageAdminInfo(@Valid CreditApplicationPageInfo pageInfo);
}
