package com.zbkj.service.service.impl.finance;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.FarmInstitution;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.finance.FarmBreedType;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.vo.finance.FarmBreedTypeRequestVO;
import com.zbkj.common.vo.finance.FarmBreedTypeSearchVO;
import com.zbkj.common.vo.finance.LivestockInventoryDataVO;
import com.zbkj.service.dao.finance.FarmBreedTypeDao;
import com.zbkj.service.service.finance.FarmBreedTypeService;
import com.zbkj.service.service.finance.FarmInstitutionService;
import com.zbkj.service.service.finance.MumaIntegrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 养殖品种类型管理服务实现
 */
@Slf4j
@Service
public class FarmBreedTypeServiceImpl extends ServiceImpl<FarmBreedTypeDao, FarmBreedType> implements FarmBreedTypeService {

    @Autowired
    private FarmInstitutionService farmInstitutionService;

    @Autowired
    private MumaIntegrationService mumaIntegrationService;

    @Override
    public IPage<FarmBreedType> pageList(PageParamRequest pageParamRequest, FarmBreedTypeSearchVO searchVO) {
        try {
            log.info("分页查询参数 - page: {}, limit: {}", pageParamRequest.getPage(), pageParamRequest.getLimit());
            
            LambdaQueryWrapper<FarmBreedType> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FarmBreedType::getDeleteFlag, 0);
            
            if (searchVO != null) {
                log.info("搜索条件: {}", searchVO);
                if (StrUtil.isNotBlank(searchVO.getKeywords())) {
                    wrapper.and(w -> w.like(FarmBreedType::getFarmName, searchVO.getKeywords())
                            .or().like(FarmBreedType::getBreedName, searchVO.getKeywords())
                            .or().like(FarmBreedType::getBreedType, searchVO.getKeywords()));
                }
                if (StrUtil.isNotBlank(searchVO.getFarmCode())) {
                    wrapper.like(FarmBreedType::getFarmCode, searchVO.getFarmCode());
                }
                if (StrUtil.isNotBlank(searchVO.getFarmName())) {
                    wrapper.like(FarmBreedType::getFarmName, searchVO.getFarmName());
                }
                if (StrUtil.isNotBlank(searchVO.getBreedName())) {
                    wrapper.like(FarmBreedType::getBreedName, searchVO.getBreedName());
                }
                if (StrUtil.isNotBlank(searchVO.getBreedType())) {
                    wrapper.like(FarmBreedType::getBreedType, searchVO.getBreedType());
                }
                if (StrUtil.isNotBlank(searchVO.getGrowthStage())) {
                    wrapper.like(FarmBreedType::getGrowthStage, searchVO.getGrowthStage());
                }
                if (StrUtil.isNotBlank(searchVO.getDateLimit())) {
                    String[] dates = searchVO.getDateLimit().split(" - ");
                    if (dates.length == 2) {
                        wrapper.between(FarmBreedType::getCreateTime, dates[0] + " 00:00:00", dates[1] + " 23:59:59");
                    }
                }
            }
            
            wrapper.orderByDesc(FarmBreedType::getCreateTime);
            
            // 使用PageHelper分页（与系统其他地方保持一致）
            com.github.pagehelper.Page<FarmBreedType> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
            List<FarmBreedType> list = this.list(wrapper);
            
            log.info("分页查询结果 - 总数: {}, 当前页数据量: {}", page.getTotal(), list.size());
            
            // 转换为IPage格式返回
            Page<FarmBreedType> result = new Page<>(pageParamRequest.getPage(), pageParamRequest.getLimit());
            result.setRecords(list);
            result.setTotal(page.getTotal());
            result.setCurrent(pageParamRequest.getPage());
            result.setSize(pageParamRequest.getLimit());
            
            return result;
        } catch (Exception e) {
            log.error("分页查询养殖品种类型失败", e);
            Page<FarmBreedType> emptyResult = new Page<>(pageParamRequest.getPage(), pageParamRequest.getLimit());
            emptyResult.setTotal(0);
            emptyResult.setRecords(new ArrayList<>());
            return emptyResult;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean add(FarmBreedTypeRequestVO requestVO) {
        FarmBreedType farmBreedType = new FarmBreedType();
        BeanUtils.copyProperties(requestVO, farmBreedType);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(new Date());
        farmBreedType.setCreateTime(now);
        farmBreedType.setUpdateTime(now);
        farmBreedType.setDeleteFlag(0);
        
        return this.save(farmBreedType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean edit(FarmBreedTypeRequestVO requestVO) {
        if (StrUtil.isBlank(requestVO.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "ID不能为空");
        }
        
        FarmBreedType farmBreedType = getDetailByIdException(requestVO.getId());
        BeanUtils.copyProperties(requestVO, farmBreedType);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        farmBreedType.setUpdateTime(sdf.format(new Date()));
        
        return this.updateById(farmBreedType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(String id) {
        FarmBreedType farmBreedType = getDetailByIdException(id);
        farmBreedType.setDeleteFlag(1);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        farmBreedType.setUpdateTime(sdf.format(new Date()));
        
        return this.updateById(farmBreedType);
    }

    @Override
    public FarmBreedType getDetailById(String id) {
        return getDetailByIdException(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean syncLivestockInventoryData(String farmCode, List<LivestockInventoryDataVO> livestockData) {
        try {
            log.info("开始同步养殖场{}的存栏数据，共{}条", farmCode, livestockData.size());
            
            // 软删除现有数据
            LambdaUpdateWrapper<FarmBreedType> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(FarmBreedType::getDeleteFlag, 1);
            updateWrapper.eq(FarmBreedType::getFarmCode, farmCode);
            updateWrapper.eq(FarmBreedType::getDeleteFlag, 0);
            this.update(updateWrapper);
            
            // 批量插入新数据
            List<FarmBreedType> farmBreedTypeList = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String now = sdf.format(new Date());
            
            for (LivestockInventoryDataVO data : livestockData) {
                FarmBreedType farmBreedType = new FarmBreedType();
                farmBreedType.setFarmCode(data.getFarmCode());
                farmBreedType.setFarmName(data.getFarmName());
                farmBreedType.setBreedName(data.getBreed());
                farmBreedType.setBreedType(data.getLivestockType());
                farmBreedType.setGrowthStage(data.getBreedingStage());
                // 将字符串类型的存栏量转换为整数类型
                try {
                    farmBreedType.setStockQuantity(Integer.parseInt(data.getCurrentPeriodStock()));
                } catch (NumberFormatException e) {
                    log.warn("存栏量转换失败，设置为0: {}", data.getCurrentPeriodStock());
                    farmBreedType.setStockQuantity(0);
                }
                farmBreedType.setCreateTime(now);
                farmBreedType.setUpdateTime(now);
                farmBreedType.setDeleteFlag(0);
                
                farmBreedTypeList.add(farmBreedType);
            }
            
            boolean result = this.saveBatch(farmBreedTypeList);
            log.info("同步养殖场{}存栏数据完成，结果: {}", farmCode, result);
            return result;
        } catch (Exception e) {
            log.error("同步存栏数据异常", e);
            return false;
        }
    }

    @Override
    public List<FarmBreedType> getByFarmCode(String farmCode) {
        LambdaQueryWrapper<FarmBreedType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FarmBreedType::getFarmCode, farmCode);
        wrapper.eq(FarmBreedType::getDeleteFlag, 0);
        //存栏量大于等于1
        wrapper.ge(FarmBreedType::getStockQuantity, 1);
        wrapper.orderByDesc(FarmBreedType::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<FarmBreedType> getAllList() {
        LambdaQueryWrapper<FarmBreedType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FarmBreedType::getDeleteFlag, 0);
        wrapper.orderByDesc(FarmBreedType::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean syncLivestockDataByFarmCode(String farmCode) {
        try {
            log.info("开始同步养殖场{}的存栏数据", farmCode);
            
            // 调用牧码通接口获取数据
            com.zbkj.common.vo.finance.LivestockInventoryRequestVO request = new com.zbkj.common.vo.finance.LivestockInventoryRequestVO();
            request.setFarmCode(farmCode);
            
            com.zbkj.common.vo.finance.LivestockInventoryResponseVO.LivestockInventoryData data = mumaIntegrationService.getLivestockInventory(request);
            
            if (data != null && data.getRowData() != null && !data.getRowData().isEmpty()) {
                // 查询或创建养殖机构
                com.zbkj.common.model.finance.FarmInstitution institution = farmInstitutionService.getFarmInstitutionByFarmCode(farmCode);
                if (institution == null) {
                    // 根据API返回的数据创建养殖机构
                    if (!data.getRowData().isEmpty()) {
                        com.zbkj.common.vo.finance.LivestockInventoryDataVO firstRow = data.getRowData().get(0);
                        institution = createFarmInstitution(farmCode, firstRow.getFarmName());
                    }
                }
                
                // 同步数据到数据库（包含养殖场ID设置）
                Boolean result = syncLivestockInventoryDataWithInstitution(farmCode, institution, data.getRowData());
                
                if (result) {
                    log.info("养殖场{}存栏数据同步成功，共{}条数据", farmCode, data.getRowData().size());
                    return true;
                } else {
                    log.error("养殖场{}存栏数据同步失败", farmCode);
                    return false;
                }
            } else {
                log.warn("养殖场{}未获取到存栏数据", farmCode);
                return false;
            }
        } catch (Exception e) {
            log.error("同步养殖场{}存栏数据异常", farmCode, e);
            return false;
        }
    }

    /**
     * 创建养殖机构记录
     */
    private com.zbkj.common.model.finance.FarmInstitution createFarmInstitution(String farmCode, String farmName) {
        try {
            com.zbkj.common.model.finance.FarmInstitution institution = new com.zbkj.common.model.finance.FarmInstitution();
            institution.setFarmCode(farmCode);
            institution.setFarmName(farmName);
            institution.setAuditStatus(1); // 默认审核通过
            institution.setCreateTime(new Date());
            institution.setUpdateTime(new Date());
            institution.setDeleteFlag(0);
            
            boolean saved = farmInstitutionService.save(institution);
            if (saved) {
                log.info("成功创建养殖机构：{}({})", farmName, farmCode);
                return institution;
            }
        } catch (Exception e) {
            log.error("创建养殖机构失败：{}({})", farmName, farmCode, e);
        }
        return null;
    }

    /**
     * 同步存栏数据并设置养殖场ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean syncLivestockInventoryDataWithInstitution(String farmCode, com.zbkj.common.model.finance.FarmInstitution institution, List<com.zbkj.common.vo.finance.LivestockInventoryDataVO> livestockData) {
        try {
            log.info("开始同步养殖场{}的存栏数据，共{}条", farmCode, livestockData.size());
            
            // 软删除现有数据
            LambdaUpdateWrapper<FarmBreedType> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(FarmBreedType::getDeleteFlag, 1);
            updateWrapper.eq(FarmBreedType::getFarmCode, farmCode);
            updateWrapper.eq(FarmBreedType::getDeleteFlag, 0);
            this.update(updateWrapper);
            
            // 批量插入新数据
            List<FarmBreedType> farmBreedTypeList = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String now = sdf.format(new Date());
            
            for (com.zbkj.common.vo.finance.LivestockInventoryDataVO data : livestockData) {
                FarmBreedType farmBreedType = new FarmBreedType();
                farmBreedType.setFarmCode(data.getFarmCode());
                farmBreedType.setFarmName(data.getFarmName());
                // 设置养殖场ID
                if (institution != null) {
                    farmBreedType.setFarmId(institution.getId().toString());
                }
                farmBreedType.setBreedName(data.getBreed());
                farmBreedType.setBreedType(data.getLivestockType());
                farmBreedType.setGrowthStage(data.getBreedingStage());
                // 将字符串类型的存栏量转换为整数类型
                try {
                    farmBreedType.setStockQuantity(Integer.parseInt(data.getCurrentPeriodStock()));
                } catch (NumberFormatException e) {
                    log.warn("存栏量转换失败，设置为0: {}", data.getCurrentPeriodStock());
                    farmBreedType.setStockQuantity(0);
                }
                farmBreedType.setCreateTime(now);
                farmBreedType.setUpdateTime(now);
                farmBreedType.setDeleteFlag(0);
                
                farmBreedTypeList.add(farmBreedType);
            }
            
            boolean result = this.saveBatch(farmBreedTypeList);
            log.info("同步养殖场{}存栏数据完成，结果: {}", farmCode, result);
            return result;
        } catch (Exception e) {
            log.error("同步存栏数据异常", e);
            return false;
        }
    }

    @Override
    public Integer getStockQuantityByFarmCodeAndBreedName(String farmCode, String breedName) {
        LambdaQueryWrapper<FarmBreedType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FarmBreedType::getFarmCode, farmCode);
        wrapper.eq(FarmBreedType::getBreedName, breedName);
        wrapper.eq(FarmBreedType::getDeleteFlag, 0);
        
        List<FarmBreedType> farmBreedTypes = this.list(wrapper);
        if (farmBreedTypes == null || farmBreedTypes.isEmpty()) {
            return null; // 不存在返回null
        }
        
        // 计算总存栏量
        int totalStock = farmBreedTypes.stream()
                .mapToInt(farmBreedType -> farmBreedType.getStockQuantity() != null ? farmBreedType.getStockQuantity() : 0)
                .sum();
        
        log.info("养殖场{}品种{}的总存栏量：{}", farmCode, breedName, totalStock);
        return totalStock;
    }

    /**
     * 根据ID获取详情，不存在则抛出异常
     */
    private FarmBreedType getDetailByIdException(String id) {
        FarmBreedType farmBreedType = this.getById(id);
        if (ObjectUtil.isNull(farmBreedType) || farmBreedType.getDeleteFlag() == 1) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "养殖品种类型不存在");
        }
        return farmBreedType;
    }
} 