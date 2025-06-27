package com.zbkj.service.service.impl.finance;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.enums.AuditStatus;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.finance.FarmInstitution;
import com.zbkj.common.model.finance.Organization;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.finance.FarmInstitutionRequest;
import com.zbkj.common.request.finance.FarmInstitutionSearchRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.vo.finance.ApiResponseVO;
import com.zbkj.common.vo.finance.FarmInstitutionRequestVO;
import com.zbkj.common.vo.finance.FarmInstitutionResponseVO;
import com.zbkj.service.dao.finance.FarmInstitutionDao;
import com.zbkj.service.service.finance.FarmInstitutionService;
import com.zbkj.service.service.finance.OrganizationService;
import com.zbkj.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 养殖场机构信息服务实现
 */
@Slf4j
@Service
public class FarmInstitutionServiceImpl extends ServiceImpl<FarmInstitutionDao, FarmInstitution> implements FarmInstitutionService {

    @Value("${api.base-url:https://mmt.haoyicn.cn}")
    private String apiBaseUrl;

    @Value("${api.token:zAXyemg13M4Lc7tKhigKJ9jmpdiktP7W}")
    private String apiToken;
    
    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UserService userService;

    /**
     * 获取养殖场机构信息
     * @param username 用户名
     * @param farmCode 养殖场编码 (可选)
     * @return 机构信息列表
     */
    @Override
    public List<FarmInstitutionResponseVO> getFarmInstitution(String username, String farmCode) {
        try {
            // 构建请求参数
            Map<String, Object> params = new HashMap<>();
            if(username != null && !username.isEmpty()){
                params.put("username", username);
            }
            if (farmCode != null && !farmCode.isEmpty()) {
                params.put("farm_code", farmCode);
            }
            params.put("page", 1);
            params.put("pageSize", 10000);

            // 发送API请求
            String url = apiBaseUrl + "/data-service/api/farm_farm_user";
            HttpResponse response = HttpRequest.post(url)
                    .header("Content-Type", "application/json")
                    .header("apiToken",  apiToken)
                    .body(JSONUtil.toJsonStr(params))
                    .execute();

            if (!response.isOk()) {
                log.error("获取养殖场机构信息失败: {}", response.body());
                return new ArrayList<>();
            }

            // 解析响应
            ApiResponseVO<Map<String, Object>> apiResponse = JSONUtil.toBean(
                    response.body(),
                    JSONUtil.toBean(response.body(), ApiResponseVO.class).getClass()
            );

            if (apiResponse.getCode() != 0 || apiResponse.getData() == null || apiResponse.getData().getRowData() == null) {
                log.error("获取养殖场机构信息失败，API返回错误: {}", response.body());
                return new ArrayList<>();
            }

            // 转换结果
            List<Map<String, Object>> rawData = apiResponse.getData().getRowData();
            List<FarmInstitutionResponseVO> result = new ArrayList<>();

            for (Map<String, Object> item : rawData) {
                FarmInstitutionResponseVO vo = new FarmInstitutionResponseVO();
                vo.setJgdxlx(getStringValue(item, "jgdxlx"));
                vo.setCode(getStringValue(item, "code"));
                vo.setUserName(getStringValue(item, "user_name"));
                vo.setFarmName(getStringValue(item, "farm_name"));
                vo.setFzrPhone(getStringValue(item, "fzr_phone"));
                vo.setFzr(getStringValue(item, "fzr"));
                vo.setFarmCode(getStringValue(item, "farm_code"));
                result.add(vo);
            }

            return result;

        } catch (Exception e) {
            log.error("获取养殖场机构信息异常", e);
            return new ArrayList<>();
        }
    }

