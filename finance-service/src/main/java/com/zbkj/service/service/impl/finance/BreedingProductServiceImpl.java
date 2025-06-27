package com.zbkj.service.service.impl.finance;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.config.ApiProperties;
import com.zbkj.common.dto.BreedingProductApiRequest;
import com.zbkj.common.dto.BreedingProductApiResponse;
import com.zbkj.common.dto.StockQuantityApiRequest;
import com.zbkj.common.dto.StockQuantityApiResponse;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.finance.BreedingProduct;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.service.dao.finance.BreedingProductDao;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.finance.BreedingProductService;
import com.zbkj.service.service.finance.FarmInstitutionService;
import com.zbkj.service.service.finance.FarmBreedTypeService;
import com.zbkj.common.vo.finance.BreedingProductReponse;
import com.zbkj.common.vo.finance.BreedingProductRequestVO;
import com.zbkj.common.vo.finance.BreedingProductSearchVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.github.pagehelper.PageHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 养殖品种服务实现
 */
@Slf4j
@Service
public class BreedingProductServiceImpl extends ServiceImpl<BreedingProductDao, BreedingProduct> implements BreedingProductService {

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ApiProperties apiProperties;
    @Autowired
    private UserService userService;
    @Autowired
    private FarmInstitutionService farmInstitutionService;
    @Autowired
    private FarmBreedTypeService farmBreedTypeService;
    /**
     * 分页查询养殖品种
     * @param pageParamRequest 分页参数
     * @param searchVO 搜索条件
     * @return 分页结果
     */
    @Override
    public List<BreedingProduct> pageList(PageParamRequest pageParamRequest, BreedingProductSearchVO searchVO) {
        try {
            log.info("分页查询养殖品种，页码：{}，大小：{}", pageParamRequest.getPage(), pageParamRequest.getLimit());
            
            // 使用PageHelper分页
            PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
            
            LambdaQueryWrapper<BreedingProduct> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BreedingProduct::getDeleteFlag, 0);
            
            // 添加搜索条件
            if (searchVO != null) {
                if (StrUtil.isNotBlank(searchVO.getKeywords())) {
                    wrapper.and(w -> w.like(BreedingProduct::getName, searchVO.getKeywords())
                                    .or().like(BreedingProduct::getCode, searchVO.getKeywords()));
                }
                if (StrUtil.isNotBlank(searchVO.getCode())) {
                    wrapper.like(BreedingProduct::getCode, searchVO.getCode());
                }
                if (StrUtil.isNotBlank(searchVO.getName())) {
                    wrapper.like(BreedingProduct::getName, searchVO.getName());
                }
                if (searchVO.getStockQuantity() != null) {
                    wrapper.eq(BreedingProduct::getStockQuantity, searchVO.getStockQuantity());
                }
                if (StrUtil.isNotBlank(searchVO.getDateLimit())) {
                    String[] dates = searchVO.getDateLimit().split(" - ");
                    if (dates.length == 2) {
                        wrapper.between(BreedingProduct::getCreateTime, dates[0] + " 00:00:00", dates[1] + " 23:59:59");
                    }
                }
            }
            
            wrapper.orderByDesc(BreedingProduct::getCreateTime);
            
            List<BreedingProduct> list = this.list(wrapper);
            log.info("分页查询养殖品种成功，数据量：{}", list.size());
            return list;
        } catch (Exception e) {
            log.error("分页查询养殖品种失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 添加养殖品种
     * @param requestVO 请求参数
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean add(BreedingProductRequestVO requestVO) {
        // 检查品种编号是否重复
        if (checkCode(requestVO.getCode(), null)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "品种编号已存在");
        }
        
        BreedingProduct breedingProduct = new BreedingProduct();
        BeanUtils.copyProperties(requestVO, breedingProduct);
        breedingProduct.setCreateTime(new Date());
        breedingProduct.setUpdateTime(new Date());
        breedingProduct.setDeleteFlag(0);
        breedingProduct.setFarmCode(requestVO.getFarmCode());
        breedingProduct.setFarmName(requestVO.getFarmName());
        
        return this.save(breedingProduct);
    }

    /**
     * 编辑养殖品种
     * @param requestVO 请求参数
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean edit(BreedingProductRequestVO requestVO) {
        if (StrUtil.isBlank(requestVO.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "品种ID不能为空");
        }
        
        BreedingProduct breedingProduct = getByIdException(requestVO.getId());
        
        // 检查品种编号是否重复
        if (!requestVO.getCode().equals(breedingProduct.getCode()) && 
            checkCode(requestVO.getCode(), requestVO.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "品种编号已存在");
        }
        
        BeanUtils.copyProperties(requestVO, breedingProduct);
        breedingProduct.setUpdateTime(new Date());
        
        return this.updateById(breedingProduct);
    }

    /**
     * 删除养殖品种
     * @param id 品种ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(String id) {
        BreedingProduct breedingProduct = getByIdException(id);
        breedingProduct.setDeleteFlag(1);
        breedingProduct.setUpdateTime(new Date());
        return this.updateById(breedingProduct);
    }

    /**
     * 检查品种编号是否存在
     * @param code 品种编号
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    @Override
    public Boolean checkCode(String code, String excludeId) {
        LambdaQueryWrapper<BreedingProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BreedingProduct::getCode, code);
        wrapper.eq(BreedingProduct::getDeleteFlag, 0);
        if (StrUtil.isNotBlank(excludeId)) {
            wrapper.ne(BreedingProduct::getId, excludeId);
        }
        return this.count(wrapper) > 0;
    }

    /**
     * 获取所有养殖品种列表（不分页）
     * @return 品种列表
     */
    @Override
    public List<BreedingProduct> getAllList() {
        LambdaQueryWrapper<BreedingProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BreedingProduct::getDeleteFlag, 0);
        wrapper.orderByDesc(BreedingProduct::getCreateTime);
        return this.list(wrapper);
    }

