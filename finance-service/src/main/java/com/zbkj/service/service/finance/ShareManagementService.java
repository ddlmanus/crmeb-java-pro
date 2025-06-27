package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.ShareChangeRecord;
import com.zbkj.common.model.finance.ShareManagement;
import com.zbkj.common.model.finance.ShareManagementDate;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.vo.finance.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 股份管理服务接口
 */
public interface ShareManagementService extends IService<ShareManagement> {

    /**
     * 股份变更
     * @param shareVO
     * @return
     */
     Boolean addShare(ShareUpdateRequest shareVO);

    /**
     * 获取变更列表
     * @param pageParamRequest
     * @return
     */
    List<ShareManagement> changeList(ShareManagementPageVO pageParamRequest);

    /**
     * 导出股份管理数据
     * @param pageVO 查询参数
     * @param response HTTP响应对象
     * @throws Exception 导出异常
     */
    void exportShareData(ShareManagementPageVO pageVO, HttpServletResponse response) throws Exception;

    List<ShareManagement> getAdminList(ShareManagementPageVO request);

    PageInfo<ShareManagementDate> changeDateList(ShareManagementDatePageVO pageParamRequest);

    List<ShareManagement> changeDateDetail(String id);

    List<ShareChangeRecord> changeRecordList(String id);
}