    /**
     * 同步会员的养殖场机构信息
     * @param memberId 会员ID
     * @param username 牧码通平台用户名
     * @param farmCode 养殖场编码 (可选)
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public  Boolean   syncMemberFarmInstitution(Integer memberId, String username, String farmCode) {
        try {
            // 获取养殖场机构信息
            List<FarmInstitutionResponseVO> farmInstitutions = getFarmInstitution(username, farmCode);
            if (farmInstitutions.isEmpty()) {
                log.error("同步会员养殖场机构信息失败：未获取到机构信息, memberId={}, username={}", memberId, username);
                return false;
            }
            /**
             * 存储用户机构信息
             */
            for (FarmInstitutionResponseVO farmInfo : farmInstitutions) {
                //查询是否存在
                FarmInstitution existingInstitution = getFarmInstitutionByFarmCode(farmInfo.getFarmCode());
                if (existingInstitution != null) {
                    continue;
                }
                FarmInstitution farmInstitution = new FarmInstitution();
                farmInstitution.setOrganizationId(""); // 默认为空，需要后续设置
                farmInstitution.setOrganizationName("默认机构");
                farmInstitution.setOrganizationCode("DEFAULT");
                farmInstitution.setFarmCode(farmInfo.getFarmCode());
                farmInstitution.setFarmName(farmInfo.getFarmName());
                farmInstitution.setLegalPerson(farmInfo.getUserName());
                farmInstitution.setContactPhone(farmInfo.getFzrPhone());
                farmInstitution.setContactName(farmInfo.getFzr());
                farmInstitution.setCreateTime(new Date());
                this.saveOrUpdate(farmInstitution);
            }
            return true;
        } catch (Exception e) {
            log.error("同步会员养殖场机构信息异常", e);
            return false;
        }
    }

    /**
     * 根据会员ID获取养殖场机构信息
     * @param farmCode 会员ID
     * @return 机构信息
     */
    @Override
    public FarmInstitution getFarmInstitutionByMemberId(String farmCode) {
        LambdaQueryWrapper<FarmInstitution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FarmInstitution::getFarmCode, farmCode);
        queryWrapper.eq(FarmInstitution::getDeleteFlag, 0);
        return getOne(queryWrapper);
    }

    /**
     * 根据养殖场编码获取机构信息
     * @param farmCode 养殖场编码
     * @return 机构信息
     */
    @Override
    public FarmInstitution getFarmInstitutionByFarmCode(String farmCode) {
        LambdaQueryWrapper<FarmInstitution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FarmInstitution::getFarmCode, farmCode);
        queryWrapper.eq(FarmInstitution::getDeleteFlag, 0);
        return getOne(queryWrapper);
    }

    @Async
    //定时任务每天晚上同步一次
    @Scheduled(cron = "0 0 23 * * ?")
    @Override
    public Boolean syncFarmInstitution() {
        log.info("---开始同步养殖场机构信息------开始时间: {}", CrmebDateUtil.nowDateTime());
        // 获取养殖场机构信息
        List<FarmInstitutionResponseVO> farmInstitutions = getFarmInstitution(null, null);
        if (farmInstitutions.isEmpty()) {
            return false;
        }
        /**
         * 存储用户机构信息
         */
        for (FarmInstitutionResponseVO farmInfo : farmInstitutions) {
            //查询是否存在
            FarmInstitution existingInstitution = getFarmInstitutionByFarmCode(farmInfo.getFarmCode());
            if (existingInstitution != null) {
                continue;
            }
            FarmInstitution farmInstitution = new FarmInstitution();
            farmInstitution.setOrganizationId(""); // 默认为空，需要后续设置
            farmInstitution.setOrganizationName("默认机构");
            farmInstitution.setOrganizationCode("DEFAULT");
            farmInstitution.setFarmCode(farmInfo.getFarmCode());
            farmInstitution.setFarmName(farmInfo.getFarmName());
            farmInstitution.setLegalPerson(farmInfo.getUserName());
            farmInstitution.setContactPhone(farmInfo.getFzrPhone());
            farmInstitution.setContactName(farmInfo.getFzr());
            farmInstitution.setCreateTime(new Date());
            this.saveOrUpdate(farmInstitution);
        }
        return true;
    }

    /**
     * 从Map中获取String值
     */
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    /**
     * 添加养殖场机构信息
     * @param farmInstitution 机构信息
     * @return 是否成功
     */
    @Override
    public Boolean addFarmInstitution(FarmInstitution farmInstitution) {
        try {
            farmInstitution.setCreateTime(new Date());
            farmInstitution.setUpdateTime(new Date());
            farmInstitution.setDeleteFlag(0);
            return this.save(farmInstitution);
        } catch (Exception e) {
            log.error("添加养殖场机构信息失败", e);
            return false;
        }
    }

    /**
     * 更新养殖场机构信息
     * @param farmInstitution 机构信息
     * @return 是否成功
     */
    @Override
    public Boolean updateFarmInstitution(FarmInstitution farmInstitution) {
        try {
            farmInstitution.setUpdateTime(new Date());
            return this.updateById(farmInstitution);
        } catch (Exception e) {
            log.error("更新养殖场机构信息失败", e);
            return false;
        }
    }

    /**
     * 删除养殖场机构信息
     * @param id 机构ID
     * @return 是否成功
     */
    @Override
    public Boolean deleteFarmInstitution(Integer id) {
        try {
            FarmInstitution farmInstitution = new FarmInstitution();
            farmInstitution.setId(id);
            farmInstitution.setDeleteFlag(1);
            farmInstitution.setUpdateTime(new Date());
            return this.updateById(farmInstitution);
        } catch (Exception e) {
            log.error("删除养殖场机构信息失败", e);
            return false;
        }
    }

    /**
     * 分页查询养殖场机构信息
     * @param pageParamRequest 分页参数
     * @param keywords 关键词
     * @return 分页结果
     */
    @Override
    public IPage<FarmInstitution> getFarmInstitutionPage(PageParamRequest pageParamRequest, String keywords) {
        try {
            LambdaQueryWrapper<FarmInstitution> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FarmInstitution::getDeleteFlag, 0);
            
            if (keywords != null && !keywords.trim().isEmpty()) {
                wrapper.and(w -> w.like(FarmInstitution::getFarmName, keywords)
                        .or().like(FarmInstitution::getFarmCode, keywords)
                        .or().like(FarmInstitution::getContactPhone, keywords)
                        .or().like(FarmInstitution::getContactName, keywords));
            }
            
            wrapper.orderByDesc(FarmInstitution::getCreateTime);
            
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<FarmInstitution> page = 
                    new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                            pageParamRequest.getPage(), 
                            pageParamRequest.getLimit()
                    );
            
            return this.page(page, wrapper);
        } catch (Exception e) {
            log.error("分页查询养殖场机构信息失败", e);
            return null;
        }
    }

    /**
     * 管理端分页查询养殖场机构信息
     * @param pageParamRequest 分页参数
     * @param searchRequest 搜索条件
     * @return 分页结果
     */
    @Override
    public PageInfo<FarmInstitution> getAdminPage(PageParamRequest pageParamRequest, FarmInstitutionSearchRequest searchRequest) {
        Page<FarmInstitution> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<FarmInstitution> lqw = new LambdaQueryWrapper<>();
        
        if (searchRequest != null) {
            if (StrUtil.isNotBlank(searchRequest.getFarmCode())) {
                lqw.like(FarmInstitution::getFarmCode, searchRequest.getFarmCode());
            }
            if (StrUtil.isNotBlank(searchRequest.getFarmName())) {
                lqw.like(FarmInstitution::getFarmName, searchRequest.getFarmName());
            }
            if (StrUtil.isNotBlank(searchRequest.getContactName())) {
                lqw.like(FarmInstitution::getContactName, searchRequest.getContactName());
            }
            if (StrUtil.isNotBlank(searchRequest.getContactPhone())) {
                lqw.like(FarmInstitution::getContactPhone, searchRequest.getContactPhone());
            }
            if (StrUtil.isNotBlank(searchRequest.getKeywords())) {
                lqw.and(wrapper -> wrapper
                    .like(FarmInstitution::getFarmName, searchRequest.getKeywords())
                    .or().like(FarmInstitution::getContactName, searchRequest.getKeywords())
                    .or().like(FarmInstitution::getContactPhone, searchRequest.getKeywords())
                    .or().like(FarmInstitution::getFarmCode, searchRequest.getKeywords())
                );
            }
            if (StrUtil.isNotBlank(searchRequest.getDateLimit())) {
                String[] dates = searchRequest.getDateLimit().split(" - ");
                if (dates.length == 2) {
                    lqw.between(FarmInstitution::getCreateTime, dates[0] + " 00:00:00", dates[1] + " 23:59:59");
                }
            }
        }
        
        lqw.eq(FarmInstitution::getDeleteFlag, 0);
        lqw.orderByDesc(FarmInstitution::getCreateTime);
        List<FarmInstitution> institutionList = this.list(lqw);
        return CommonPage.copyPageInfo(page, institutionList);
    }

    /**
     * 添加养殖场机构信息
     * @param request 请求参数
     * @return 是否成功
     */
    @Override
    public Boolean add(FarmInstitutionRequest request) {
        // 检查机构标识代码是否重复
        if (checkFarmCode(request.getFarmCode(), null)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "机构标识代码已存在");
        }
        
        FarmInstitution farmInstitution = new FarmInstitution();
        BeanUtils.copyProperties(request, farmInstitution);
        
        // 设置所属机构信息
        if (StrUtil.isNotBlank(request.getOrganizationId())) {
            Organization organization = organizationService.getById(request.getOrganizationId());
            if (organization != null) {
                farmInstitution.setOrganizationId(request.getOrganizationId());
                farmInstitution.setOrganizationName(organization.getOrgName());
                farmInstitution.setOrganizationCode(organization.getOrgCode());
            }
        }
        farmInstitution.setAuditStatus(AuditStatus.PENDING.getCode());
        farmInstitution.setCreateTime(new Date());
        farmInstitution.setUpdateTime(new Date());
        farmInstitution.setDeleteFlag(0);
        return this.save(farmInstitution);
    }

    /**
     * 编辑养殖场机构信息
     * @param request 请求参数
     * @return 是否成功
     */
    @Override
    public Boolean edit(FarmInstitutionRequest request) {
        if (request.getId() == null) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "机构ID不能为空");
        }
        FarmInstitution institution = getByIdException(request.getId().toString());
        
        // 检查机构标识代码是否重复
        if (!request.getFarmCode().equals(institution.getFarmCode()) && 
            checkFarmCode(request.getFarmCode(), request.getId().toString())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "机构标识代码已存在");
        }
        
        // 设置所属机构信息
        if (StrUtil.isNotBlank(request.getOrganizationId())) {
            Organization organization = organizationService.getById(request.getOrganizationId());
            if (organization != null) {
                institution.setOrganizationId(request.getOrganizationId());
                institution.setOrganizationName(organization.getOrgName());
                institution.setOrganizationCode(organization.getOrgCode());
            }
        }
        
        BeanUtils.copyProperties(request, institution);
        institution.setUpdateTime(new Date());
        return this.updateById(institution);
    }

    /**
     * 删除养殖场机构信息
     * @param id 机构ID
     * @return 是否成功
     */
    @Override
    public Boolean delete(String id) {
        FarmInstitution institution = getByIdException(id);
        institution.setDeleteFlag(1);
        institution.setUpdateTime(new Date());
        return this.updateById(institution);
    }

    /**
     * 检查机构标识代码是否存在
     * @param farmCode 机构标识代码
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    @Override
    public Boolean checkFarmCode(String farmCode, String excludeId) {
        LambdaQueryWrapper<FarmInstitution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FarmInstitution::getFarmCode, farmCode);
        wrapper.eq(FarmInstitution::getDeleteFlag, 0);
        if (StrUtil.isNotBlank(excludeId)) {
            wrapper.ne(FarmInstitution::getId, excludeId);
        }
        return this.count(wrapper) > 0;
    }

    /**
     * 获取所有养殖场机构列表
     * @return 机构列表
     */
    @Override
    public List<FarmInstitution> getAllList() {
        LambdaQueryWrapper<FarmInstitution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FarmInstitution::getDeleteFlag, 0);
        wrapper.orderByDesc(FarmInstitution::getCreateTime);
        return this.list(wrapper);
    }

    /**
     * 根据机构ID获取养殖场信息
     * @param organizationId 机构ID
     * @return 养殖场信息列表
     */
    @Override
    public List<FarmInstitution> getFarmInstitutionByOrganizationId(String organizationId) {
        LambdaQueryWrapper<FarmInstitution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FarmInstitution::getOrganizationId, organizationId);
        wrapper.eq(FarmInstitution::getDeleteFlag, 0);
        wrapper.orderByDesc(FarmInstitution::getCreateTime);
        return this.list(wrapper);
    }

    /**
     * 获取所有养殖场机构列表，用户信息中不存在该养殖场的管理员信息
     */
    @Override
    public List<FarmInstitution> getFarmInstitutionNoAdmin() {
        try {
            // 获取所有养殖场机构信息
            List<FarmInstitution> allFarmInstitutions = getAllList();
            if (allFarmInstitutions.isEmpty()) {
                return new ArrayList<>();
            }

            List<FarmInstitution> result = new ArrayList<>();
            
            // 遍历每个养殖场机构，检查是否存在管理员用户
            for (FarmInstitution farmInstitution : allFarmInstitutions) {
                String farmCode = farmInstitution.getFarmCode();
                if (StrUtil.isBlank(farmCode)) {
                    continue;
                }
                
                // 查询该养殖场下是否存在管理员用户（userType = 1）
                LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
                userWrapper.eq(User::getFarmCode, farmCode);
                userWrapper.eq(User::getUserType, 1); // 1-管理员
                userWrapper.eq(User::getIsLogoff, false); // 未注销
                
                List<User> adminUsers = userService.list(userWrapper);
                
                // 如果该养殖场下不存在管理员用户，则添加到结果列表中
                if (adminUsers.isEmpty()) {
                    result.add(farmInstitution);
                    log.debug("养殖场 {} ({}) 没有管理员用户", farmInstitution.getFarmName(), farmCode);
                } else {
                    log.debug("养殖场 {} ({}) 已有{}个管理员用户", farmInstitution.getFarmName(), farmCode, adminUsers.size());
                }
            }
            
            log.info("共找到{}个没有管理员的养殖场机构", result.size());
            return result;
            
        } catch (Exception e) {
            log.error("获取没有管理员的养殖场机构信息失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据ID获取机构信息，不存在则抛出异常
     * @param id 机构ID
     * @return 机构信息
     */
    private FarmInstitution getByIdException(String id) {
        FarmInstitution institution = this.getById(id);
        if (ObjectUtil.isNull(institution) || institution.getDeleteFlag() == 1) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "机构不存在");
        }
        return institution;
    }
} 