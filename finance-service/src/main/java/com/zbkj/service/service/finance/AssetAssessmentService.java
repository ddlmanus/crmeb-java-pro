package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.AssetAssessment;
import com.zbkj.common.vo.finance.*;

/**
 * 资产评估服务接口
 */
public interface AssetAssessmentService extends IService<AssetAssessment> {
    
    /**
     * 创建或更新资产评估
     * @param request 资产评估请求参数
     * @return 评估ID
     */
    String createOrUpdateAssessment(AssetAssessmentRequestVO request);
    
    /**
     * 获取资产评估详情
     * @param assessmentId 评估ID
     * @return 评估详情
     */
    AssetAssessmentDetailVO getAssessmentDetail(String assessmentId);
    
    /**
     * 分页查询资产评估记录
     * @param pageRequest 分页请求参数
     * @return 评估记录分页列表
     */
    IPage<AssetAssessmentPageVO> pageAssessment(AssetAssessmentPageRequest pageRequest);
    
    /**
     * 管理员分页查询资产评估记录
     * @param searchVO 搜索条件
     * @return 评估记录分页列表
     */
    PageInfo<AssetAssessmentPageVO> getPageForAdmin(AssetAssessmentSearchVO searchVO);
    
    /**
     * 根据授信申请ID获取资产评估信息
     * @param applicationId 授信申请ID
     * @return 评估详情
     */
    AssetAssessmentDetailVO getAssessmentByCreditApplication(String applicationId);
    
    /**
     * 获取资产评估统计信息
     * @return 统计数据
     */
    Object getAssessmentStatistics();
    
    /**
     * 检查用户是否有可用的资产评估
     * @param userId 用户ID
     * @return 评估ID，如果没有返回null
     */
    String getAvailableAssessmentByUser(Integer userId);
    
    /**
     * 标记资产评估为已使用
     * @param assessmentId 评估ID
     */
    void markAssessmentAsUsed(String assessmentId);

    /**
     * 获取当前用户最新的资产评估记录
     * @return 最新的资产评估记录
     */
    AssetAssessmentPageVO getLatestAssessmentByCurrentUser();
} 