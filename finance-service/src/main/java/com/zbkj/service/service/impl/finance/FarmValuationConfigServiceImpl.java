package com.zbkj.service.service.impl.finance;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.finance.FarmValuationConfig;
import com.zbkj.common.model.finance.BreedingProduct;
import com.zbkj.common.model.finance.FarmBreedType;
import com.zbkj.common.model.finance.FarmInstitution;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.vo.finance.FarmValuationConfigSearchVO;
import com.zbkj.common.vo.finance.FarmValuationConfigVO;
import com.zbkj.service.dao.finance.FarmValuationConfigDao;
import com.zbkj.service.dao.finance.BreedingProductDao;
import com.zbkj.service.dao.finance.FarmBreedTypeDao;
import com.zbkj.service.dao.finance.FarmInstitutionDao;
import com.zbkj.service.service.finance.FarmValuationConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 养殖场评估价值配置服务实现
 */
@Slf4j
@Service
public class FarmValuationConfigServiceImpl extends ServiceImpl<FarmValuationConfigDao, FarmValuationConfig> implements FarmValuationConfigService {
    
    @Autowired
    private BreedingProductDao breedingProductDao;
    
    @Autowired
    private FarmBreedTypeDao farmBreedTypeDao;
    
    @Autowired
    private FarmInstitutionDao farmInstitutionDao;

    @Override
    public PageInfo<FarmValuationConfig> getPage(PageParamRequest pageParamRequest, FarmValuationConfigSearchVO searchVO) {
        Page<FarmValuationConfig> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<FarmValuationConfig> lqw = new LambdaQueryWrapper<>();
        
        if (searchVO != null) {
            if (StrUtil.isNotBlank(searchVO.getBreedingType())) {
                lqw.like(FarmValuationConfig::getBreedingType, searchVO.getBreedingType());
            }
            if (StrUtil.isNotBlank(searchVO.getBreedingCode())) {
                lqw.like(FarmValuationConfig::getBreedingCode, searchVO.getBreedingCode());
            }
            if (searchVO.getStatus() != null) {
                lqw.eq(FarmValuationConfig::getStatus, searchVO.getStatus());
            }
            if (StrUtil.isNotBlank(searchVO.getKeywords())) {
                lqw.and(wrapper -> wrapper
                    .like(FarmValuationConfig::getBreedingName, searchVO.getKeywords())
                    .or().like(FarmValuationConfig::getBreedingType, searchVO.getKeywords())
                    .or().like(FarmValuationConfig::getBreedingCode, searchVO.getKeywords())
                );
            }
            if (StrUtil.isNotBlank(searchVO.getDateLimit())) {
                String[] dates = searchVO.getDateLimit().split(" - ");
                if (dates.length == 2) {
                    lqw.between(FarmValuationConfig::getCreateTime, dates[0] + " 00:00:00", dates[1] + " 23:59:59");
                }
            }
        }
        
        lqw.eq(FarmValuationConfig::getDeleteFlag, 0);
        lqw.orderByDesc(FarmValuationConfig::getCreateTime);
        List<FarmValuationConfig> configList = this.list(lqw);
        return CommonPage.copyPageInfo(page, configList);
    }

    @Override
    public Boolean add(FarmValuationConfigVO configVO) {
        // 检查养殖场+品种名称+类型组合是否已存在
        if (checkFarmBreedingCombinationExists(configVO.getFarmCode(), configVO.getBreedingName(), configVO.getBreedingType(), null)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "该养殖场的养殖品种和类型组合配置已存在");
        }
        
