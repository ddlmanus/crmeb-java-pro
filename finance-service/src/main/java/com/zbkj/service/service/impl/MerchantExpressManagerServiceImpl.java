package com.zbkj.service.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.express.Express;
import com.zbkj.common.model.express.MerchantExpress;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.MerchantExpressSearchRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.MerchantResultCode;
import com.zbkj.common.result.SystemConfigResultCode;
import com.zbkj.service.dao.MerchantExpressDao;
import com.zbkj.service.service.ExpressService;
import com.zbkj.service.service.MerchantExpressManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author HZW
 * @description MerchantExpressServiceImpl 接口实现
 * @date 2024-05-08
 */
@Service
public class MerchantExpressManagerServiceImpl extends ServiceImpl<MerchantExpressDao, MerchantExpress> implements MerchantExpressManagerService {

    @Resource
    private MerchantExpressDao dao;

    @Autowired
    private ExpressService expressService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 关联物流公司
     *
     * @param expressId 物流公司ID
     * @return 关联结果
     */
    @Override
    public Boolean relate(Integer expressId, SystemAdmin admin) {

        Express express = expressService.getInfo(expressId);
        if (!express.getIsShow()) {
            throw new CrmebException(SystemConfigResultCode.EXPRESS_NOT_EXIST);
        }

        if (validateMerAndExpress(admin.getMerId(), expressId)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "已关联此物流公司");
        }

        MerchantExpress merchantExpress = new MerchantExpress();
        merchantExpress.setMerId(admin.getMerId());
        merchantExpress.setExpressId(expressId);
        merchantExpress.setCode(express.getCode());
        merchantExpress.setName(express.getName());
        return save(merchantExpress);
    }

    /**
     * 检查商户与物流公司是否关联
     */
    private Boolean validateMerAndExpress(Integer merId, Integer expressId) {
        LambdaQueryWrapper<MerchantExpress> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantExpress::getMerId, merId);
        lqw.eq(MerchantExpress::getExpressId, expressId);
        lqw.eq(MerchantExpress::getIsDelete, 0);
        lqw.last(" limit 1");
        MerchantExpress merchantExpress = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(merchantExpress);
    }

    /**
     * 商户物流公司分页列表
     */
    @Override
    public PageInfo<MerchantExpress> searchPage(MerchantExpressSearchRequest request, SystemAdmin admin) {
        LambdaQueryWrapper<MerchantExpress> lqw = Wrappers.lambdaQuery();
        lqw.select(MerchantExpress::getId, MerchantExpress::getCode, MerchantExpress::getName, MerchantExpress::getIsDefault, MerchantExpress::getIsOpen);
        lqw.eq(MerchantExpress::getMerId, admin.getMerId());
        lqw.eq(MerchantExpress::getIsDelete, 0);
        if (ObjectUtil.isNotNull(request.getOpenStatus())) {
            lqw.eq(MerchantExpress::getIsOpen, request.getOpenStatus());
        }
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            lqw.and(i -> i.like(MerchantExpress::getName, keywords).or().like(MerchantExpress::getCode, keywords));
        }
        lqw.orderByDesc(MerchantExpress::getIsDefault, MerchantExpress::getExpressId);
        Page<MerchantExpress> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<MerchantExpress> merchantExpressList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, merchantExpressList);
    }

    /**
     * 商户物流公司开关
     *
     * @param id 商户物流公司ID
     */
    @Override
    public Boolean openSwitch(Integer id, SystemAdmin admin) {
        MerchantExpress merchantExpress = getByIdException(id);
        if (!admin.getMerId().equals(merchantExpress.getMerId())) {
            throw new CrmebException(MerchantResultCode.MERCHANT_EXPRESS_NOT_EXIST);
        }
        merchantExpress.setIsOpen(!merchantExpress.getIsOpen());
        return updateById(merchantExpress);
    }

    /**
     * 商户物流公司默认开关
     *
     * @param id 商户物流公司ID
     */
    @Override
    public Boolean defaultSwitch(Integer id, SystemAdmin admin) {
        MerchantExpress merchantExpress = getByIdException(id);
        if (!admin.getMerId().equals(merchantExpress.getMerId())) {
            throw new CrmebException(MerchantResultCode.MERCHANT_EXPRESS_NOT_EXIST);
        }
        if (merchantExpress.getIsDefault()) {
            merchantExpress.setIsDefault(false);
            return updateById(merchantExpress);
        }
        merchantExpress.setIsDefault(true);
        return transactionTemplate.execute(e -> {
            cancelDefaultByMerId(merchantExpress.getMerId());
            updateById(merchantExpress);
            return Boolean.TRUE;
        });
    }

    /**
     * 商户物流公司删除
     *
     * @param id 商户物流公司ID
     */
    @Override
    public Boolean delete(Integer id, SystemAdmin admin) {
        MerchantExpress merchantExpress = getByIdException(id);
        if (!admin.getMerId().equals(merchantExpress.getMerId())) {
            throw new CrmebException(MerchantResultCode.MERCHANT_EXPRESS_NOT_EXIST);
        }
        merchantExpress.setIsDelete(true);
        return updateById(merchantExpress);
    }

    private void cancelDefaultByMerId(Integer merId) {
        LambdaUpdateWrapper<MerchantExpress> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(MerchantExpress::getIsDefault, 0);
        wrapper.eq(MerchantExpress::getIsDelete, 0);
        wrapper.eq(MerchantExpress::getMerId, merId);
        update(wrapper);
    }

    private MerchantExpress getByIdException(Integer id) {
        MerchantExpress merchantExpress = getById(id);
        if (ObjectUtil.isNull(merchantExpress)) {
            throw new CrmebException(MerchantResultCode.MERCHANT_EXPRESS_NOT_EXIST);
        }
        return merchantExpress;
    }
}

