package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.finance.Organization;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.vo.finance.OrganizationRequestVO;
import com.zbkj.common.vo.finance.OrganizationSearchVO;

import java.util.List;

/**
 * 机构管理服务接口
 */
public interface OrganizationService extends IService<Organization> {

    /**
     * 分页查询机构列表
     * @param pageParamRequest 分页参数
     * @param searchVO 搜索条件
     * @return 机构列表
     */
    List<Organization> pageList(PageParamRequest pageParamRequest, OrganizationSearchVO searchVO);

    /**
     * 添加机构
     * @param requestVO 请求参数
     * @return 是否成功
     */
    Boolean add(OrganizationRequestVO requestVO);

    /**
     * 编辑机构
     * @param requestVO 请求参数
     * @return 是否成功
     */
    Boolean edit(OrganizationRequestVO requestVO);

    /**
     * 删除机构
     * @param id 机构ID
     * @return 是否成功
     */
    Boolean delete(String id);

    /**
     * 检查机构编号是否存在
     * @param orgCode 机构编号
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    Boolean checkOrgCode(String orgCode, String excludeId);

    /**
     * 获取所有机构列表（用于下拉选择）
     * @return 机构列表
     */
    List<Organization> getAllList();

    /**
     * 根据上级机构ID获取子机构列表
     * @param parentOrgId 上级机构ID
     * @return 机构列表
     */
    List<Organization> getByParentId(String parentOrgId);
} 