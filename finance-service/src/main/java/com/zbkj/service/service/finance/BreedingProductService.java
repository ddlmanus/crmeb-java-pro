package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.finance.BreedingProduct;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.vo.finance.BreedingProductReponse;
import com.zbkj.common.vo.finance.BreedingProductRequestVO;
import com.zbkj.common.vo.finance.BreedingProductSearchVO;

import java.util.List;

/**
 * 养殖品种服务接口
 */
public interface BreedingProductService extends IService<BreedingProduct> {

    /**
     * 分页查询养殖品种
     * @param pageParamRequest 分页参数
     * @param searchVO 搜索条件
     * @return 分页结果列表（使用PageHelper分页）
     */
    List<BreedingProduct> pageList(PageParamRequest pageParamRequest, BreedingProductSearchVO searchVO);

    /**
     * 添加养殖品种
     * @param requestVO 请求参数
     * @return 是否成功
     */
    Boolean add(BreedingProductRequestVO requestVO);

    /**
     * 编辑养殖品种
     * @param requestVO 请求参数
     * @return 是否成功
     */
    Boolean edit(BreedingProductRequestVO requestVO);

    /**
     * 删除养殖品种
     * @param id 品种ID
     * @return 是否成功
     */
    Boolean delete(String id);

    /**
     * 检查品种编号是否存在
     * @param code 品种编号
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    Boolean checkCode(String code, String excludeId);

    /**
     * 获取所有养殖品种列表（不分页）
     * @return 品种列表
     */
    List<BreedingProduct> getAllList();

        /**
     * 根据养殖场编码同步养殖品种数据
     * @param farmCode 养殖场编码  
     * @param farmName 养殖场名称
     * @return 同步的品种数量，返回null表示同步失败
     */
    Integer syncBreedingProductsByFarmCode(String farmCode, String farmName);

    /**
     * 根据当前用户的机构ID获取该机构下所有养殖场的品种信息
     * @return 养殖品种列表
     */
    List<BreedingProductReponse> getBreedingProductsByCurrentUserOrganization();
}