        FarmValuationConfig config = new FarmValuationConfig();
        BeanUtils.copyProperties(configVO, config);
        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());
        config.setDeleteFlag(0);
        config.setStatus(1); // 默认启用
        
        return this.save(config);
    }

    @Override
    public Boolean edit(FarmValuationConfigVO configVO) {
        if (configVO.getId() == null) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "配置ID不能为空");
        }
        
        FarmValuationConfig existingConfig = getConfigById(configVO.getId());
        if (existingConfig == null) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "配置不存在");
        }
        
        // 检查养殖场+品种名称+类型组合是否已存在（排除当前记录）
        if ((!existingConfig.getFarmCode().equals(configVO.getFarmCode()) ||
             !existingConfig.getBreedingName().equals(configVO.getBreedingName()) || 
             !existingConfig.getBreedingType().equals(configVO.getBreedingType())) && 
            checkFarmBreedingCombinationExists(configVO.getFarmCode(), configVO.getBreedingName(), configVO.getBreedingType(), configVO.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "该养殖场的养殖品种和类型组合配置已存在");
        }
        
        BeanUtils.copyProperties(configVO, existingConfig);
        existingConfig.setUpdateTime(new Date());
        
        return this.updateById(existingConfig);
    }

    @Override
    public Boolean delete(Integer id) {
        FarmValuationConfig config = getConfigById(id);
        if (config == null) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "配置不存在");
        }
        
        config.setDeleteFlag(1);
        config.setUpdateTime(new Date());
        
        return this.updateById(config);
    }

    @Override
    public FarmValuationConfig getConfigById(Integer id) {
        LambdaQueryWrapper<FarmValuationConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FarmValuationConfig::getId, id);
        wrapper.eq(FarmValuationConfig::getDeleteFlag, 0);
        return this.getOne(wrapper);
    }

    @Override
    public List<FarmValuationConfig> getEnabledConfigs() {
        LambdaQueryWrapper<FarmValuationConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FarmValuationConfig::getStatus, 1);
        wrapper.eq(FarmValuationConfig::getDeleteFlag, 0);
        wrapper.orderByDesc(FarmValuationConfig::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public Boolean updateStatus(Integer id, Integer status) {
        FarmValuationConfig config = getConfigById(id);
        if (config == null) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "配置不存在");
        }
        
        config.setStatus(status);
        config.setUpdateTime(new Date());
        
        return this.updateById(config);
    }

    @Override
    public FarmValuationConfig getByBreedingType(String farmCode, String breedName, String breedType) {
        LambdaQueryWrapper<FarmValuationConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FarmValuationConfig::getFarmCode, farmCode);
        wrapper.eq(FarmValuationConfig::getBreedingName, breedName);
        wrapper.eq(FarmValuationConfig::getBreedingType, breedType);
        return this.getOne(wrapper);
    }

    /**
     * 检查养殖场+品种名称+类型组合是否已存在
     * @param farmCode 养殖场编码
     * @param breedingName 养殖品种名称
     * @param breedingType 养殖品种类型
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    private Boolean checkFarmBreedingCombinationExists(String farmCode, String breedingName, String breedingType, Integer excludeId) {
        LambdaQueryWrapper<FarmValuationConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FarmValuationConfig::getFarmCode, farmCode);
        wrapper.eq(FarmValuationConfig::getBreedingName, breedingName);
        wrapper.eq(FarmValuationConfig::getBreedingType, breedingType);
        wrapper.eq(FarmValuationConfig::getDeleteFlag, 0);
        if (excludeId != null) {
            wrapper.ne(FarmValuationConfig::getId, excludeId);
        }
        return this.count(wrapper) > 0;
    }
    
    @Override
    public List<BreedingProduct> getAllBreedingProducts() {
        LambdaQueryWrapper<BreedingProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BreedingProduct::getDeleteFlag, 0);
        wrapper.orderByDesc(BreedingProduct::getCreateTime);
        return breedingProductDao.selectList(wrapper);
    }
    
    @Override
    public List<FarmBreedType> getBreedTypesByName(String breedName) {
        if (StrUtil.isBlank(breedName)) {
            return null;
        }
        LambdaQueryWrapper<FarmBreedType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FarmBreedType::getBreedName, breedName);
        wrapper.eq(FarmBreedType::getDeleteFlag, 0);
        wrapper.orderByDesc(FarmBreedType::getCreateTime);
        return farmBreedTypeDao.selectList(wrapper);
    }
    
    @Override
    public List<FarmInstitution> getAllFarms() {
        LambdaQueryWrapper<FarmInstitution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FarmInstitution::getDeleteFlag, 0);
        wrapper.eq(FarmInstitution::getAuditStatus, 1); // 只获取审核通过的养殖场
        wrapper.orderByDesc(FarmInstitution::getCreateTime);
        return farmInstitutionDao.selectList(wrapper);
    }
    
    @Override
    public List<BreedingProduct> getBreedingProductsByFarm(String farmCode) {
        if (StrUtil.isBlank(farmCode)) {
            return null;
        }
        LambdaQueryWrapper<BreedingProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BreedingProduct::getFarmCode, farmCode);
        wrapper.eq(BreedingProduct::getDeleteFlag, 0);
        wrapper.orderByDesc(BreedingProduct::getCreateTime);
        return breedingProductDao.selectList(wrapper);
    }
} 