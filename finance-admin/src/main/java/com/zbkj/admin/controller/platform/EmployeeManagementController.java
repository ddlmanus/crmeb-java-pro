package com.zbkj.admin.controller.platform;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zbkj.common.request.employee.EmployeePageRequest;
import com.zbkj.common.request.employee.EmployeeRequest;
import com.zbkj.common.response.employee.EmployeeResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.BreedingProductReponse;
import com.zbkj.service.service.EmployeeService;
import com.zbkj.service.service.finance.CreditApplicationService;
import com.zbkj.service.service.finance.FarmInstitutionService;
import com.zbkj.common.model.finance.FarmInstitution;
import com.zbkj.common.response.employee.FarmInstitutionSimpleResponse;

import java.util.List;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 后台员工管理控制器
 * </p>
 *
 * @author zbkj
 * @since 2024-12-19
 */
@RestController
@Api(tags = "后台员工管理接口")
@RequestMapping("/api/admin/platform/employee")
public class EmployeeManagementController {

    @Resource
    private EmployeeService employeeService;

    @Resource
    private CreditApplicationService creditApplicationService;

    @Resource
    private FarmInstitutionService farmInstitutionService;

    /**
     * 分页获取员工列表
     */
    @PostMapping("/page")
    @ApiOperation(value = "分页获取员工列表")
    @PreAuthorize("hasAuthority('platform:employee:page')")
    public CommonResult<IPage<EmployeeResponse>> getEmployeePage(@RequestBody @Validated EmployeePageRequest request) {
        return CommonResult.success(employeeService.getEmployeePage(request));
    }

    /**
     * 获取员工详情
     */
    @GetMapping("/detail/{id}")
    @ApiOperation(value = "获取员工详情")
    @PreAuthorize("hasAuthority('platform:employee:detail')")
    public CommonResult<EmployeeResponse> getEmployeeDetail(
            @ApiParam(value = "员工ID", required = true) @PathVariable("id") Integer id) {
        return CommonResult.success(employeeService.getEmployeeDetail(id));
    }

    /**
     * 新增员工
     */
    @PostMapping("/create")
    @ApiOperation(value = "新增员工")
    @PreAuthorize("hasAuthority('platform:employee:create')")
    public CommonResult<Boolean> createEmployee(@RequestBody @Validated EmployeeRequest request) {
        return CommonResult.success(employeeService.createEmployee(request));
    }

    /**
     * 更新员工信息
     */
    @PostMapping("/update")
    @ApiOperation(value = "更新员工信息")
    @PreAuthorize("hasAuthority('platform:employee:update')")
    public CommonResult<Boolean> updateEmployee(@RequestBody @Validated EmployeeRequest request) {
        return CommonResult.success(employeeService.updateEmployee(request));
    }

    /**
     * 删除员工
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除员工")
    @PreAuthorize("hasAuthority('platform:employee:delete')")
    public CommonResult<Boolean> deleteEmployee(
            @ApiParam(value = "员工ID", required = true) @PathVariable("id") Integer id) {
        return CommonResult.success(employeeService.deleteEmployee(id));
    }

    /**
     * 批量删除员工
     */
    @DeleteMapping("/batchDelete")
    @ApiOperation(value = "批量删除员工")
    @PreAuthorize("hasAuthority('platform:employee:delete')")
    public CommonResult<Boolean> batchDeleteEmployee(
            @ApiParam(value = "员工ID列表，逗号分隔", required = true) @RequestParam("ids") String ids) {
        return CommonResult.success(employeeService.batchDeleteEmployee(ids));
    }

    /**
     * 更新员工状态
     */
    @PostMapping("/updateStatus")
    @ApiOperation(value = "更新员工状态")
    @PreAuthorize("hasAuthority('platform:employee:update')")
    public CommonResult<Boolean> updateEmployeeStatus(
            @ApiParam(value = "员工ID", required = true) @RequestParam("id") Integer id,
            @ApiParam(value = "状态：0离职，1在职，2试用期", required = true) @RequestParam("status") Integer status) {
        return CommonResult.success(employeeService.updateEmployeeStatus(id, status));
    }

