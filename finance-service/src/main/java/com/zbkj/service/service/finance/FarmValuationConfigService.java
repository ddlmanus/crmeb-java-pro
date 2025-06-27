package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.FarmValuationConfig;
import com.zbkj.common.model.finance.BreedingProduct;
import com.zbkj.common.model.finance.FarmBreedType;
import com.zbkj.common.model.finance.FarmInstitution;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.vo.finance.FarmValuationConfigSearchVO;
import com.zbkj.common.vo.finance.FarmValuationConfigVO;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 养殖场评估价值配置服务接口
 */
public interface FarmValuationConfigService extends IService<FarmValuationConfig> {

    /**
     * 分页查询养殖场评估价值配置
     * @param pageParamRequest 分页参数
     * @param searchVO 搜索条件
     * @return 分页结果
     */
    PageInfo<FarmValuationConfig> getPage(PageParamRequest pageParamRequest, FarmValuationConfigSearchVO searchVO);

    /**
     * 添加养殖场评估价值配置
     * @param configVO 配置信息
     * @return 是否成功
     */
    Boolean add(FarmValuationConfigVO configVO);

    /**
     * 更新养殖场评估价值配置
     * @param configVO 配置信息
     * @return 是否成功
     */
    Boolean edit(FarmValuationConfigVO configVO);

    /**
     * 删除养殖场评估价值配置
     * @param id 配置ID
     * @return 是否成功
     */
    Boolean delete(Integer id);

    /**
     * 根据ID获取配置详情
     * @param id 配置ID
     * @return 配置信息
     */
    FarmValuationConfig getConfigById(Integer id);

    /**
     * 获取所有启用的配置列表
     * @return 配置列表
     */
    List<FarmValuationConfig> getEnabledConfigs();

    /**
     * 启用/禁用配置
     * @param id 配置ID
     * @param status 状态
     * @return 是否成功
     */
    Boolean updateStatus(Integer id, Integer status);

    FarmValuationConfig getByBreedingType(@NotBlank(message = "养殖场code不能为空") String farmCode, @NotBlank(message = "养殖品种名称不能为空") String breedName, String breedType);
    
    /**
     * 获取所有养殖品种列表
     * @return 品种列表
     */
    List<BreedingProduct> getAllBreedingProducts();
    
    /**
     * 根据品种名称获取品种类型列表
     * @param breedName 品种名称
     * @return 品种类型列表
     */
    List<FarmBreedType> getBreedTypesByName(String breedName);
    
    /**
     * 获取所有养殖场列表
     * @return 养殖场列表
     */
    List<FarmInstitution> getAllFarms();
    
    /**
     * 根据养殖场编码获取养殖品种列表
     * @param farmCode 养殖场编码
     * @return 品种列表
     */
    List<BreedingProduct> getBreedingProductsByFarm(String farmCode);
}