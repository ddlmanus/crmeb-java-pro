package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.OrgCategory;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.finance.OrgCategoryRequest;

import java.util.List;

/**
 * 组织分类服务接口
 */
public interface OrgCategoryService extends IService<OrgCategory> {
    
    /**
     * 根据父级代码获取子分类列表
     * @param parentCode 父级代码
     * @return 子分类列表
     */
    List<OrgCategory> getByParentCode(String parentCode);
    
    /**
     * 根据层级获取分类列表
     * @param level 层级
     * @return 分类列表
     */
    List<OrgCategory> getByLevel(Integer level);
    
    /**
     * 根据代码获取分类信息
     * @param code 分类代码
     * @return 分类信息
     */
    OrgCategory getByCode(String code);
    
    /**
     * 获取所有启用的分类列表
     * @return 分类列表
     */
    List<OrgCategory> getAllEnabled();

    /**
     * 分页查询养殖机构分类
     * @param pageParamRequest 分页参数
     * @param keywords 关键词
     * @return 分页结果
     */
    PageInfo<OrgCategory> getAdminPage(PageParamRequest pageParamRequest, String keywords);

    /**
     * 添加养殖机构分类
     * @param request 请求参数
     * @return 是否成功
     */
    Boolean add(OrgCategoryRequest request);

    /**
     * 编辑养殖机构分类
     * @param request 请求参数
     * @return 是否成功
     */
    Boolean edit(OrgCategoryRequest request);

    /**
     * 删除养殖机构分类
     * @param id 分类ID
     * @return 是否成功
     */
    Boolean delete(String id);
    
    /**
     * 检查分类代码是否存在
     * @param code 分类代码
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    Boolean checkCode(String code, String excludeId);
    
    /**
     * 检查分类名称是否存在
     * @param name 分类名称
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    Boolean checkName(String name, String excludeId);
}
