package com.zbkj.service.service.impl.system;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.system.SystemDictData;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.system.SystemDictDataRequest;
import com.zbkj.common.request.system.SystemDictSearchRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.service.dao.system.SystemDictDataDao;
import com.zbkj.service.service.system.SystemDictDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 字典数据服务实现类
 */
@Slf4j
@Service
public class SystemDictDataServiceImpl extends ServiceImpl<SystemDictDataDao, SystemDictData> implements SystemDictDataService {

    @Resource
    private SystemDictDataDao dao;

    /**
     * 分页查询字典数据
     * @param pageParamRequest 分页参数
     * @param searchRequest 搜索条件
     * @return 分页结果
     */
    @Override
    public PageInfo<SystemDictData> getAdminPage(PageParamRequest pageParamRequest, SystemDictSearchRequest searchRequest) {
        Page<SystemDictData> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<SystemDictData> lqw = new LambdaQueryWrapper<>();
        
        if (searchRequest != null) {
            if (StrUtil.isNotBlank(searchRequest.getDictType())) {
                lqw.eq(SystemDictData::getDictType, searchRequest.getDictType());
            }
            if (StrUtil.isNotBlank(searchRequest.getDictLabel())) {
                lqw.like(SystemDictData::getDictLabel, searchRequest.getDictLabel());
            }
            if (StrUtil.isNotBlank(searchRequest.getDictValue())) {
                lqw.like(SystemDictData::getDictValue, searchRequest.getDictValue());
            }
            if (searchRequest.getStatus() != null) {
                lqw.eq(SystemDictData::getStatus, searchRequest.getStatus());
            }
            if (StrUtil.isNotBlank(searchRequest.getKeywords())) {
                lqw.and(wrapper -> wrapper
                    .like(SystemDictData::getDictLabel, searchRequest.getKeywords())
                    .or().like(SystemDictData::getDictValue, searchRequest.getKeywords())
                    .or().like(SystemDictData::getRemark, searchRequest.getKeywords())
                );
            }
            if (StrUtil.isNotBlank(searchRequest.getDateLimit())) {
                String[] dates = searchRequest.getDateLimit().split(" - ");
                if (dates.length == 2) {
                    lqw.between(SystemDictData::getCreateTime, dates[0] + " 00:00:00", dates[1] + " 23:59:59");
                }
            }
        }
        
        lqw.eq(SystemDictData::getDeleteFlag, 0);
        lqw.orderByAsc(SystemDictData::getDictSort, SystemDictData::getCreateTime);
        List<SystemDictData> dataList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, dataList);
    }

    /**
     * 添加字典数据
     * @param request 请求参数
     * @return 是否成功
     */
    @Override
    public Boolean add(SystemDictDataRequest request) {
        // 检查字典值是否重复
        if (checkDictValue(request.getDictType(), request.getDictValue(), null)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "字典值已存在");
        }
        
        SystemDictData dictData = new SystemDictData();
        BeanUtils.copyProperties(request, dictData);
        dictData.setId(null);
        dictData.setCreateBy("admin"); // 实际应该从当前登录用户获取
        dictData.setCreateTime(new Date());
        dictData.setUpdateTime(new Date());
        dictData.setDeleteFlag(0);
        
        // 如果设置为默认值，需要取消其他的默认值
        if ("Y".equals(request.getIsDefault())) {
            clearDefaultValue(request.getDictType());
        }
        
        return dao.insert(dictData) > 0;
    }

    /**
     * 编辑字典数据
     * @param request 请求参数
     * @return 是否成功
     */
    @Override
    public Boolean edit(SystemDictDataRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "字典数据ID不能为空");
        }
        
        SystemDictData dictData = getByIdException(request.getId());
        
        // 检查字典值是否重复
        if (!request.getDictValue().equals(dictData.getDictValue()) && 
            checkDictValue(request.getDictType(), request.getDictValue(), request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "字典值已存在");
        }
        
        // 如果设置为默认值，需要取消其他的默认值
        if ("Y".equals(request.getIsDefault()) && !"Y".equals(dictData.getIsDefault())) {
            clearDefaultValue(request.getDictType());
        }
        
        BeanUtils.copyProperties(request, dictData);
        dictData.setUpdateBy("admin"); // 实际应该从当前登录用户获取
        dictData.setUpdateTime(new Date());
        return dao.updateById(dictData) > 0;
    }

    /**
     * 删除字典数据
     * @param id 字典数据ID
     * @return 是否成功
     */
    @Override
    public Boolean delete(Long id) {
        SystemDictData dictData = getByIdException(id);
        dictData.setDeleteFlag(1);
        dictData.setUpdateBy("admin"); // 实际应该从当前登录用户获取
        dictData.setUpdateTime(new Date());
        return dao.updateById(dictData) > 0;
    }

    /**
     * 根据字典类型获取字典数据
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    @Override
    public List<SystemDictData> getByDictType(String dictType) {
        LambdaQueryWrapper<SystemDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemDictData::getDictType, dictType);
        wrapper.eq(SystemDictData::getDeleteFlag, 0);
        wrapper.eq(SystemDictData::getStatus, 0);
        wrapper.orderByAsc(SystemDictData::getDictSort);
        return dao.selectList(wrapper);
    }

    /**
     * 根据字典类型和字典值获取字典标签
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    @Override
    public String getDictLabel(String dictType, String dictValue) {
        LambdaQueryWrapper<SystemDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemDictData::getDictType, dictType);
        wrapper.eq(SystemDictData::getDictValue, dictValue);
        wrapper.eq(SystemDictData::getDeleteFlag, 0);
        wrapper.eq(SystemDictData::getStatus, 0);
        SystemDictData dictData = dao.selectOne(wrapper);
        return dictData != null ? dictData.getDictLabel() : dictValue;
    }

    /**
     * 检查字典值是否存在
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    @Override
    public Boolean checkDictValue(String dictType, String dictValue, Long excludeId) {
        LambdaQueryWrapper<SystemDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemDictData::getDictType, dictType);
        wrapper.eq(SystemDictData::getDictValue, dictValue);
        wrapper.eq(SystemDictData::getDeleteFlag, 0);
        if (ObjectUtil.isNotNull(excludeId)) {
            wrapper.ne(SystemDictData::getId, excludeId);
        }
        return dao.selectCount(wrapper) > 0;
    }

    /**
     * 批量删除字典数据
     * @param ids 字典数据ID列表
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
     * 根据字典类型删除字典数据
     * @param dictType 字典类型
     * @return 是否成功
     */
    @Override
    public Boolean deleteByDictType(String dictType) {
        LambdaQueryWrapper<SystemDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemDictData::getDictType, dictType);
        wrapper.eq(SystemDictData::getDeleteFlag, 0);
        List<SystemDictData> dataList = dao.selectList(wrapper);
        
        for (SystemDictData dictData : dataList) {
            dictData.setDeleteFlag(1);
            dictData.setUpdateBy("admin"); // 实际应该从当前登录用户获取
            dictData.setUpdateTime(new Date());
            dao.updateById(dictData);
        }
        return true;
    }

    /**
     * 获取字典数据的默认值
     * @param dictType 字典类型
     * @return 默认字典数据
     */
    @Override
    public SystemDictData getDefaultByDictType(String dictType) {
        LambdaQueryWrapper<SystemDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemDictData::getDictType, dictType);
        wrapper.eq(SystemDictData::getIsDefault, "Y");
        wrapper.eq(SystemDictData::getDeleteFlag, 0);
        wrapper.eq(SystemDictData::getStatus, 0);
        return dao.selectOne(wrapper);
    }

    /**
     * 清除指定字典类型的默认值
     * @param dictType 字典类型
     */
    private void clearDefaultValue(String dictType) {
        LambdaQueryWrapper<SystemDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemDictData::getDictType, dictType);
        wrapper.eq(SystemDictData::getIsDefault, "Y");
        wrapper.eq(SystemDictData::getDeleteFlag, 0);
        List<SystemDictData> dataList = dao.selectList(wrapper);
        
        for (SystemDictData dictData : dataList) {
            dictData.setIsDefault("N");
            dictData.setUpdateBy("admin"); // 实际应该从当前登录用户获取
            dictData.setUpdateTime(new Date());
            dao.updateById(dictData);
        }
    }

    /**
     * 根据ID获取字典数据信息，不存在则抛出异常
     * @param id 字典数据ID
     * @return 字典数据信息
     */
    private SystemDictData getByIdException(Long id) {
        SystemDictData dictData = dao.selectById(id);
        if (ObjectUtil.isNull(dictData) || dictData.getDeleteFlag() == 1) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "字典数据不存在");
        }
        return dictData;
    }
} 