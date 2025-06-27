package com.zbkj.service.service.impl.finance;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.AssetAssessmentBreeding;
import com.zbkj.common.model.finance.CreditApplication;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.vo.finance.AssetAssessmentBreedingSearchVO;
import com.zbkj.common.vo.finance.AssetAssessmentBreedingStatisticsVO;
import com.zbkj.service.dao.finance.AssetAssessmentBreedingDao;
import com.zbkj.service.dao.finance.CreditApplicationDao;
import com.zbkj.service.service.finance.AssetAssessmentBreedingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 资产评估养殖品种Service实现类
 */
@Slf4j
@Service
public class AssetAssessmentBreedingServiceImpl extends ServiceImpl<AssetAssessmentBreedingDao, AssetAssessmentBreeding> implements AssetAssessmentBreedingService {

    @Autowired
    private CreditApplicationDao creditApplicationDao;

    @Override
    public PageInfo<AssetAssessmentBreeding> getPage(PageParamRequest pageParamRequest, AssetAssessmentBreedingSearchVO searchVO) {
        Page<AssetAssessmentBreeding> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        
        LambdaQueryWrapper<AssetAssessmentBreeding> lqw = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (searchVO != null && StrUtil.isNotBlank(searchVO.getKeywords())) {
            String keywords = searchVO.getKeywords();
            lqw.and(wrapper -> wrapper
                .like(AssetAssessmentBreeding::getBreedName, keywords)
                .or()
                .like(AssetAssessmentBreeding::getFarmName, keywords)
                .or()
                .like(AssetAssessmentBreeding::getBreedType, keywords)
            );
        }
        
        // 养殖场编码
        if (searchVO != null && StrUtil.isNotBlank(searchVO.getFarmCode())) {
            lqw.eq(AssetAssessmentBreeding::getFarmCode, searchVO.getFarmCode());
        }
        
        // 养殖场名称
        if (searchVO != null && StrUtil.isNotBlank(searchVO.getFarmName())) {
            lqw.like(AssetAssessmentBreeding::getFarmName, searchVO.getFarmName());
        }
        
        // 品种名称
        if (searchVO != null && StrUtil.isNotBlank(searchVO.getBreedName())) {
            lqw.like(AssetAssessmentBreeding::getBreedName, searchVO.getBreedName());
        }
        
        // 品种类型
        if (searchVO != null && StrUtil.isNotBlank(searchVO.getBreedType())) {
            lqw.like(AssetAssessmentBreeding::getBreedType, searchVO.getBreedType());
        }
        
        // 资产评估ID
        if (searchVO != null && StrUtil.isNotBlank(searchVO.getAssessmentId())) {
            lqw.eq(AssetAssessmentBreeding::getAssessmentId, searchVO.getAssessmentId());
        }
        
        // 时间范围
        if (searchVO != null && StrUtil.isNotBlank(searchVO.getDateLimit())) {
            String[] dates = searchVO.getDateLimit().split(" - ");
            if (dates.length == 2) {
                String startDate = dates[0] + " 00:00:00";
                String endDate = dates[1] + " 23:59:59";
                lqw.ge(AssetAssessmentBreeding::getCreateTime, startDate)
                   .le(AssetAssessmentBreeding::getCreateTime, endDate);
            }
        }
        
        lqw.orderByDesc(AssetAssessmentBreeding::getCreateTime);
        
        List<AssetAssessmentBreeding> list = this.list(lqw);
        return new PageInfo<>(list);
    }

    @Override
    public List<AssetAssessmentBreeding> getByAssessmentId(String assessmentId) {
        if (StrUtil.isBlank(assessmentId)) {
            return null;
        }
        
        LambdaQueryWrapper<AssetAssessmentBreeding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetAssessmentBreeding::getAssessmentId, assessmentId);
        wrapper.orderByDesc(AssetAssessmentBreeding::getCreateTime);
        
