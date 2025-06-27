package com.zbkj.service.service.impl.system;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.system.SystemDictType;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.system.SystemDictSearchRequest;
import com.zbkj.common.request.system.SystemDictTypeRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.service.dao.system.SystemDictTypeDao;
import com.zbkj.service.service.system.SystemDictDataService;
import com.zbkj.service.service.system.SystemDictTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 字典类型服务实现类
 */
@Slf4j
@Service
public class SystemDictTypeServiceImpl extends ServiceImpl<SystemDictTypeDao, SystemDictType> implements SystemDictTypeService {

    @Resource
    private SystemDictTypeDao dao;

    @Autowired
    private SystemDictDataService systemDictDataService;

    /**
     * 分页查询字典类型
     * @param pageParamRequest 分页参数
     * @param searchRequest 搜索条件
     * @return 分页结果
     */
    @Override
    public PageInfo<SystemDictType> getAdminPage(PageParamRequest pageParamRequest, SystemDictSearchRequest searchRequest) {
        Page<SystemDictType> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<SystemDictType> lqw = new LambdaQueryWrapper<>();
        
        if (searchRequest != null) {
            if (StrUtil.isNotBlank(searchRequest.getDictName())) {
                lqw.like(SystemDictType::getDictName, searchRequest.getDictName());
            }
            if (StrUtil.isNotBlank(searchRequest.getDictType())) {
                lqw.like(SystemDictType::getDictType, searchRequest.getDictType());
            }
            if (searchRequest.getStatus() != null) {
                lqw.eq(SystemDictType::getStatus, searchRequest.getStatus());
            }
            if (StrUtil.isNotBlank(searchRequest.getKeywords())) {
                lqw.and(wrapper -> wrapper
                    .like(SystemDictType::getDictName, searchRequest.getKeywords())
                    .or().like(SystemDictType::getDictType, searchRequest.getKeywords())
                    .or().like(SystemDictType::getRemark, searchRequest.getKeywords())
                );
            }
            if (StrUtil.isNotBlank(searchRequest.getDateLimit())) {
                String[] dates = searchRequest.getDateLimit().split(" - ");
                if (dates.length == 2) {
                    lqw.between(SystemDictType::getCreateTime, dates[0] + " 00:00:00", dates[1] + " 23:59:59");
                }
            }
        }
        
        lqw.eq(SystemDictType::getDeleteFlag, 0);
        lqw.orderByDesc(SystemDictType::getCreateTime);
        List<SystemDictType> typeList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, typeList);
    }

    /**
     * 添加字典类型
     * @param request 请求参数
     * @return 是否成功
     */
    @Override
    public Boolean add(SystemDictTypeRequest request) {
        // 检查字典类型是否重复
        if (checkDictType(request.getDictType(), null)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "字典类型已存在");
        }
        
        SystemDictType dictType = new SystemDictType();
        BeanUtils.copyProperties(request, dictType);
        dictType.setId(null);
        dictType.setCreateBy("admin"); // 实际应该从当前登录用户获取
        dictType.setCreateTime(new Date());
        dictType.setUpdateTime(new Date());
        dictType.setDeleteFlag(0);
        return dao.insert(dictType) > 0;
    }

    /**
     * 编辑字典类型
     * @param request 请求参数
     * @return 是否成功
     */
    @Override
    public Boolean edit(SystemDictTypeRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "字典类型ID不能为空");
        }
        
        SystemDictType dictType = getByIdException(request.getId());
        
        // 检查字典类型是否重复
        if (!request.getDictType().equals(dictType.getDictType()) && 
            checkDictType(request.getDictType(), request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "字典类型已存在");
        }
        
        BeanUtils.copyProperties(request, dictType);
        dictType.setUpdateBy("admin"); // 实际应该从当前登录用户获取
        dictType.setUpdateTime(new Date());
        return dao.updateById(dictType) > 0;
    }

    /**
     * 删除字典类型
     * @param id 字典类型ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        SystemDictType dictType = getByIdException(id);
        
        // 删除相关的字典数据
        systemDictDataService.deleteByDictType(dictType.getDictType());
        
        // 删除字典类型
        dictType.setDeleteFlag(1);
        dictType.setUpdateBy("admin"); // 实际应该从当前登录用户获取
        dictType.setUpdateTime(new Date());
        return dao.updateById(dictType) > 0;
    }

    /**
     * 检查字典类型是否存在
     * @param dictType 字典类型
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    @Override
    public Boolean checkDictType(String dictType, Long excludeId) {
        LambdaQueryWrapper<SystemDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemDictType::getDictType, dictType);
        wrapper.eq(SystemDictType::getDeleteFlag, 0);
        if (ObjectUtil.isNotNull(excludeId)) {
            wrapper.ne(SystemDictType::getId, excludeId);
        }
        return dao.selectCount(wrapper) > 0;
    }

    /**
     * 获取所有正常状态的字典类型
     * @return 字典类型列表
     */
    @Override
    public List<SystemDictType> getAllEnabled() {
        LambdaQueryWrapper<SystemDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemDictType::getDeleteFlag, 0);
        wrapper.eq(SystemDictType::getStatus, 0);
        wrapper.orderByDesc(SystemDictType::getCreateTime);
        return dao.selectList(wrapper);
    }

    /**
     * 根据字典类型获取字典信息
     * @param dictType 字典类型
     * @return 字典类型信息
     */
    @Override
    public SystemDictType getByDictType(String dictType) {
        LambdaQueryWrapper<SystemDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemDictType::getDictType, dictType);
        wrapper.eq(SystemDictType::getDeleteFlag, 0);
        return dao.selectOne(wrapper);
    }

    /**
     * 批量删除字典类型
     * @param ids 字典类型ID列表
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            delete(id);
        }
        return true;
    }

    /**
     * 根据ID获取字典类型信息，不存在则抛出异常
     * @param id 字典类型ID
     * @return 字典类型信息
     */
    private SystemDictType getByIdException(Long id) {
        SystemDictType dictType = dao.selectById(id);
        if (ObjectUtil.isNull(dictType) || dictType.getDeleteFlag() == 1) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "字典类型不存在");
        }
        return dictType;
    }
} 