    /**
     * 更新员工授信额度
     */
    @PostMapping("/updateCreditLimit")
    @ApiOperation(value = "更新员工授信额度")
    @PreAuthorize("hasAuthority('platform:employee:update')")
    public CommonResult<Boolean> updateEmployeeCreditLimit(
            @ApiParam(value = "员工ID", required = true) @RequestParam("id") Integer id,
            @ApiParam(value = "授信额度", required = true) @RequestParam("creditLimit") java.math.BigDecimal creditLimit,
            @ApiParam(value = "授信系数", required = true) @RequestParam("creditCoefficient") java.math.BigDecimal creditCoefficient) {
        return CommonResult.success(employeeService.updateEmployeeCreditLimit(id, creditLimit, creditCoefficient));
    }

    /**
     * 获取养殖机构列表
     */
    @GetMapping("/farmInstitutions")
    @ApiOperation(value = "获取养殖机构列表")
    @PreAuthorize("hasAuthority('platform:employee:page')")
    public CommonResult<List<FarmInstitutionSimpleResponse>> getFarmInstitutions() {
        List<FarmInstitution> institutions = farmInstitutionService.getAllList();
        List<FarmInstitutionSimpleResponse> result = institutions.stream()
                .map(institution -> new FarmInstitutionSimpleResponse(institution.getId(), institution.getFarmName()))
                .collect(java.util.stream.Collectors.toList());
        return CommonResult.success(result);
    }

    // ==================== 员工授信统计相关接口 ====================

    /**
     * 获取员工授信统计概览数据
     */
    @GetMapping("/credit/statistics")
    @ApiOperation(value = "获取员工授信统计概览数据")
    @PreAuthorize("hasAuthority('platform:employee:page')")
    public CommonResult<com.zbkj.common.response.employee.EmployeeCreditStatisticsResponse> getEmployeeCreditStatistics() {
        return CommonResult.success(employeeService.getEmployeeCreditStatistics());
    }

    /**
     * 分页查询员工授信明细
     */
    @PostMapping("/credit/detailPage")
    @ApiOperation(value = "分页查询员工授信明细")
    @PreAuthorize("hasAuthority('platform:employee:page')")
    public CommonResult<IPage<com.zbkj.common.response.employee.EmployeeCreditDetailResponse>> getEmployeeCreditDetailPage(
            @RequestBody @Validated com.zbkj.common.request.employee.EmployeeCreditPageRequest request) {
        return CommonResult.success(employeeService.getEmployeeCreditDetailPage(request));
    }

    /**
     * 获取员工授信趋势数据
     */
    @GetMapping("/credit/trend")
    @ApiOperation(value = "获取员工授信趋势数据")
    @PreAuthorize("hasAuthority('platform:employee:page')")
    public CommonResult<com.zbkj.common.response.employee.EmployeeCreditTrendResponse> getEmployeeCreditTrend(
            @ApiParam(value = "开始日期") @RequestParam(value = "startDate", required = false) String startDate,
            @ApiParam(value = "结束日期") @RequestParam(value = "endDate", required = false) String endDate) {
        return CommonResult.success(employeeService.getEmployeeCreditTrend(startDate, endDate));
    }

    /**
     * 导出员工授信统计数据
     */
    @GetMapping("/credit/export")
    @ApiOperation(value = "导出员工授信统计数据")
    @PreAuthorize("hasAuthority('platform:employee:page')")
    public void exportEmployeeCreditData(
            @ApiParam(value = "员工姓名") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "手机号") @RequestParam(value = "phone", required = false) String phone,
            @ApiParam(value = "养殖机构ID") @RequestParam(value = "farmInstitutionId", required = false) Integer farmInstitutionId,
            @ApiParam(value = "授信状态") @RequestParam(value = "creditStatus", required = false) String creditStatus,
            javax.servlet.http.HttpServletResponse response) throws Exception {
        employeeService.exportEmployeeCreditData(name, phone, farmInstitutionId, creditStatus, response);
    }


} 