        return this.list(wrapper);
    }

    @Override
    public List<AssetAssessmentBreeding> getByCreditApplicationId(String applicationId) {
        if (StrUtil.isBlank(applicationId)) {
            return null;
        }
        
        // 根据授信申请ID查找对应的资产评估ID
        CreditApplication creditApplication = creditApplicationDao.selectById(applicationId);
        if (creditApplication == null || StrUtil.isBlank(creditApplication.getAssessmentId())) {
            return null;
        }
        
        return getByAssessmentId(creditApplication.getAssessmentId());
    }

    @Override
    public AssetAssessmentBreedingStatisticsVO getStatistics(AssetAssessmentBreedingSearchVO searchVO) {
        LambdaQueryWrapper<AssetAssessmentBreeding> lqw = buildQueryWrapper(searchVO);
        
        List<AssetAssessmentBreeding> allData = this.list(lqw);
        
        AssetAssessmentBreedingStatisticsVO statistics = new AssetAssessmentBreedingStatisticsVO();
        
        if (allData.isEmpty()) {
            // 如果没有数据，返回空统计
            statistics.setTotalCount(0L);
            statistics.setTotalStock(0L);
            statistics.setTotalValue(BigDecimal.ZERO);
            statistics.setAveragePrice(BigDecimal.ZERO);
            statistics.setFarmCount(0L);
            statistics.setBreedCount(0L);
            statistics.setMaxPrice(BigDecimal.ZERO);
            statistics.setMinPrice(BigDecimal.ZERO);
            return statistics;
        }
        
        // 计算总记录数
        statistics.setTotalCount((long) allData.size());
        
        // 计算总存栏数量
        long totalStock = allData.stream()
            .mapToLong(item -> item.getStockQuantity() != null ? item.getStockQuantity() : 0L)
            .sum();
        statistics.setTotalStock(totalStock);
        
        // 计算总评估价值
        BigDecimal totalValue = allData.stream()
            .map(item -> item.getAssessmentTotalPrice() != null ? item.getAssessmentTotalPrice() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.setTotalValue(totalValue);
        
        // 计算养殖场数量（去重）
        long farmCount = allData.stream()
            .map(AssetAssessmentBreeding::getFarmCode)
            .distinct()
            .count();
        statistics.setFarmCount(farmCount);
        
        // 计算品种数量（去重，按品种名称+类型）
        long breedCount = allData.stream()
            .map(item -> item.getBreedName() + "_" + item.getBreedType())
            .distinct()
            .count();
        statistics.setBreedCount(breedCount);
        
        // 计算单价相关统计（过滤掉null和0的值）
        List<BigDecimal> validPrices = allData.stream()
            .map(AssetAssessmentBreeding::getAssessmentPrice)
            .filter(price -> price != null && price.compareTo(BigDecimal.ZERO) > 0)
            .collect(java.util.stream.Collectors.toList());
        
        if (!validPrices.isEmpty()) {
            // 平均单价
            BigDecimal avgPrice = validPrices.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(validPrices.size()), 2, RoundingMode.HALF_UP);
            statistics.setAveragePrice(avgPrice);
            
            // 最高单价
            BigDecimal maxPrice = validPrices.stream()
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
            statistics.setMaxPrice(maxPrice);
            
            // 最低单价
            BigDecimal minPrice = validPrices.stream()
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
            statistics.setMinPrice(minPrice);
        } else {
            statistics.setAveragePrice(BigDecimal.ZERO);
            statistics.setMaxPrice(BigDecimal.ZERO);
            statistics.setMinPrice(BigDecimal.ZERO);
        }
        
        return statistics;
    }
    
    /**
     * 构建查询条件
     * @param searchVO 搜索条件
     * @return 查询包装器
     */
    private LambdaQueryWrapper<AssetAssessmentBreeding> buildQueryWrapper(AssetAssessmentBreedingSearchVO searchVO) {
        LambdaQueryWrapper<AssetAssessmentBreeding> lqw = new LambdaQueryWrapper<>();
        
        if (searchVO == null) {
            return lqw;
        }
        
        // 关键词搜索
        if (StrUtil.isNotBlank(searchVO.getKeywords())) {
            String keywords = searchVO.getKeywords();
            lqw.and(wrapper -> wrapper
                .like(AssetAssessmentBreeding::getBreedName, keywords)
                .or()
                .like(AssetAssessmentBreeding::getFarmName, keywords)
                .or()
                .like(AssetAssessmentBreeding::getBreedType, keywords)
            );
        }
        
        // 养殖场编码
        if (StrUtil.isNotBlank(searchVO.getFarmCode())) {
            lqw.eq(AssetAssessmentBreeding::getFarmCode, searchVO.getFarmCode());
        }
        
        // 养殖场名称
        if (StrUtil.isNotBlank(searchVO.getFarmName())) {
            lqw.like(AssetAssessmentBreeding::getFarmName, searchVO.getFarmName());
        }
        
        // 品种名称
        if (StrUtil.isNotBlank(searchVO.getBreedName())) {
            lqw.like(AssetAssessmentBreeding::getBreedName, searchVO.getBreedName());
        }
        
        // 品种类型
        if (StrUtil.isNotBlank(searchVO.getBreedType())) {
            lqw.like(AssetAssessmentBreeding::getBreedType, searchVO.getBreedType());
        }
        
        // 资产评估ID
        if (StrUtil.isNotBlank(searchVO.getAssessmentId())) {
            lqw.eq(AssetAssessmentBreeding::getAssessmentId, searchVO.getAssessmentId());
        }
        
        // 时间范围
        if (StrUtil.isNotBlank(searchVO.getDateLimit())) {
            String[] dates = searchVO.getDateLimit().split(" - ");
            if (dates.length == 2) {
                String startDate = dates[0] + " 00:00:00";
                String endDate = dates[1] + " 23:59:59";
                lqw.ge(AssetAssessmentBreeding::getCreateTime, startDate)
                   .le(AssetAssessmentBreeding::getCreateTime, endDate);
            }
        }
        
        return lqw;
    }
} 