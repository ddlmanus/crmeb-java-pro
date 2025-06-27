package com.zbkj.service.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.system.SystemDictData;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.system.SystemDictDataRequest;
import com.zbkj.common.request.system.SystemDictSearchRequest;

import java.util.List;

/**
 * 字典数据服务接口
 */
public interface SystemDictDataService extends IService<SystemDictData> {

    /**
     * 分页查询字典数据
     * @param pageParamRequest 分页参数
     * @param searchRequest 搜索条件
     * @return 分页结果
     */
    PageInfo<SystemDictData> getAdminPage(PageParamRequest pageParamRequest, SystemDictSearchRequest searchRequest);

    /**
     * 添加字典数据
     * @param request 请求参数
     * @return 是否成功
     */
    Boolean add(SystemDictDataRequest request);

    /**
     * 编辑字典数据
     * @param request 请求参数
     * @return 是否成功
     */
    Boolean edit(SystemDictDataRequest request);

    /**
     * 删除字典数据
     * @param id 字典数据ID
     * @return 是否成功
     */
    Boolean delete(Long id);

    /**
     * 根据字典类型获取字典数据
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    List<SystemDictData> getByDictType(String dictType);

    /**
     * 根据字典类型和字典值获取字典标签
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    String getDictLabel(String dictType, String dictValue);

    /**
     * 检查字典值是否存在
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    Boolean checkDictValue(String dictType, String dictValue, Long excludeId);

    /**
     * 批量删除字典数据
     * @param ids 字典数据ID列表
     * @return 是否成功
     */
    Boolean deleteBatch(List<Long> ids);

    /**
     * 根据字典类型删除字典数据
     * @param dictType 字典类型
     * @return 是否成功
     */
    Boolean deleteByDictType(String dictType);

    /**
     * 获取字典数据的默认值
     * @param dictType 字典类型
     * @return 默认字典数据
     */
    SystemDictData getDefaultByDictType(String dictType);
} 