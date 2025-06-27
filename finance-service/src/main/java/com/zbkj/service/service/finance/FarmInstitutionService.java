package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.FarmInstitution;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.finance.FarmInstitutionRequest;
import com.zbkj.common.request.finance.FarmInstitutionSearchRequest;
import com.zbkj.common.vo.finance.FarmInstitutionRequestVO;
import com.zbkj.common.vo.finance.FarmInstitutionResponseVO;

import java.util.List;

/**
 * 养殖场机构信息服务接口
 */
public interface FarmInstitutionService extends IService<FarmInstitution> {
    
    /**
     * 获取养殖场机构信息
     * @param username 用户名
     * @param farmCode 养殖场编码 (可选)
     * @return 机构信息列表
     */
    List<FarmInstitutionResponseVO> getFarmInstitution(String username, String farmCode);
    
    /**
     * 同步会员的养殖场机构信息
     * @param memberId 会员ID
     * @param username 牧码通平台用户名
     * @param farmCode 养殖场编码 (可选)
     * @return 是否成功
     */
    Boolean syncMemberFarmInstitution(Integer memberId, String username, String farmCode);
    
    /**
     * 根据会员ID获取养殖场机构信息
     * @param farmCode 会员code
     * @return 机构信息
     */
    FarmInstitution getFarmInstitutionByMemberId(String farmCode);
    
    /**
     * 根据养殖场编码获取机构信息
     * @param farmCode 养殖场编码
     * @return 机构信息
     */
    FarmInstitution getFarmInstitutionByFarmCode(String farmCode);

    /**
     * 同步养殖场机构信息
     */
    Boolean syncFarmInstitution();
    
    /**
     * 添加养殖场机构信息
     * @param farmInstitution 机构信息
     * @return 是否成功
     */
    Boolean addFarmInstitution(FarmInstitution farmInstitution);
    
    /**
     * 更新养殖场机构信息
     * @param farmInstitution 机构信息
     * @return 是否成功
     */
    Boolean updateFarmInstitution(FarmInstitution farmInstitution);
    
    /**
     * 删除养殖场机构信息
     * @param id 机构ID
     * @return 是否成功
     */
    Boolean deleteFarmInstitution(Integer id);
    
    /**
     * 分页查询养殖场机构信息
     * @param pageParamRequest 分页参数
     * @param keywords 关键词
     * @return 分页结果
     */
    IPage<FarmInstitution> getFarmInstitutionPage(PageParamRequest pageParamRequest, String keywords);

    /**
     * 管理端分页查询养殖场机构信息
     * @param pageParamRequest 分页参数
     * @param searchRequest 搜索条件
     * @return 分页结果
     */
    PageInfo<FarmInstitution> getAdminPage(PageParamRequest pageParamRequest, FarmInstitutionSearchRequest searchRequest);

    /**
     * 添加养殖场机构信息
     * @param request 请求参数
     * @return 是否成功
     */
    Boolean add(FarmInstitutionRequest request);

    /**
     * 编辑养殖场机构信息
     * @param request 请求参数
     * @return 是否成功
     */
    Boolean edit(FarmInstitutionRequest request);

    /**
     * 删除养殖场机构信息
     * @param id 机构ID
     * @return 是否成功
     */
    Boolean delete(String id);

    /**
     * 检查机构标识代码是否存在
     * @param farmCode 机构标识代码
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    Boolean checkFarmCode(String farmCode, String excludeId);

    /**
     * 获取所有养殖场机构列表
     * @return 机构列表
     */
    List<FarmInstitution> getAllList();

    /**
     * 根据机构ID获取养殖场信息
     * @param organizationId 机构ID
     * @return 养殖场信息列表
     */
    List<FarmInstitution> getFarmInstitutionByOrganizationId(String organizationId);

    List<FarmInstitution> getFarmInstitutionNoAdmin();
}