package com.zbkj.service.service.impl.finance;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.finance.OrgCategory;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.finance.OrgCategoryRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.service.dao.finance.OrgCategoryDao;
import com.zbkj.service.service.finance.OrgCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 组织分类服务实现类
 */
@Slf4j
@Service
public class OrgCategoryServiceImpl extends ServiceImpl<OrgCategoryDao, OrgCategory> implements OrgCategoryService {

    @Resource
    private OrgCategoryDao dao;

    /**
     * 根据父级代码获取子分类列表
     * @param parentCode 父级代码
     * @return 子分类列表
     */
    @Override
    public List<OrgCategory> getByParentCode(String parentCode) {
        try {
            LambdaQueryWrapper<OrgCategory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrgCategory::getParentCode, parentCode);
            wrapper.eq(OrgCategory::getDeleteFlag, 0);
            wrapper.eq(OrgCategory::getStatus, 1);
            wrapper.orderByAsc(OrgCategory::getSortOrder);
            return this.list(wrapper);
        } catch (Exception e) {
            log.error("根据父级代码获取子分类列表失败，parentCode: {}", parentCode, e);
            return null;
        }
    }

    /**
     * 根据层级获取分类列表
     * @param level 层级
     * @return 分类列表
     */
    @Override
    public List<OrgCategory> getByLevel(Integer level) {
        try {
            LambdaQueryWrapper<OrgCategory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrgCategory::getLevel, level);
            wrapper.eq(OrgCategory::getDeleteFlag, 0);
            wrapper.eq(OrgCategory::getStatus, 1);
            wrapper.orderByAsc(OrgCategory::getSortOrder);
            return this.list(wrapper);
        } catch (Exception e) {
            log.error("根据层级获取分类列表失败，level: {}", level, e);
            return null;
        }
    }

    /**
     * 根据代码获取分类信息
     * @param code 分类代码
     * @return 分类信息
     */
    @Override
    public OrgCategory getByCode(String code) {
        try {
            LambdaQueryWrapper<OrgCategory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrgCategory::getCode, code);
            wrapper.eq(OrgCategory::getDeleteFlag, 0);
            return this.getOne(wrapper);
        } catch (Exception e) {
            log.error("根据代码获取分类信息失败，code: {}", code, e);
            return null;
        }
    }

    /**
     * 获取所有启用的分类列表
     * @return 分类列表
     */
    @Override
    public List<OrgCategory> getAllEnabled() {
        try {
            LambdaQueryWrapper<OrgCategory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrgCategory::getDeleteFlag, 0);
            wrapper.eq(OrgCategory::getStatus, 1);
            wrapper.orderByAsc(OrgCategory::getLevel, OrgCategory::getSortOrder);
            return this.list(wrapper);
        } catch (Exception e) {
            log.error("获取所有启用的分类列表失败", e);
            return null;
        }
    }

    /**
     * 分页查询养殖机构分类
     * @param pageParamRequest 分页参数
     * @param keywords 关键词
     * @return 分页结果
     */
    @Override
    public PageInfo<OrgCategory> getAdminPage(PageParamRequest pageParamRequest, String keywords) {
        Page<OrgCategory> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<OrgCategory> lqw = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(keywords)) {
            lqw.and(wrapper -> wrapper
                .like(OrgCategory::getName, keywords)
                .or().like(OrgCategory::getCode, keywords)
                .or().like(OrgCategory::getTypeName, keywords)
            );
        }
        
        lqw.eq(OrgCategory::getDeleteFlag, 0);
        lqw.orderByAsc(OrgCategory::getLevel, OrgCategory::getSortOrder);
        List<OrgCategory> categoryList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, categoryList);
    }

    /**
     * 添加养殖机构分类
     * @param request 请求参数
     * @return 是否成功
     */
    @Override
    public Boolean add(OrgCategoryRequest request) {
        // 检查分类代码是否重复
        if (StrUtil.isNotBlank(request.getCode()) && checkCode(request.getCode(), null)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "分类代码已存在");
        }
        
        // 检查分类名称是否重复
        if (checkName(request.getName(), null)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "分类名称已存在");
        }
        
        OrgCategory orgCategory = new OrgCategory();
        if (StrUtil.isBlank(request.getParentCode())) {
            request.setParentCode("0");
        }
        BeanUtils.copyProperties(request, orgCategory);
        orgCategory.setId(null);
        orgCategory.setCreateTime(new Date());
        orgCategory.setUpdateTime(new Date());
        orgCategory.setDeleteFlag(0);
        return dao.insert(orgCategory) > 0;
    }

    /**
     * 编辑养殖机构分类
     * @param request 请求参数
     * @return 是否成功
     */
    @Override
    public Boolean edit(OrgCategoryRequest request) {
        if (StrUtil.isBlank(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "分类ID不能为空");
        }
        
        OrgCategory category = getByIdException(request.getId());
        
        // 检查分类代码是否重复
        if (StrUtil.isNotBlank(request.getCode()) && 
            !request.getCode().equals(category.getCode()) && 
            checkCode(request.getCode(), request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "分类代码已存在");
        }
        
        // 检查分类名称是否重复
        if (!request.getName().equals(category.getName()) && 
            checkName(request.getName(), request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "分类名称已存在");
        }
        
        BeanUtils.copyProperties(request, category);
        category.setUpdateTime(new Date());
        return dao.updateById(category) > 0;
    }

    /**
     * 删除养殖机构分类
     * @param id 分类ID
     * @return 是否成功
     */
    @Override
    public Boolean delete(String id) {
        OrgCategory category = getByIdException(id);
        
        // 检查是否有子分类
        LambdaQueryWrapper<OrgCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgCategory::getParentCode, category.getCode());
        wrapper.eq(OrgCategory::getDeleteFlag, 0);
        long count = dao.selectCount(wrapper);
        if (count > 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "该分类下还有子分类，不能删除");
        }
        
        category.setDeleteFlag(1);
        category.setUpdateTime(new Date());
        return dao.updateById(category) > 0;
    }

    /**
     * 检查分类代码是否存在
     * @param code 分类代码
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    @Override
    public Boolean checkCode(String code, String excludeId) {
        LambdaQueryWrapper<OrgCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgCategory::getCode, code);
        wrapper.eq(OrgCategory::getDeleteFlag, 0);
        if (StrUtil.isNotBlank(excludeId)) {
            wrapper.ne(OrgCategory::getId, excludeId);
        }
        return dao.selectCount(wrapper) > 0;
    }

    /**
     * 检查分类名称是否存在
     * @param name 分类名称
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    @Override
    public Boolean checkName(String name, String excludeId) {
        LambdaQueryWrapper<OrgCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgCategory::getName, name);
        wrapper.eq(OrgCategory::getDeleteFlag, 0);
        if (StrUtil.isNotBlank(excludeId)) {
            wrapper.ne(OrgCategory::getId, excludeId);
        }
        return dao.selectCount(wrapper) > 0;
    }

    /**
     * 根据ID获取分类信息，不存在则抛出异常
     * @param id 分类ID
     * @return 分类信息
     */
    private OrgCategory getByIdException(String id) {
        OrgCategory category = dao.selectById(id);
        if (ObjectUtil.isNull(category) || category.getDeleteFlag() == 1) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "分类不存在");
        }
        return category;
    }
} 