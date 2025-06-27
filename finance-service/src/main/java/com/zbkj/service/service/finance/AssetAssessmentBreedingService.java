package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.AssetAssessmentBreeding;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.vo.finance.AssetAssessmentBreedingSearchVO;
import com.zbkj.common.vo.finance.AssetAssessmentBreedingStatisticsVO;

import java.util.List;

/**
 * 资产评估养殖品种Service接口
 */
public interface AssetAssessmentBreedingService extends IService<AssetAssessmentBreeding> {

    /**
     * 分页查询资产评估养殖品种
     * @param pageParamRequest 分页参数
     * @param searchVO 搜索条件
     * @return 分页结果
     */
    PageInfo<AssetAssessmentBreeding> getPage(PageParamRequest pageParamRequest, AssetAssessmentBreedingSearchVO searchVO);

    /**
     * 根据资产评估ID获取养殖品种列表
     * @param assessmentId 资产评估ID
     * @return 养殖品种列表
     */
    List<AssetAssessmentBreeding> getByAssessmentId(String assessmentId);

    /**
     * 根据授信申请ID获取养殖品种列表
     * @param applicationId 授信申请ID
     * @return 养殖品种列表
     */
    List<AssetAssessmentBreeding> getByCreditApplicationId(String applicationId);

    /**
     * 获取统计信息
     * @param searchVO 搜索条件
     * @return 统计信息
     */
    AssetAssessmentBreedingStatisticsVO getStatistics(AssetAssessmentBreedingSearchVO searchVO);
} 