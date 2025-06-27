package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.MerchantEmployee;
import com.zbkj.common.request.merchant.manage.MerchantEmployeeRequest;
import com.zbkj.common.request.merchant.manage.MerchantEmployeeSearchRequest;
import com.zbkj.common.token.FrontTokenComponent;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.LoginFrontUserVo;
import com.zbkj.service.dao.MerchantEmployeeDao;
import com.zbkj.service.service.MerchantEmployeeService;
import com.zbkj.service.service.SystemAttachmentService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* @author dazongzi
* @description MerchantEmployeeServiceImpl 接口实现
* @date 2024-05-24
*/
@Service
public class MerchantEmployeeServiceImpl extends ServiceImpl<MerchantEmployeeDao, MerchantEmployee> implements MerchantEmployeeService {

    @Resource
    private MerchantEmployeeDao dao;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private FrontTokenComponent tokenComponent;

    /**
    * 列表
    * @param request 请求参数
    * @author dazongzi
    * @since 2024-05-24
    * @return List<MerchantEmployee>
    */
    @Override
    public List<MerchantEmployee> getList(MerchantEmployeeSearchRequest request) {
        PageHelper.startPage(request.getPage(), request.getLimit());
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();

        //带 MerchantEmployee 类的多条件查询
        LambdaQueryWrapper<MerchantEmployee> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if(ObjectUtil.isNotEmpty(request.getKeywords())){
            lambdaQueryWrapper.like(MerchantEmployee::getPhone, request.getKeywords())
                    .or()
                    .like(MerchantEmployee::getName, request.getKeywords());
        }
        if(ObjectUtil.isNotEmpty(request.getStatus())){
            lambdaQueryWrapper.eq(MerchantEmployee::getStatus, request.getStatus());
        }
        lambdaQueryWrapper.eq(MerchantEmployee::getMerId, systemAdmin.getMerId());
        lambdaQueryWrapper.orderByDesc(MerchantEmployee::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 新增店铺管理员
     *
     * @param request 当前新增的店铺管理员
     * @return 新增结果
     */
    @Override
    public Boolean addMerchantEmployee(MerchantEmployeeRequest request) {
        // 关联当前商户id
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        // 检查当前新增店铺管理员时候已经存在
        checkEmployeeExistCurrentMerchant(request, systemAdmin);

        MerchantEmployee merchantEmployee = new MerchantEmployee();
        BeanUtils.copyProperties(request, merchantEmployee);
        // 清理素材前缀
        merchantEmployee.setAvatar(systemAttachmentService.clearPrefix(merchantEmployee.getAvatar()));
        merchantEmployee.setMerId(systemAdmin.getMerId()); // 这里拿到的是当前登录商户的商户id 正确数据是大于0的
        return save(merchantEmployee);
    }

    /**
     * 更新店铺管理员
     *
     * @param request 当前新增的店铺管理员
     * @return 更新结果
     */
    @Override
    public Boolean editMerchantEmployee(MerchantEmployeeRequest request) {
        MerchantEmployee merchantEmployee = new MerchantEmployee();
        BeanUtils.copyProperties(request, merchantEmployee);
        merchantEmployee.setAvatar(systemAttachmentService.clearPrefix(merchantEmployee.getAvatar()));
        return updateById(merchantEmployee);
    }

    /**
     * 根据用户id 查询所在店铺管理员列表
     *
     * @param uid 用户id
     * @return 当前用户开启的管理店铺集合
     */
    @Override
    public List<MerchantEmployee> getShopMangerByUserId(Integer uid) {
        LambdaQueryWrapper<MerchantEmployee> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MerchantEmployee::getUid, uid);
        lambdaQueryWrapper.eq(MerchantEmployee::getStatus, Boolean.TRUE);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 检测移动端商户管理权限
     *
     * @param roleNo 权限编号 权限: 1订单管理，2商品管理，3售后管理，4代客下单，5订单核销，5统计
     * @return 是否拥有
     */
    @Override
    public Boolean checkShopMangerRoleByUserId(Integer roleNo) {
        LoginFrontUserVo userForMerchantEmployee = tokenComponent.getUserForMerchantEmployee();
        LambdaQueryWrapper<MerchantEmployee> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MerchantEmployee::getUid, userForMerchantEmployee.getUser().getId())
                .eq(MerchantEmployee::getMerId, userForMerchantEmployee.getActiveMerchant())
                .eq(MerchantEmployee::getStatus, Boolean.TRUE)
                .like(MerchantEmployee::getRole, roleNo);
        List<MerchantEmployee> merchantEmployees = dao.selectList(lambdaQueryWrapper);
        if(merchantEmployees.isEmpty()){
            throw new CrmebException("当前移动端管理员无权限操作");
        }
        return Boolean.TRUE;
    }

    /**
     * 检测当前添加的用户是否已经是当前店铺的员工
     * @param request 当前添加的员工
     */
    private void checkEmployeeExistCurrentMerchant(MerchantEmployeeRequest request, SystemAdmin systemAdmin) {
        LambdaQueryWrapper<MerchantEmployee> queryWrapperExit = Wrappers.lambdaQuery();
        queryWrapperExit.eq(MerchantEmployee::getUid, request.getUid());
        queryWrapperExit.eq(MerchantEmployee::getMerId, systemAdmin.getMerId());
        List<MerchantEmployee> exitEmployees = dao.selectList(queryWrapperExit);
        if(!exitEmployees.isEmpty()){
            throw new CrmebException("当前用户已经是当前店铺的移动端管理员");
        }
    }
}

