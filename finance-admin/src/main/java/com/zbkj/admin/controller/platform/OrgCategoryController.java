package com.zbkj.admin.controller.platform;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.finance.OrgCategory;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.finance.OrgCategoryRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.finance.OrgCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 养殖机构分类管理控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/finance/org-category")
@Api(tags = "平台端养殖机构分类管理")
public class OrgCategoryController {

    @Autowired
    private OrgCategoryService orgCategoryService;

  //  @PreAuthorize("hasAuthority('platform:finance:org-category:list')")
    @ApiOperation(value="养殖机构分类分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<OrgCategory>> getList(
            @Validated PageParamRequest pageParamRequest,
            @ApiParam(value = "关键词") @RequestParam(value = "keywords", required = false) String keywords) {
        PageInfo<OrgCategory> pageInfo = orgCategoryService.getAdminPage(pageParamRequest, keywords);
        return CommonResult.success(CommonPage.restPage(pageInfo));
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.ADD, description = "添加养殖机构分类")
   // @PreAuthorize("hasAuthority('platform:finance:org-category:add')")
    @ApiOperation(value="添加养殖机构分类")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<Object> add(@RequestBody @Validated OrgCategoryRequest request) {
        if (orgCategoryService.add(request)) {
            return CommonResult.success("添加养殖机构分类成功");
        }
        return CommonResult.failed("添加养殖机构分类失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "编辑养殖机构分类")
  //  @PreAuthorize("hasAuthority('platform:finance:org-category:update')")
    @ApiOperation(value="编辑养殖机构分类")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<Object> update(@RequestBody @Validated OrgCategoryRequest request) {
        if (orgCategoryService.edit(request)) {
            return CommonResult.success("编辑养殖机构分类成功");
        }
        return CommonResult.failed("编辑养殖机构分类失败");
    }

    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除养殖机构分类")
   // @PreAuthorize("hasAuthority('platform:finance:org-category:delete')")
    @ApiOperation(value="删除养殖机构分类")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<Object> delete(@PathVariable("id") String id) {
        if (orgCategoryService.delete(id)) {
            return CommonResult.success("删除养殖机构分类成功");
        }
        return CommonResult.failed("删除养殖机构分类失败");
    }

   // @PreAuthorize("hasAuthority('platform:finance:org-category:all')")
    @ApiOperation(value="获取全部启用的养殖机构分类列表")
    @RequestMapping(value = "/all/list", method = RequestMethod.GET)
    public CommonResult<List<OrgCategory>> allList() {
        return CommonResult.success(orgCategoryService.getAllEnabled());
    }

   // @PreAuthorize("hasAuthority('platform:finance:org-category:detail')")
    @ApiOperation(value="根据ID获取养殖机构分类详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<OrgCategory> detail(@PathVariable("id") String id) {
        OrgCategory category = orgCategoryService.getById(id);
        if (category != null && category.getDeleteFlag() == 0) {
            return CommonResult.success(category);
        }
        return CommonResult.failed("分类不存在");
    }

  //  @PreAuthorize("hasAuthority('platform:finance:org-category:level')")
    @ApiOperation(value="根据层级获取分类列表")
    @RequestMapping(value = "/level/{level}", method = RequestMethod.GET)
    public CommonResult<List<OrgCategory>> getByLevel(@PathVariable("level") Integer level) {
        return CommonResult.success(orgCategoryService.getByLevel(level));
    }

   // @PreAuthorize("hasAuthority('platform:finance:org-category:parent')")
    @ApiOperation(value="根据父级代码获取子分类列表")
    @RequestMapping(value = "/parent/{parentCode}", method = RequestMethod.GET)
    public CommonResult<List<OrgCategory>> getByParentCode(@PathVariable("parentCode") String parentCode) {
        return CommonResult.success(orgCategoryService.getByParentCode(parentCode));
    }
} 