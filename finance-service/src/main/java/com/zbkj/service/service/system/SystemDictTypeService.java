package com.zbkj.service.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.system.SystemDictType;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.system.SystemDictSearchRequest;
import com.zbkj.common.request.system.SystemDictTypeRequest;

import java.util.List;

/**
 * 字典类型服务接口
 */
public interface SystemDictTypeService extends IService<SystemDictType> {

    /**
     * 分页查询字典类型
     * @param pageParamRequest 分页参数
     * @param searchRequest 搜索条件
     * @return 分页结果
     */
    PageInfo<SystemDictType> getAdminPage(PageParamRequest pageParamRequest, SystemDictSearchRequest searchRequest);

    /**
     * 添加字典类型
     * @param request 请求参数
     * @return 是否成功
     */
    Boolean add(SystemDictTypeRequest request);

    /**
     * 编辑字典类型
     * @param request 请求参数
     * @return 是否成功
     */
    Boolean edit(SystemDictTypeRequest request);

    /**
     * 删除字典类型
     * @param id 字典类型ID
     * @return 是否成功
     */
    Boolean delete(Long id);

    /**
     * 检查字典类型是否存在
     * @param dictType 字典类型
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    Boolean checkDictType(String dictType, Long excludeId);

    /**
     * 获取所有正常状态的字典类型
     * @return 字典类型列表
     */
    List<SystemDictType> getAllEnabled();

    /**
     * 根据字典类型获取字典信息
     * @param dictType 字典类型
     * @return 字典类型信息
     */
    SystemDictType getByDictType(String dictType);

    /**
     * 批量删除字典类型
     * @param ids 字典类型ID列表
     * @return 是否成功
     */
    Boolean deleteBatch(List<Long> ids);
} 