package com.zbkj.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.employee.Employee;
import com.zbkj.common.request.employee.EmployeePageRequest;
import com.zbkj.common.request.employee.EmployeeRequest;
import com.zbkj.common.response.employee.EmployeeResponse;

/**
 * <p>
 * 员工表 服务类
 * </p>
 *
 * @author zbkj
 * @since 2024-12-19
 */
public interface EmployeeService extends IService<Employee> {

    /**
     * 分页获取员工列表
     * @param request 分页查询参数
     * @return 员工分页列表
     */
    IPage<EmployeeResponse> getEmployeePage(EmployeePageRequest request);

    /**
     * 根据ID获取员工详情
     * @param id 员工ID
     * @return 员工详情
     */
    EmployeeResponse getEmployeeDetail(Integer id);

    /**
     * 新增员工
     * @param request 员工信息
     * @return 是否成功
     */
    Boolean createEmployee(EmployeeRequest request);

    /**
     * 更新员工信息
     * @param request 员工信息
     * @return 是否成功
     */
    Boolean updateEmployee(EmployeeRequest request);

    /**
     * 删除员工
     * @param id 员工ID
     * @return 是否成功
     */
    Boolean deleteEmployee(Integer id);

    /**
     * 批量删除员工
     * @param ids 员工ID列表
     * @return 是否成功
     */
    Boolean batchDeleteEmployee(String ids);

    /**
     * 更新员工状态
     * @param id 员工ID
     * @param status 状态
     * @return 是否成功
     */
    Boolean updateEmployeeStatus(Integer id, Integer status);

    /**
     * 更新员工授信额度
     * @param id 员工ID
     * @param creditLimit 授信额度
     * @param creditCoefficient 授信系数
     * @return 是否成功
     */
    Boolean updateEmployeeCreditLimit(Integer id, java.math.BigDecimal creditLimit, java.math.BigDecimal creditCoefficient);

    /**
     * 获取员工授信统计概览数据
     * @return 统计数据
     */
    com.zbkj.common.response.employee.EmployeeCreditStatisticsResponse getEmployeeCreditStatistics();

    /**
     * 分页查询员工授信明细
     * @param request 查询参数
     * @return 分页结果
     */
    IPage<com.zbkj.common.response.employee.EmployeeCreditDetailResponse> getEmployeeCreditDetailPage(com.zbkj.common.request.employee.EmployeeCreditPageRequest request);

    /**
     * 获取员工授信趋势数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 趋势数据
     */
    com.zbkj.common.response.employee.EmployeeCreditTrendResponse getEmployeeCreditTrend(String startDate, String endDate);

    /**
     * 导出员工授信统计数据
     * @param name 员工姓名
     * @param phone 手机号
     * @param farmInstitutionId 养殖机构ID
     * @param creditStatus 授信状态
     * @param response HTTP响应
     */
    void exportEmployeeCreditData(String name, String phone, Integer farmInstitutionId, String creditStatus, javax.servlet.http.HttpServletResponse response) throws Exception;

    /**
     * 管理员给员工分配授信额度
     * @param request 授信分配请求参数
     * @return 是否成功
     */
    Boolean allocateEmployeeCredit(com.zbkj.common.vo.finance.EmployeeCreditAllocationRequestVO request);
} 