package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.ShareManagementDate;
import com.zbkj.common.vo.finance.ShareManagementDatePageVO;

public interface ShareManagementDateService extends IService<ShareManagementDate> {
    PageInfo<ShareManagementDate> changeDateList(ShareManagementDatePageVO pageParamRequest);
}
