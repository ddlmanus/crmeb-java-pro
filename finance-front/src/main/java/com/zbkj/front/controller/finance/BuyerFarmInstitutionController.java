package com.zbkj.front.controller.finance;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zbkj.common.model.finance.FarmInstitution;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.FarmInstitutionAddRequest;
import com.zbkj.common.vo.finance.FarmInstitutionUpdateRequest;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.finance.FarmInstitutionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 养殖场信息接口信息接口
 */
@Slf4j
@RestController
@Api(tags = "养殖场信息接口")
@RequestMapping("/api/front/finance/farmInstitution")
public class BuyerFarmInstitutionController {

    @Autowired
    private FarmInstitutionService farmInstitutionService;
    @Autowired
    private UserService userService;

    /**
     * 获取当前社员的养殖场机构信息
     */
    @GetMapping("/info")
    @ApiOperation(value = "获取当前社员的养殖场机构信息")
    public CommonResult<FarmInstitution> getCurrentUserFarmInstitution() {
        User currentUser = userService.getInfo();
        if (currentUser == null) {
            return CommonResult.failed("用户不存在");
        }
        return CommonResult.success(farmInstitutionService.getFarmInstitutionByMemberId(currentUser.getFarmCode()));
    }

    /**
     * 同步养殖机构信息
     */
    @GetMapping("/sync")
    @ApiOperation(value = "同步养殖机构信息")
    public CommonResult<Boolean> syncFarmInstitution() {
        return CommonResult.success(farmInstitutionService.syncFarmInstitution());
    }

    /**
     * 分页查询养殖场机构信息
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询养殖场机构信息")
    public CommonResult<IPage<FarmInstitution>> getFarmInstitutionPage(
            @Validated PageParamRequest pageParamRequest,
            @ApiParam(value = "关键词搜索") @RequestParam(value = "keywords", required = false) String keywords) {
        
        IPage<FarmInstitution> page = farmInstitutionService.getFarmInstitutionPage(pageParamRequest, keywords);
        return CommonResult.success(page);
    }

    /**
     * 添加养殖场机构信息
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加养殖场机构信息")
    public CommonResult<Boolean> addFarmInstitution(@Validated @RequestBody FarmInstitutionAddRequest request) {
        User currentUser = userService.getInfo();
        if (currentUser == null) {
            return CommonResult.failed("用户不存在");
        }

        FarmInstitution farmInstitution = new FarmInstitution();
        BeanUtils.copyProperties(request, farmInstitution);
        farmInstitution.setLegalPerson(currentUser.getNickname());

        Boolean result = farmInstitutionService.addFarmInstitution(farmInstitution);
        if (result) {
            return CommonResult.success(result);
        } else {
            return CommonResult.failed("添加失败");
        }
    }

    /**
     * 更新养殖场机构信息
     */
    @PostMapping("/update")
    @ApiOperation(value = "更新养殖场机构信息")
    public CommonResult<Boolean> updateFarmInstitution(@Validated @RequestBody FarmInstitutionUpdateRequest request) {
        User currentUser = userService.getInfo();
        if (currentUser == null) {
            return CommonResult.failed("用户不存在");
        }

        // 检查机构是否存在且属于当前用户
        FarmInstitution existingInstitution = farmInstitutionService.getById(request.getId());
        if (existingInstitution == null || existingInstitution.getDeleteFlag() == 1) {
            return CommonResult.failed("机构不存在");
        }

        FarmInstitution farmInstitution = new FarmInstitution();
        BeanUtils.copyProperties(request, farmInstitution);
        farmInstitution.setLegalPerson(currentUser.getNickname());

        Boolean result = farmInstitutionService.updateFarmInstitution(farmInstitution);
        if (result) {
            return CommonResult.success(result);
        } else {
            return CommonResult.failed("更新失败");
        }
    }

    /**
     * 删除养殖场机构信息
     */
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除养殖场机构信息")
    public CommonResult<Boolean> deleteFarmInstitution(@ApiParam(value = "机构ID") @PathVariable Integer id) {
        User currentUser = userService.getInfo();
        if (currentUser == null) {
            return CommonResult.failed("用户不存在");
        }

        // 检查机构是否存在且属于当前用户
        FarmInstitution existingInstitution = farmInstitutionService.getById(id);
        if (existingInstitution == null || existingInstitution.getDeleteFlag() == 1) {
            return CommonResult.failed("机构不存在");
        }
        Boolean result = farmInstitutionService.deleteFarmInstitution(id);
        if (result) {
            return CommonResult.success(result);
        } else {
            return CommonResult.failed("删除失败");
        }
    }

    /**
     * 根据当前用户的机构ID获取养殖场信息
     */
    @GetMapping("/by-organization")
    @ApiOperation(value = "根据当前用户的机构ID获取养殖场信息")
    public CommonResult<List<FarmInstitution>> getFarmInstitutionByOrganization() {
        try {
            User currentUser = userService.getInfo();
            if (currentUser == null) {
                return CommonResult.failed("用户不存在");
            }

            if (currentUser.getOrganizationId() == null || currentUser.getOrganizationId().isEmpty()) {
                return CommonResult.failed("用户未关联机构");
            }

            List<FarmInstitution> farmInstitutions = farmInstitutionService.getFarmInstitutionByOrganizationId(currentUser.getOrganizationId());
            return CommonResult.success(farmInstitutions);
        } catch (Exception e) {
            log.error("根据机构ID获取养殖场信息失败", e);
            return CommonResult.failed("获取失败：" + e.getMessage());
        }
    }
    
} 