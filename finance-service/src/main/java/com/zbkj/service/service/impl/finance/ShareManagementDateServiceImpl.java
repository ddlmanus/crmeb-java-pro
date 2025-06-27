package com.zbkj.service.service.impl.finance;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.ShareManagementDate;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.vo.finance.ShareManagementDatePageVO;
import com.zbkj.service.dao.finance.ShareManagementDateDao;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.finance.ShareManagementDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ShareManagementDateServiceImpl extends ServiceImpl<ShareManagementDateDao, ShareManagementDate> implements ShareManagementDateService {

    @Autowired
    private UserService userService;
    @Override
    public PageInfo<ShareManagementDate> changeDateList(ShareManagementDatePageVO pageParamRequest) {

        Page<ShareManagementDate> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<ShareManagementDate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShareManagementDate::getUserId, userService.getUserId());
        wrapper.eq(ShareManagementDate::getDeleteFlag, 0);
        if(StringUtils.hasText(pageParamRequest.getChangeTime())){
            wrapper.like(ShareManagementDate::getChangeTime, pageParamRequest.getChangeTime());
        }
        List<ShareManagementDate> list = this.list(wrapper);
        return CommonPage.copyPageInfo(page, list);
    }
}
