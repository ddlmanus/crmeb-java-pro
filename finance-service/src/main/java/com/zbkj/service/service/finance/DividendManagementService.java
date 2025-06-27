package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.finance.DividendManagement;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.finance.DividendCreateRequest;
import com.zbkj.common.vo.finance.DividendManagementPageVO;
import com.zbkj.common.vo.finance.DividendManagementVO;
import com.zbkj.common.vo.finance.DividendStatisticsVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 分红管理服务接口
 */
public interface DividendManagementService extends IService<DividendManagement> {

    Boolean addDividend(DividendCreateRequest request);

    List<DividendManagementVO> dividendList(DividendManagementPageVO pageParamRequest);

    /**
     * 导出分红管理数据
     * @param pageVO 查询参数
     * @param response HTTP响应对象
     * @throws Exception 导出异常
     */
    void exportDividendData(DividendManagementPageVO pageVO, HttpServletResponse response) throws Exception;

    List<DividendManagementVO> dividendAdminList(DividendManagementPageVO request);
} 