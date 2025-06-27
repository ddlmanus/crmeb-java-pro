package com.zbkj.front.controller.finance;

import com.zbkj.common.enums.UserTypeEnum;
import com.zbkj.common.model.finance.FarmBreedType;
import com.zbkj.common.model.finance.FarmInstitution;
import com.zbkj.common.model.user.User;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.finance.FarmBreedTypeService;
import com.zbkj.service.service.finance.FarmInstitutionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 养殖品种类型前端接口
 */
@Slf4j
@RestController
@Api(tags = "牧码通养殖品种类型接口")
@RequestMapping("/api/front/finance/farm-breed-type")
public class FarmBreedTypeController {
    
    @Autowired
    private FarmBreedTypeService farmBreedTypeService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private FarmInstitutionService farmInstitutionService;

    /**
     * 根据当前用户的机构获取品种类型
     */
    @GetMapping("/by-current-user-organization")
    @ApiOperation(value = "根据当前用户的机构获取品种类型")
    public CommonResult<List<FarmBreedType>> getBreedTypesByCurrentUserOrganization() {
        try {
            log.info("根据当前用户的机构获取品种类型");
            
            // 1. 获取当前用户
            User currentUser = userService.getInfo();
            if (currentUser == null) {
                return CommonResult.failed("用户不存在");
            }
            log.info("当前用户ID: {}, 用户名: {}", currentUser.getId(), currentUser.getNickname());
            
            List<FarmBreedType> allBreedTypes = new ArrayList<>();
            
            // 2. 优先检查用户是否关联机构
            if (currentUser.getOrganizationId() != null && !currentUser.getOrganizationId().isEmpty()&&  !currentUser.getUserType().equals(UserTypeEnum.USER_TYPE_EMPLOYEE.getCode())) {
                log.info("用户关联机构ID: {}", currentUser.getOrganizationId());
                
                // 3. 根据机构ID获取所有养殖场
                List<FarmInstitution> farmInstitutions = farmInstitutionService.getFarmInstitutionByOrganizationId(currentUser.getOrganizationId());
                if (farmInstitutions != null && !farmInstitutions.isEmpty()) {
                    log.info("找到{}个养殖场", farmInstitutions.size());
                    
                    // 4. 根据养殖场编码获取养殖品种类型
                    for (FarmInstitution farmInstitution : farmInstitutions) {
                        String farmCode = farmInstitution.getFarmCode();
                        if (farmCode != null && !farmCode.isEmpty()) {
                            List<FarmBreedType> breedTypes = farmBreedTypeService.getByFarmCode(farmCode);
                            if (breedTypes != null && !breedTypes.isEmpty()) {
                                log.info("养殖场{}找到{}个品种类型", farmCode, breedTypes.size());
                                allBreedTypes.addAll(breedTypes);
                            }
                        }
                    }
                } else {
                    log.warn("机构{}下没有找到养殖场", currentUser.getOrganizationId());
                }
            }
            
            // 5. 如果通过机构没有获取到数据，且用户有养殖场编码，则使用用户的养殖场编码
            if (allBreedTypes.isEmpty() && currentUser.getFarmCode() != null && !currentUser.getFarmCode().isEmpty()) {
                log.info("机构下没有数据或用户无机构，使用用户养殖场编码: {}", currentUser.getFarmCode());
                
                // 直接根据用户的养殖场编码获取品种类型
                List<FarmBreedType> breedTypes = farmBreedTypeService.getByFarmCode(currentUser.getFarmCode());
                if (breedTypes != null && !breedTypes.isEmpty()) {
                    log.info("用户养殖场{}找到{}个品种类型", currentUser.getFarmCode(), breedTypes.size());
                    allBreedTypes.addAll(breedTypes);
                } else {
                    log.warn("用户养殖场{}下没有找到品种类型", currentUser.getFarmCode());
                }
            }
            
            // 6. 如果还是没有数据
            if (allBreedTypes.isEmpty()) {
                log.warn("用户{}既没有通过机构获取到数据，也没有通过个人养殖场获取到数据", currentUser.getId());
                return CommonResult.failed("未找到相关品种类型数据");
            }
            
            log.info("根据当前用户获取品种类型完成，总共{}条数据", allBreedTypes.size());
            return CommonResult.success(allBreedTypes);
            
        } catch (Exception e) {
            log.error("根据当前用户获取品种类型失败", e);
            return CommonResult.failed("获取失败：" + e.getMessage());
        }
    }
} 