    /**
     * 根据养殖场编码同步养殖品种数据
     * @param farmCode 养殖场编码
     * @param farmName 养殖场名称
     * @return 同步的品种数量，返回null表示同步失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer syncBreedingProductsByFarmCode(String farmCode, String farmName) {
        try {
            log.info("开始同步养殖场{}({})的养殖品种数据", farmName, farmCode);
            
            // 构建请求参数
            BreedingProductApiRequest request = new BreedingProductApiRequest();
            request.setFarm_code(farmCode);
            request.setPage(1);
            request.setPageSize(100); // 一次获取较多数据
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (apiProperties.getAuthToken() != null && !apiProperties.getAuthToken().isEmpty()) {
                headers.set("apiToken", apiProperties.getAuthToken());
            }
            
            // 发送请求
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            String url = apiProperties.getBreedingProductUrl() 
                    + "?farm_code=" + farmCode 
                    + "&page=" + request.getPage() 
                    + "&pageSize=" + request.getPageSize();
            
            log.debug("请求养殖品种API: {}", url);
            ResponseEntity<BreedingProductApiResponse> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    BreedingProductApiResponse.class
            );
            
            // 处理响应
            BreedingProductApiResponse response = responseEntity.getBody();
            if (response == null || response.getData() == null || response.getData().getRowData() == null) {
                log.warn("同步养殖品种数据失败：响应数据为空，farmCode: {}", farmCode);
                return null;
            }
            
            // 先软删除该养殖场的现有品种数据
            LambdaUpdateWrapper<BreedingProduct> deleteWrapper = new LambdaUpdateWrapper<>();
            deleteWrapper.set(BreedingProduct::getDeleteFlag, 1)
                    .set(BreedingProduct::getUpdateTime, new Date())
                    .eq(BreedingProduct::getFarmCode, farmCode)
                    .eq(BreedingProduct::getDeleteFlag, 0);
            this.update(deleteWrapper);
            
            // 保存新数据
            List<BreedingProduct> breedingProducts = new ArrayList<>();
            for (BreedingProductApiResponse.BreedingProductApiRow row : response.getData().getRowData()) {
                BreedingProduct product = new BreedingProduct();
                product.setFarmCode(farmCode);
                product.setFarmName(farmName);
                product.setCode(row.getCode());
                product.setName(row.getName());
                product.setSplitSpecies(row.getSplit_species());
                product.setRemark("来自牧码通API的养殖品种数据");
                
                // 获取存栏量并设置
                Integer stockQuantity = getStockQuantity(farmCode, row.getName());
                product.setStockQuantity(stockQuantity);
                log.info("设置品种{}的存栏量：{}", row.getName(), stockQuantity);
                
                product.setCreateTime(new Date());
                product.setUpdateTime(new Date());
                product.setDeleteFlag(0);
                breedingProducts.add(product);
            }
            
            boolean saveResult = this.saveBatch(breedingProducts);
            if (saveResult) {
                log.info("同步养殖场{}({})的养殖品种数据完成，共同步{}条数据", 
                    farmName, farmCode, breedingProducts.size());
                return breedingProducts.size();
            } else {
                log.error("保存养殖场{}({})的养殖品种数据失败", farmName, farmCode);
                return null;
            }
        } catch (Exception e) {
            log.error("同步养殖场{}({})的养殖品种数据异常", farmName, farmCode, e);
            return null;
        }
    }

    /**
     * 根据当前用户的机构ID获取该机构下所有养殖场的品种信息
     * @return 养殖品种列表
     */
    @Override
    public List<BreedingProductReponse> getBreedingProductsByCurrentUserOrganization() {
        User currentUser = userService.getInfo();
        if (currentUser == null) {
            log.error("获取当前用户信息失败");
            return new ArrayList<>();
        }
        
        log.info("根据当前用户获取养殖品种信息，用户ID：{}", currentUser.getId());
        
        try {
            List<String> farmCodes = new ArrayList<>();
            
            // 1. 优先检查用户是否关联机构
            if (currentUser.getOrganizationId() != null && !currentUser.getOrganizationId().isEmpty()) {
                log.info("用户关联机构ID：{}", currentUser.getOrganizationId());
                
                // 根据机构ID获取该机构下的所有养殖场
                List<com.zbkj.common.model.finance.FarmInstitution> farmInstitutions = 
                    farmInstitutionService.getFarmInstitutionByOrganizationId(currentUser.getOrganizationId());
                
                if (farmInstitutions != null && !farmInstitutions.isEmpty()) {
                    log.info("找到{}个养殖场", farmInstitutions.size());
                    
                    // 获取所有养殖场的编码
                    for (com.zbkj.common.model.finance.FarmInstitution farmInstitution : farmInstitutions) {
                        if (farmInstitution.getFarmCode() != null && !farmInstitution.getFarmCode().isEmpty()) {
                            farmCodes.add(farmInstitution.getFarmCode());
                        }
                    }
                } else {
                    log.warn("机构{}下没有找到养殖场", currentUser.getOrganizationId());
                }
            }
            
            // 2. 如果通过机构没有获取到养殖场编码，且用户有养殖场编码，则使用用户的养殖场编码
            if (farmCodes.isEmpty() && currentUser.getFarmCode() != null && !currentUser.getFarmCode().isEmpty()) {
                log.info("机构下没有数据或用户无机构，使用用户养殖场编码：{}", currentUser.getFarmCode());
                farmCodes.add(currentUser.getFarmCode());
            }
            
            if (farmCodes.isEmpty()) {
                log.warn("没有找到有效的养殖场编码");
                return new ArrayList<>();
            }
            
            log.info("有效的养殖场编码：{}", farmCodes);
            
            // 3. 根据养殖场编码查询所有品种
            LambdaQueryWrapper<BreedingProduct> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(BreedingProduct::getFarmCode, farmCodes);
            wrapper.eq(BreedingProduct::getDeleteFlag, 0);
            wrapper.orderByDesc(BreedingProduct::getCreateTime);
            
            List<BreedingProduct> breedingProducts = this.list(wrapper);
            log.info("找到{}条养殖品种记录", breedingProducts != null ? breedingProducts.size() : 0);
            
            // 4. 如果没有数据，尝试为每个养殖场同步数据
            if (breedingProducts == null || breedingProducts.isEmpty()) {
                log.info("数据库中没有品种数据，尝试同步数据");
                
                if (currentUser.getOrganizationId() != null && !currentUser.getOrganizationId().isEmpty()) {
                    // 有机构的情况，从养殖场列表同步
                    List<com.zbkj.common.model.finance.FarmInstitution> farmInstitutions = 
                        farmInstitutionService.getFarmInstitutionByOrganizationId(currentUser.getOrganizationId());
                    if (farmInstitutions != null) {
                        for (com.zbkj.common.model.finance.FarmInstitution farmInstitution : farmInstitutions) {
                            if (farmInstitution.getFarmCode() != null && !farmInstitution.getFarmCode().isEmpty()) {
                                Integer syncCount = syncBreedingProductsByFarmCode(
                                    farmInstitution.getFarmCode(), 
                                    farmInstitution.getFarmName()
                                );
                                log.info("同步养殖场{}({})的品种数据，结果：{}", 
                                        farmInstitution.getFarmName(), 
                                        farmInstitution.getFarmCode(), 
                                        syncCount);
                            }
                        }
                    }
                }
                
                // 如果用户有个人养殖场编码，也尝试同步
                if (currentUser.getFarmCode() != null && !currentUser.getFarmCode().isEmpty()) {
                    Integer syncCount = syncBreedingProductsByFarmCode(
                        currentUser.getFarmCode(), 
                        currentUser.getFarmName() != null ? currentUser.getFarmName() : "用户养殖场"
                    );
                    log.info("同步用户养殖场{}的品种数据，结果：{}", 
                            currentUser.getFarmCode(), 
                            syncCount);
                }
                
                // 重新查询
                breedingProducts = this.list(wrapper);
                log.info("同步完成后找到{}条养殖品种记录", breedingProducts != null ? breedingProducts.size() : 0);
            }
            
            // 5. 转换为响应VO
            List<BreedingProductReponse> result = new ArrayList<>();
            if (breedingProducts != null) {
                for (BreedingProduct product : breedingProducts) {
                    BreedingProductReponse response = new BreedingProductReponse();
                    response.setId(product.getId());
                    response.setName(product.getName());
                    response.setDescription(product.getRemark());
                    response.setFarmCode(product.getFarmCode());
                    response.setFarmName(product.getFarmName());
                    response.setStockQuantity(product.getStockQuantity());
                    result.add(response);
                }
            }
            
            log.info("最终返回{}条养殖品种数据", result.size());
            return result;
            
        } catch (Exception e) {
            log.error("根据当前用户获取养殖品种信息失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 调用牧码通API获取指定养殖场和品种的存栏量
     * @param farmCode 养殖场编码
     * @param breedName 品种名称
     * @return 存栏量，如果获取失败返回0
     */
    private Integer getStockQuantityFromApi(String farmCode, String breedName) {
        try {
            log.info("调用牧码通API获取存栏量，养殖场编码：{}，品种名称：{}", farmCode, breedName);
            
            // 构建请求参数
            StockQuantityApiRequest request = new StockQuantityApiRequest();
            request.setFarm_code(farmCode);
            request.setBreed(breedName);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (apiProperties.getAuthToken() != null && !apiProperties.getAuthToken().isEmpty()) {
                headers.set("apiToken", apiProperties.getAuthToken());
            }
            
            // 发送请求
            HttpEntity<StockQuantityApiRequest> requestEntity = new HttpEntity<>(request, headers);
            
            log.debug("请求存栏量API: {}", apiProperties.getStockQuantityUrl());
            ResponseEntity<StockQuantityApiResponse> responseEntity = restTemplate.exchange(
                    apiProperties.getStockQuantityUrl(),
                    HttpMethod.POST,
                    requestEntity,
                    StockQuantityApiResponse.class
            );
            
            // 处理响应
            StockQuantityApiResponse response = responseEntity.getBody();
            if (response == null || response.getData() == null || response.getData().getRowData() == null) {
                log.warn("获取存栏量数据失败：响应数据为空，farmCode: {}, breedName: {}", farmCode, breedName);
                return 0;
            }
            
            // 筛选指定品种的数据并计算总存栏量
            int totalStock = 0;
            for (StockQuantityApiResponse.StockQuantityRow row : response.getData().getRowData()) {
                if (breedName.equals(row.getBreed())) {
                    try {
                        int stock = Integer.parseInt(row.getCurrent_period_stock());
                        totalStock += stock;
                    } catch (NumberFormatException e) {
                        log.warn("存栏量转换失败，跳过: {}", row.getCurrent_period_stock());
                    }
                }
            }
            
            log.info("API获取到养殖场{}品种{}的总存栏量：{}", farmCode, breedName, totalStock);
            return totalStock;
            
        } catch (Exception e) {
            log.error("调用牧码通API获取存栏量失败，farmCode: {}, breedName: {}", farmCode, breedName, e);
            return 0;
        }
    }
    
    /**
     * 获取品种的存栏量
     * 先从数据库查询，如果不存在则调用API获取
     * @param farmCode 养殖场编码
     * @param breedName 品种名称
     * @return 存栏量
     */
    private Integer getStockQuantity(String farmCode, String breedName) {
        // 1. 先查询品种类型表
        Integer stockQuantity = farmBreedTypeService.getStockQuantityByFarmCodeAndBreedName(farmCode, breedName);
        
        // 2. 如果数据库中不存在，调用API获取
        if (stockQuantity == null) {
            log.info("数据库中不存在品种{}的存栏量，调用API获取", breedName);
            stockQuantity = getStockQuantityFromApi(farmCode, breedName);
        }
        
        return stockQuantity != null ? stockQuantity : 0;
    }

    /**
     * 根据ID获取品种信息，不存在则抛出异常
     * @param id 品种ID
     * @return 品种信息
     */
    private BreedingProduct getByIdException(String id) {
        BreedingProduct breedingProduct = this.getById(id);
        if (ObjectUtil.isNull(breedingProduct) || breedingProduct.getDeleteFlag() == 1) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "养殖品种不存在");
        }
        return breedingProduct;
    }
} 