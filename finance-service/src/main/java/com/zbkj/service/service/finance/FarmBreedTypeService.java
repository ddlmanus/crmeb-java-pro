package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.finance.FarmBreedType;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.vo.finance.FarmBreedTypeRequestVO;
import com.zbkj.common.vo.finance.FarmBreedTypeSearchVO;
import com.zbkj.common.vo.finance.LivestockInventoryDataVO;

import java.util.List;

/**
 * 养殖品种类型管理服务接口
 */
public interface FarmBreedTypeService extends IService<FarmBreedType> {

    /**
     * 分页查询养殖品种类型
     * @param pageParamRequest 分页参数
     * @param searchVO 搜索条件
     * @return 分页结果
     */
    IPage<FarmBreedType> pageList(PageParamRequest pageParamRequest, FarmBreedTypeSearchVO searchVO);

    /**
     * 添加养殖品种类型
     * @param requestVO 请求参数
     * @return 是否成功
     */
    Boolean add(FarmBreedTypeRequestVO requestVO);

    /**
     * 编辑养殖品种类型
     * @param requestVO 请求参数
     * @return 是否成功
     */
    Boolean edit(FarmBreedTypeRequestVO requestVO);

    /**
     * 删除养殖品种类型
     * @param id ID
     * @return 是否成功
     */
    Boolean delete(String id);

    /**
     * 获取养殖品种类型详情
     * @param id ID
     * @return 详情信息
     */
    FarmBreedType getDetailById(String id);

    /**
     * 同步牧码通存栏数据到数据库
     * @param farmCode 养殖场编码
     * @param livestockData 存栏数据列表
     * @return 是否成功
     */
    Boolean syncLivestockInventoryData(String farmCode, List<LivestockInventoryDataVO> livestockData);

    /**
     * 根据养殖场编码查询存栏数据
     * @param farmCode 养殖场编码
     * @return 存栏数据列表
     */
    List<FarmBreedType> getByFarmCode(String farmCode);

    /**
     * 获取所有养殖品种类型列表（不分页）
     * @return 列表
     */
    List<FarmBreedType> getAllList();

    /**
     * 根据养殖场编码同步存栏数据（包含养殖机构创建逻辑）
     * @param farmCode 养殖场编码
     * @return 是否成功
     */
    Boolean syncLivestockDataByFarmCode(String farmCode);

    /**
     * 根据养殖场编码和品种名称获取存栏量
     * @param farmCode 养殖场编码
     * @param breedName 品种名称
     * @return 存栏量总数，如果不存在返回null
     */
    Integer getStockQuantityByFarmCodeAndBreedName(String farmCode, String breedName);
} 