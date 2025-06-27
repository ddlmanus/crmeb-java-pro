package com.zbkj.service.service.impl.finance;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.finance.OrgCategory;
import com.zbkj.common.model.finance.Organization;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.vo.finance.OrganizationRequestVO;
import com.zbkj.common.vo.finance.OrganizationSearchVO;
import com.zbkj.service.dao.finance.OrganizationDao;
import com.zbkj.service.service.finance.OrgCategoryService;
import com.zbkj.service.service.finance.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 机构管理服务实现类
 */
@Slf4j
@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationDao, Organization> implements OrganizationService {

    @Resource
    private OrganizationDao organizationDao;

    @Autowired
    private OrgCategoryService orgCategoryService;

    @Override
    public List<Organization> pageList(PageParamRequest pageParamRequest, OrganizationSearchVO searchVO) {
        try {
            log.info("分页查询机构列表，页码：{}，大小：{}", pageParamRequest.getPage(), pageParamRequest.getLimit());
            
            // 使用PageHelper分页
            PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
            
            LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Organization::getDeleteFlag, 0);
            
            // 添加搜索条件
            if (searchVO != null) {
                if (StrUtil.isNotBlank(searchVO.getKeywords())) {
                    wrapper.and(w -> w.like(Organization::getOrgName, searchVO.getKeywords())
                                    .or().like(Organization::getOrgCode, searchVO.getKeywords()));
                }
                if (StrUtil.isNotBlank(searchVO.getOrgName())) {
                    wrapper.like(Organization::getOrgName, searchVO.getOrgName());
                }
                if (StrUtil.isNotBlank(searchVO.getOrgCode())) {
                    wrapper.like(Organization::getOrgCode, searchVO.getOrgCode());
                }
                if (StrUtil.isNotBlank(searchVO.getRegion())) {
                    wrapper.like(Organization::getRegion, searchVO.getRegion());
                }
                if (StrUtil.isNotBlank(searchVO.getParentOrgId())) {
                    wrapper.eq(Organization::getParentOrgId, searchVO.getParentOrgId());
                }
                if (StrUtil.isNotBlank(searchVO.getCategoryId())) {
                    wrapper.eq(Organization::getCategoryId, searchVO.getCategoryId());
                }
                if (searchVO.getStatus() != null) {
                    wrapper.eq(Organization::getStatus, searchVO.getStatus());
                }
                if (StrUtil.isNotBlank(searchVO.getDateLimit())) {
                    String[] dates = searchVO.getDateLimit().split(" - ");
                    if (dates.length == 2) {
                        wrapper.between(Organization::getCreateTime, dates[0] + " 00:00:00", dates[1] + " 23:59:59");
                    }
                }
            }
            
            wrapper.orderByAsc(Organization::getSortOrder).orderByDesc(Organization::getCreateTime);
            
            List<Organization> list = this.list(wrapper);
            log.info("分页查询机构列表成功，数据量：{}", list.size());
            return list;
        } catch (Exception e) {
            log.error("分页查询机构列表失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean add(OrganizationRequestVO requestVO) {
        try {
            log.info("添加机构，机构名称：{}，机构编号：{}", requestVO.getOrgName(), requestVO.getOrgCode());

            // 检查机构编号是否重复
            if (checkOrgCode(requestVO.getOrgCode(), null)) {
                throw new CrmebException("机构编号已存在");
            }

            Organization organization = new Organization();
            BeanUtils.copyProperties(requestVO, organization);

            // 设置机构分类信息
            if (StrUtil.isNotBlank(requestVO.getCategoryId())) {
                OrgCategory category = orgCategoryService.getById(requestVO.getCategoryId());
                if (category != null) {
                    organization.setCategoryName(category.getName());
                    organization.setCategoryCode(category.getCode());
                }
            }

            // 设置上级机构信息
            if (StrUtil.isNotBlank(requestVO.getParentOrgId())) {
                Organization parentOrg = this.getById(requestVO.getParentOrgId());
                if (parentOrg != null) {
                    organization.setParentOrgName(parentOrg.getOrgName());
                    organization.setParentOrgCode(parentOrg.getOrgCode());
                    organization.setLevel(parentOrg.getLevel() + 1);
                } else {
                    organization.setLevel(1);
                }
            } else {
                organization.setLevel(1);
            }

            // 组装区域信息
            StringBuilder regionBuilder = new StringBuilder();
            if (StrUtil.isNotBlank(requestVO.getProvince())) {
                regionBuilder.append(requestVO.getProvince());
            }
            if (StrUtil.isNotBlank(requestVO.getCity())) {
                if (regionBuilder.length() > 0) regionBuilder.append(" ");
                regionBuilder.append(requestVO.getCity());
            }
            if (StrUtil.isNotBlank(requestVO.getDistrict())) {
                if (regionBuilder.length() > 0) regionBuilder.append(" ");
                regionBuilder.append(requestVO.getDistrict());
            }
            organization.setRegion(regionBuilder.toString());

            organization.setCreateTime(new Date());
            organization.setUpdateTime(new Date());
            organization.setDeleteFlag(0);
            organization.setStatus(requestVO.getStatus() != null ? requestVO.getStatus() : 1);

            boolean result = this.save(organization);
            log.info("添加机构{}，结果：{}", result ? "成功" : "失败", result);
            return result;
        } catch (Exception e) {
            log.error("添加机构失败", e);
            throw new CrmebException("添加机构失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean edit(OrganizationRequestVO requestVO) {
        try {
            log.info("编辑机构，ID：{}，机构名称：{}", requestVO.getId(), requestVO.getOrgName());

            if (StrUtil.isBlank(requestVO.getId())) {
                throw new CrmebException("机构ID不能为空");
            }

            Organization existOrg = this.getById(requestVO.getId());
            if (existOrg == null) {
                throw new CrmebException("机构不存在");
            }

            // 检查机构编号是否重复
            if (checkOrgCode(requestVO.getOrgCode(), requestVO.getId())) {
                throw new CrmebException("机构编号已存在");
            }

            Organization organization = new Organization();
            BeanUtils.copyProperties(requestVO, organization);

            // 设置机构分类信息
            if (StrUtil.isNotBlank(requestVO.getCategoryId())) {
                OrgCategory category = orgCategoryService.getById(requestVO.getCategoryId());
                if (category != null) {
                    organization.setCategoryName(category.getName());
                    organization.setCategoryCode(category.getCode());
                }
            }

            // 设置上级机构信息
            if (StrUtil.isNotBlank(requestVO.getParentOrgId())) {
                Organization parentOrg = this.getById(requestVO.getParentOrgId());
                if (parentOrg != null) {
                    organization.setParentOrgName(parentOrg.getOrgName());
                    organization.setParentOrgCode(parentOrg.getOrgCode());
                    organization.setLevel(parentOrg.getLevel() + 1);
                } else {
                    organization.setLevel(1);
                }
            } else {
                organization.setLevel(1);
            }

            // 组装区域信息
            StringBuilder regionBuilder = new StringBuilder();
            if (StrUtil.isNotBlank(requestVO.getProvince())) {
                regionBuilder.append(requestVO.getProvince());
            }
            if (StrUtil.isNotBlank(requestVO.getCity())) {
                if (regionBuilder.length() > 0) regionBuilder.append(" ");
                regionBuilder.append(requestVO.getCity());
            }
            if (StrUtil.isNotBlank(requestVO.getDistrict())) {
                if (regionBuilder.length() > 0) regionBuilder.append(" ");
                regionBuilder.append(requestVO.getDistrict());
            }
            organization.setRegion(regionBuilder.toString());

            organization.setUpdateTime(new Date());
            organization.setCreateTime(existOrg.getCreateTime());
            organization.setDeleteFlag(0);

            boolean result = this.updateById(organization);
            log.info("编辑机构{}，结果：{}", result ? "成功" : "失败", result);
            return result;
        } catch (Exception e) {
            log.error("编辑机构失败", e);
            throw new CrmebException("编辑机构失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(String id) {
        try {
            log.info("删除机构，ID：{}", id);

            if (StrUtil.isBlank(id)) {
                throw new CrmebException("机构ID不能为空");
            }

            Organization organization = this.getById(id);
            if (organization == null) {
                throw new CrmebException("机构不存在");
            }

            // 检查是否有子机构
            List<Organization> children = getByParentId(id);
            if (!children.isEmpty()) {
                throw new CrmebException("存在下级机构，无法删除");
            }

            // 软删除
            organization.setDeleteFlag(1);
            organization.setUpdateTime(new Date());

            boolean result = this.updateById(organization);
            log.info("删除机构{}，结果：{}", result ? "成功" : "失败", result);
            return result;
        } catch (Exception e) {
            log.error("删除机构失败", e);
            throw new CrmebException("删除机构失败：" + e.getMessage());
        }
    }

    @Override
    public Boolean checkOrgCode(String orgCode, String excludeId) {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Organization::getOrgCode, orgCode);
        wrapper.eq(Organization::getDeleteFlag, 0);
        if (StrUtil.isNotBlank(excludeId)) {
            wrapper.ne(Organization::getId, excludeId);
        }
        return this.count(wrapper) > 0;
    }

    @Override
    public List<Organization> getAllList() {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Organization::getDeleteFlag, 0);
        wrapper.eq(Organization::getStatus, 1);
        wrapper.orderByAsc(Organization::getSortOrder).orderByDesc(Organization::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<Organization> getByParentId(String parentOrgId) {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Organization::getParentOrgId, parentOrgId);
        wrapper.eq(Organization::getDeleteFlag, 0);
        wrapper.orderByAsc(Organization::getSortOrder).orderByDesc(Organization::getCreateTime);
        return this.list(wrapper);
    }
} 