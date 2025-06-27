package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.employee.Employee;
import com.zbkj.common.response.employee.EmployeeResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 员工表 Mapper 接口
 * </p>
 *
 * @author zbkj
 * @since 2024-12-19
 */
public interface EmployeeDao extends BaseMapper<Employee> {

    /**
     * 获取员工列表（包含用户信息）
     * @param keywords 关键词
     * @param status 状态
     * @param department 部门
     * @param position 职位
     * @param gender 性别
     * @param farmType 养殖类型
     * @param farmName 养殖场名称
     * @param companyName 企业名称
     * @param breedingVariety 养殖品种
     * @return 员工列表
     */
    List<EmployeeResponse> getEmployeeList(@Param("keywords") String keywords,
                                          @Param("status") Integer status,
                                          @Param("department") String department,
                                          @Param("position") String position,
                                          @Param("gender") Integer gender,
                                          @Param("farmType") Integer farmType,
                                          @Param("farmName") String farmName,
                                          @Param("companyName") String companyName,
                                          @Param("breedingVariety") String breedingVariety);

    /**
     * 根据ID获取员工详细信息（包含用户信息）
     * @param id 员工ID
     * @return 员工详细信息
     */
    EmployeeResponse getEmployeeDetail(@Param("id") Integer id);

    // ==================== 员工授信统计相关方法 ====================

    /**
     * 获取员工授信统计概览数据
     * @return 统计数据
     */
    com.zbkj.common.response.employee.EmployeeCreditStatisticsResponse getEmployeeCreditStatistics();

    /**
     * 获取员工授信明细列表
     * @param name 员工姓名
     * @param phone 手机号
     * @param farmInstitutionId 养殖机构ID
     * @param creditStatus 授信状态
     * @return 授信明细列表
     */
    List<com.zbkj.common.response.employee.EmployeeCreditDetailResponse> getEmployeeCreditDetailList(
            @Param("name") String name,
            @Param("phone") String phone,
            @Param("farmInstitutionId") Integer farmInstitutionId,
            @Param("creditStatus") String creditStatus
    );

    /**
     * 获取员工授信趋势数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 趋势数据
     */
    com.zbkj.common.response.employee.EmployeeCreditTrendResponse getEmployeeCreditTrend(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );
} 