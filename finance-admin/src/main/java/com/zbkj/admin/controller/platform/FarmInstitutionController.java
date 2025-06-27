package com.zbkj.admin.controller.platform;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.finance.FarmInstitution;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.finance.FarmInstitutionRequest;
import com.zbkj.common.request.finance.FarmInstitutionSearchRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.finance.FarmInstitutionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 养殖场机构信息管理控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/finance/farm-institution")
@Api(tags = "平台端养殖场机构信息管理")
public class FarmInstitutionController {

    @Autowired
    private FarmInstitutionService farmInstitutionService;

   // @PreAuthorize("hasAuthority('platform:finance:farm-institution:list')")
    @ApiOperation(value="养殖场机构信息分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public CommonResult<CommonPage<FarmInstitution>> getList(
            @Validated PageParamRequest pageParamRequest,
            @RequestBody(required = false) FarmInstitutionSearchRequest searchRequest) {
        PageInfo<FarmInstitution> pageInfo = farmInstitutionService.getAdminPage(pageParamRequest, searchRequest);
        return CommonResult.success(CommonPage.restPage(pageInfo));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "添加养殖场机构信息")
  //  @PreAuthorize("hasAuthority('platform:finance:farm-institution:add')")
    @ApiOperation(value="添加养殖场机构信息")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<Object> add(@RequestBody @Validated FarmInstitutionRequest request) {
        if (farmInstitutionService.add(request)) {
            return CommonResult.success("添加养殖场机构信息成功");
        }
        return CommonResult.failed("添加养殖场机构信息失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "编辑养殖场机构信息")
  //  @PreAuthorize("hasAuthority('platform:finance:farm-institution:update')")
    @ApiOperation(value="编辑养殖场机构信息")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<Object> update(@RequestBody @Validated FarmInstitutionRequest request) {
        if (farmInstitutionService.edit(request)) {
            return CommonResult.success("编辑养殖场机构信息成功");
        }
        return CommonResult.failed("编辑养殖场机构信息失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除养殖场机构信息")
   // @PreAuthorize("hasAuthority('platform:finance:farm-institution:delete')")
    @ApiOperation(value="删除养殖场机构信息")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<Object> delete(@PathVariable("id") String id) {
        if (farmInstitutionService.delete(id)) {
            return CommonResult.success("删除养殖场机构信息成功");
        }
        return CommonResult.failed("删除养殖场机构信息失败");
    }

  //  @PreAuthorize("hasAuthority('platform:finance:farm-institution:all')")
    @ApiOperation(value="获取全部养殖场机构信息列表")
    @RequestMapping(value = "/all/list", method = RequestMethod.GET)
    public CommonResult<List<FarmInstitution>> allList() {
        return CommonResult.success(farmInstitutionService.getAllList());
    }

   // @PreAuthorize("hasAuthority('platform:finance:farm-institution:detail')")
    @ApiOperation(value="根据ID获取养殖场机构信息详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<FarmInstitution> detail(@PathVariable("id") String id) {
        FarmInstitution institution = farmInstitutionService.getById(id);
        if (institution != null && institution.getDeleteFlag() == 0) {
            return CommonResult.success(institution);
        }
        return CommonResult.failed("机构信息不存在");
    }

    //@PreAuthorize("hasAuthority('platform:finance:farm-institution:sync')")
    @ApiOperation(value="同步养殖场机构信息")
    @RequestMapping(value = "/sync", method = RequestMethod.POST)
    public CommonResult<Object> sync() {
        if (farmInstitutionService.syncFarmInstitution()) {
            return CommonResult.success("同步养殖场机构信息成功");
        }
        return CommonResult.failed("同步养殖场机构信息失败");
    }

   // @PreAuthorize("hasAuthority('platform:finance:farm-institution:check-code')")
    @ApiOperation(value="检查机构标识代码是否存在")
    @RequestMapping(value = "/check-code", method = RequestMethod.GET)
    public CommonResult<Boolean> checkFarmCode(
            @RequestParam("farmCode") String farmCode,
            @RequestParam(value = "excludeId", required = false) String excludeId) {
        Boolean exists = farmInstitutionService.checkFarmCode(farmCode, excludeId);
        return CommonResult.success(exists);
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "审核养殖场机构信息")
  //  @PreAuthorize("hasAuthority('platform:finance:farm-institution:audit')")
    @ApiOperation(value="审核养殖场机构信息")
    @RequestMapping(value = "/audit/{id}", method = RequestMethod.POST)
    public CommonResult<Object> audit(
            @PathVariable("id") String id,
            @RequestParam("auditStatus") Integer auditStatus,
            @RequestParam(value = "auditRemark", required = false) String auditRemark) {
        FarmInstitution institution = farmInstitutionService.getById(id);
        if (institution == null || institution.getDeleteFlag() == 1) {
            return CommonResult.failed("机构信息不存在");
        }
        
        // 这里应该从当前登录用户获取审核员信息
        // 为了演示，暂时使用固定值，实际项目中应该从SecurityContext或Session中获取
        institution.setAuditStatus(auditStatus);
        institution.setAuditRemark(auditRemark);
        institution.setAuditUserId("admin"); // 实际应该从当前登录用户获取
        institution.setAuditUserName("管理员"); // 实际应该从当前登录用户获取
        institution.setAuditTime(new java.util.Date());
        
        if (farmInstitutionService.updateById(institution)) {
            return CommonResult.success("审核操作成功");
        }
        return CommonResult.failed("审核操作失败");
    }
} 