package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.finance.Organization;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.OrganizationRequestVO;
import com.zbkj.common.vo.finance.OrganizationSearchVO;
import com.zbkj.service.service.finance.OrganizationService;
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
 * 机构管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/platform/organization")
@Api(tags = "平台管理 - 机构管理")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    /**
     * 分页查询机构列表
     */
    @PreAuthorize("hasAuthority('platform:organization:list')")
    @ApiOperation(value = "分页查询机构列表", notes = "分页查询机构列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<Organization>> pageList(@Validated OrganizationSearchVO searchVO, @Validated PageParamRequest pageParamRequest) {
        try {
            log.info("分页查询机构列表，页码：{}，页大小：{}", pageParamRequest.getPage(), pageParamRequest.getLimit());
            
            CommonPage<Organization> pageResult = CommonPage.restPage(organizationService.pageList(pageParamRequest, searchVO));
            
            log.info("分页查询机构列表成功，总数：{}", pageResult.getTotal());
            return CommonResult.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询机构列表失败", e);
            return CommonResult.failed("查询失败：" + e.getMessage());
        }
    }

    /**
     * 添加机构
     */
    @PreAuthorize("hasAuthority('platform:organization:add')")
    @ApiOperation(value = "添加机构", notes = "添加新的机构")
    @PostMapping("/add")
    public CommonResult<Boolean> add(
            @ApiParam(value = "添加参数", required = true) @Validated @RequestBody OrganizationRequestVO requestVO) {
        try {
            log.info("添加机构，机构名称：{}，机构编号：{}", requestVO.getOrgName(), requestVO.getOrgCode());
            Boolean result = organizationService.add(requestVO);
            log.info("添加机构成功");
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("添加机构失败", e);
            return CommonResult.failed("添加失败：" + e.getMessage());
        }
    }

    /**
     * 编辑机构
     */
    @PreAuthorize("hasAuthority('platform:organization:edit')")
    @ApiOperation(value = "编辑机构", notes = "编辑机构信息")
    @PostMapping("/edit")
    public CommonResult<Boolean> edit(
            @ApiParam(value = "编辑参数", required = true) @Validated @RequestBody OrganizationRequestVO requestVO) {
        try {
            log.info("编辑机构，ID：{}，机构名称：{}", requestVO.getId(), requestVO.getOrgName());
            Boolean result = organizationService.edit(requestVO);
            log.info("编辑机构成功");
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("编辑机构失败", e);
            return CommonResult.failed("编辑失败：" + e.getMessage());
        }
    }

    /**
     * 删除机构
     */
    @PreAuthorize("hasAuthority('platform:organization:delete')")
    @ApiOperation(value = "删除机构", notes = "删除指定的机构")
    @PostMapping("/delete/{id}")
    public CommonResult<Boolean> delete(
            @ApiParam(value = "机构ID", required = true) @PathVariable("id") String id) {
        try {
            log.info("删除机构，ID：{}", id);
            Boolean result = organizationService.delete(id);
            log.info("删除机构成功");
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("删除机构失败", e);
            return CommonResult.failed("删除失败：" + e.getMessage());
        }
    }

    /**
     * 检查机构编号是否存在
     */
    @ApiOperation(value = "检查机构编号", notes = "检查机构编号是否已存在")
    @GetMapping("/check-code")
    public CommonResult<Boolean> checkOrgCode(
            @ApiParam(value = "机构编号", required = true) @RequestParam("orgCode") String orgCode,
            @ApiParam(value = "排除的ID") @RequestParam(value = "excludeId", required = false) String excludeId) {
        try {
            Boolean exists = organizationService.checkOrgCode(orgCode, excludeId);
            return CommonResult.success(exists);
        } catch (Exception e) {
            log.error("检查机构编号失败", e);
            return CommonResult.failed("检查失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有机构列表
     */
    @ApiOperation(value = "获取所有机构", notes = "获取所有可选的机构列表")
    @GetMapping("/all")
    public CommonResult<List<Organization>> getAllList() {
        try {
            log.info("获取所有机构列表");
            List<Organization> list = organizationService.getAllList();
            log.info("获取所有机构列表成功，共{}个", list.size());
            return CommonResult.success(list);
        } catch (Exception e) {
            log.error("获取所有机构列表失败", e);
            return CommonResult.failed("获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取机构详情
     */
    @ApiOperation(value = "获取机构详情", notes = "根据ID获取机构详细信息")
    @GetMapping("/detail/{id}")
    public CommonResult<Organization> getDetail(
            @ApiParam(value = "机构ID", required = true) @PathVariable("id") String id) {
        try {
            log.info("获取机构详情，ID：{}", id);
            Organization organization = organizationService.getById(id);
            if (organization == null || organization.getDeleteFlag() == 1) {
                return CommonResult.failed("机构不存在");
            }
            log.info("获取机构详情成功");
            return CommonResult.success(organization);
        } catch (Exception e) {
            log.error("获取机构详情失败", e);
            return CommonResult.failed("获取失败：" + e.getMessage());
        }
    }

    /**
     * 根据上级机构ID获取子机构列表
     */
    @ApiOperation(value = "获取子机构列表", notes = "根据上级机构ID获取子机构列表")
    @GetMapping("/children/{parentId}")
    public CommonResult<List<Organization>> getChildren(
            @ApiParam(value = "上级机构ID", required = true) @PathVariable("parentId") String parentId) {
        try {
            log.info("获取子机构列表，上级机构ID：{}", parentId);
            List<Organization> list = organizationService.getByParentId(parentId);
            log.info("获取子机构列表成功，共{}个", list.size());
            return CommonResult.success(list);
        } catch (Exception e) {
            log.error("获取子机构列表失败", e);
            return CommonResult.failed("获取失败：" + e.getMessage());
        }
    }
} 