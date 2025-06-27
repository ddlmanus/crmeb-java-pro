package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.record.ProductDayRecord;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.ProductRankingRequest;
import com.zbkj.common.response.*;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户表 服务实现类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserVisitRecordService userVisitRecordService;
    @Autowired
    private ProductService productService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private RefundOrderService refundOrderService;
    @Autowired
    private UserMerchantCollectService userMerchantCollectService;
    @Autowired
    private ProductDayRecordService productDayRecordService;
    @Autowired
    private MerchantDayRecordService merchantDayRecordService;

    /**
     * 首页数据
     * @return HomeRateResponse
     */
    @Override
    public HomeRateResponse indexMerchantDate(SystemAdmin systemAdmin) {
        String today = DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE);
        String yesterday = DateUtil.yesterday().toString(DateConstants.DATE_FORMAT_DATE);
        HomeRateResponse response = new HomeRateResponse();
        response.setSales(orderService.getPayOrderAmountByDate(systemAdmin.getMerId(), today));
        response.setYesterdaySales(orderService.getPayOrderAmountByDate(systemAdmin.getMerId(), yesterday));
        response.setOrderNum(orderService.getOrderNumByDate(systemAdmin.getMerId(), today));
        response.setYesterdayOrderNum(orderService.getOrderNumByDate(systemAdmin.getMerId(), yesterday));
        response.setFollowNum(userMerchantCollectService.getCountByMerId(systemAdmin.getMerId()));
        response.setVisitorsNum(merchantDayRecordService.getVisitorsByDate(systemAdmin.getMerId(), today));
        response.setYesterdayVisitorsNum(merchantDayRecordService.getVisitorsByDate(systemAdmin.getMerId(), yesterday));
        return response;
    }

    /**
     * 经营数据：
     * @return HomeOperatingMerDataResponse
     */
    @Override
    public HomeOperatingMerDataResponse operatingMerchantData(SystemAdmin systemAdmin) {
        HomeOperatingMerDataResponse response = new HomeOperatingMerDataResponse();
        response.setNotShippingOrderNum(orderService.getNotShippingNum(systemAdmin.getMerId()));
        response.setAwaitVerificationOrderNum(orderService.getAwaitVerificationNum(systemAdmin.getMerId()));
        response.setRefundingOrderNum(refundOrderService.getAwaitAuditNum(systemAdmin.getMerId()));
        response.setOnSaleProductNum(productService.getOnSaleNum(systemAdmin.getMerId()));
        response.setAwaitAuditProductNum(productService.getAwaitAuditNum(systemAdmin.getMerId()));
        return response;
    }

    /**
     * 平台端首页数据
     * @return PlatformHomeRateResponse
     */
    @Override
    public PlatformHomeRateResponse indexPlatformDate() {
        String today = DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE);
        String yesterday = DateUtil.yesterday().toString(DateConstants.DATE_FORMAT_DATE);
        PlatformHomeRateResponse response = new PlatformHomeRateResponse();
        response.setTodayNewUserNum(userService.getRegisterNumByDate(today));
        response.setYesterdayNewUserNum(userService.getRegisterNumByDate(yesterday));
        response.setPageviews(userVisitRecordService.getPageviewsByDate(today));
        response.setYesterdayPageviews(userVisitRecordService.getPageviewsByDate(yesterday));
        response.setTodayNewMerchantNum(merchantService.getNewNumByDate(today));
        response.setYesterdayNewMerchantNum(merchantService.getNewNumByDate(yesterday));
        response.setOrderNum(orderService.getOrderNumByDate(0, today));
        response.setYesterdayOrderNum(orderService.getOrderNumByDate(0, yesterday));
        response.setSales(orderService.getPayOrderAmountByDate(0, today));
        response.setYesterdaySales(orderService.getPayOrderAmountByDate(0, yesterday));
        response.setUserNum(userService.getTotalNum());
        response.setMerchantNum(merchantService.getAllCount());
        return response;
    }

    /**
     * 平台端首页经营数据
     * @return HomeOperatingDataResponse
     */
    @Override
    public HomeOperatingDataResponse operatingPlatformData() {
        HomeOperatingDataResponse response = new HomeOperatingDataResponse();
        response.setNotShippingOrderNum(orderService.getNotShippingNum(0));
        response.setAwaitVerificationOrderNum(orderService.getAwaitVerificationNum(0));
        response.setRefundingOrderNum(refundOrderService.getAwaitAuditNum(0));
        response.setOnSaleProductNum(productService.getOnSaleNum(0));
        response.setAwaitAuditProductNum(productService.getAwaitAuditNum(0));
        return response;
    }

    /**
     * 平台端首页获取用户渠道数据
     */
    @Override
    public List<UserChannelDataResponse> getUserChannelData() {
        List<User> userList = userService.getChannelData();
        return userList.stream().map(e -> {
            UserChannelDataResponse response = new UserChannelDataResponse();
            response.setRegisterType(e.getRegisterType());
            response.setNum(e.getPayCount());
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 商户端商品支付排行榜
     */
    @Override
    public List<ProductRankingResponse> merchantProductPayRanking(SystemAdmin systemAdmin) {
        Integer merId = systemAdmin.getMerId();
        ProductRankingRequest request = new ProductRankingRequest();
        request.setMerId(merId);
        request.setDateLimit(DateConstants.SEARCH_DATE_LATELY_7);
        request.setSortKey("salesAmount");
        PageInfo<ProductDayRecord> pageInfo = productDayRecordService.getRanking(request);
        List<ProductDayRecord> recordList = pageInfo.getList();
        List<ProductRankingResponse> list = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(recordList)) {
            for (ProductDayRecord record : recordList) {
                Product product = productService.getById(record.getProductId());
                ProductRankingResponse response = new ProductRankingResponse();
                BeanUtils.copyProperties(record, response);
                response.setSalesAmount(record.getOrderSuccessProductFee());
                response.setProductId(product.getId());
                response.setProName(product.getName());
                response.setImage(product.getImage());
                list.add(response);
            }
        }
        return list;
    }

    /**
     * 商品浏览量排行榜
     */
    @Override
    public List<ProductRankingResponse> merchantProductPageviewRanking(SystemAdmin systemAdmin) {
        Integer merId = systemAdmin.getMerId();
        ProductRankingRequest request = new ProductRankingRequest();
        request.setMerId(merId);
        request.setDateLimit(DateConstants.SEARCH_DATE_LATELY_7);
        request.setSortKey("pageviews");
        PageInfo<ProductDayRecord> pageInfo = productDayRecordService.getRanking(request);
        List<ProductDayRecord> recordList = pageInfo.getList();
        List<ProductRankingResponse> list = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(recordList)) {
            for (ProductDayRecord record : recordList) {
                Product product = productService.getById(record.getProductId());
                ProductRankingResponse response = new ProductRankingResponse();
                BeanUtils.copyProperties(record, response);
                response.setPageView(record.getPageView());
                response.setProductId(product.getId());
                response.setProName(product.getName());
                response.setImage(product.getImage());
                list.add(response);
            }
        }
        return list;
    }

    /**
     * 平台热门商品排行榜
     */
    @Override
    public List<HotProductRankingResponse> getPlatformHotProductRanking(String timeRange, String rankType, Integer limit) {
        // 设置时间范围
        String dateLimit = DateConstants.SEARCH_DATE_LATELY_7;
        if ("30d".equals(timeRange)) {
            dateLimit = DateConstants.SEARCH_DATE_LATELY_30;
        } else if ("90d".equals(timeRange)) {
            dateLimit = DateConstants.SEARCH_DATE_LATELY_90;
        }
        
        // 设置排序类型
        String sortKey = "salesNum";
        if ("amount".equals(rankType)) {
            sortKey = "salesAmount";
        } else if ("views".equals(rankType)) {
            sortKey = "pageviews";
        }
        
        ProductRankingRequest request = new ProductRankingRequest();
        request.setMerId(0); // 0表示平台级别
        request.setDateLimit(dateLimit);
        request.setSortKey(sortKey);
        
        PageInfo<ProductDayRecord> pageInfo = productDayRecordService.getRanking(request);
        List<ProductDayRecord> recordList = pageInfo.getList();
        
        if (limit != null && limit > 0) {
            recordList = recordList.stream().limit(limit).collect(Collectors.toList());
        }
        
        List<HotProductRankingResponse> list = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(recordList)) {
            BigDecimal totalSales = recordList.stream()
                .map(ProductDayRecord::getOrderSuccessProductFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
            for (int i = 0; i < recordList.size(); i++) {
                ProductDayRecord record = recordList.get(i);
                Product product = productService.getById(record.getProductId());
                if (product == null) continue;
                
                HotProductRankingResponse response = new HotProductRankingResponse();
                response.setProductId(product.getId());
                response.setProductName(product.getName());
                response.setProductImage(product.getImage());
                response.setMerchantId(product.getMerId());
                
                // 获取商户信息
                try {
                    String merchantName = merchantService.getById(product.getMerId()).getName();
                    response.setMerchantName(merchantName);
                } catch (Exception e) {
                    response.setMerchantName("未知商户");
                }
                
                response.setSalesCount(record.getOrderProductNum());
                response.setSalesAmount(record.getOrderSuccessProductFee());
                response.setPageViews(record.getPageView());
                response.setPrice(product.getPrice());
                response.setStock(product.getStock());
                response.setRanking(i + 1);
                
                // 计算销量占比
                if (totalSales.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal percentage = record.getOrderSuccessProductFee()
                        .divide(totalSales, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                    response.setSalesPercentage(percentage);
                }
                
                // 计算热度指数
                BigDecimal hotIndex = calculateHotIndex(record);
                response.setHotIndex(hotIndex);
                
                list.add(response);
            }
        }
        return list;
    }

    /**
     * 获取实时数据流
     */
    @Override
    public RealtimeDataResponse getRealtimeData() {
        String today = DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE);
        String yesterday = DateUtil.yesterday().toString(DateConstants.DATE_FORMAT_DATE);
        
        RealtimeDataResponse response = new RealtimeDataResponse();
        
        // 在线用户数（从今日访问记录中统计，使用浏览量作为活跃度指标）
        Integer todayVisitors = userVisitRecordService.getPageviewsByDate(today);
        response.setOnlineUsers(todayVisitors);
        
        // 活跃商户数（今日有订单的商户数量）
        Integer activeMerchants = getActiveMerchantCountByDate(today);
        response.setActiveMerchants(activeMerchants);
        
        // 实时订单数（今日订单数）
        response.setRealtimeOrders(orderService.getOrderNumByDate(0, today));
        
        // 支付成功率（今日支付成功订单 / 今日总订单）
        Integer totalOrders = orderService.getOrderNumByDate(0, today);
        Integer paidOrders = getPaidOrderNumByDate(today);
        BigDecimal paymentSuccessRate = BigDecimal.ZERO;
        if (totalOrders > 0) {
            paymentSuccessRate = new BigDecimal(paidOrders)
                .divide(new BigDecimal(totalOrders), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        }
        response.setPaymentSuccessRate(paymentSuccessRate);
        
        // 今日访问量
        response.setTodayVisits(userVisitRecordService.getPageviewsByDate(today));
        
        // 今日新增用户
        response.setTodayNewUsers(userService.getRegisterNumByDate(today));
        
        // 今日销售额
        response.setTodaySales(orderService.getPayOrderAmountByDate(0, today));
        
        // 系统负载（根据今日访问量判断）
        Integer pageviews = userVisitRecordService.getPageviewsByDate(today);
        String systemLoad = "正常";
        if (pageviews > 10000) {
            systemLoad = "繁忙";
        } else if (pageviews > 5000) {
            systemLoad = "中等";
        }
        response.setSystemLoad(systemLoad);
        
        // 计算趋势（使用昨日数据对比）
        Integer yesterdayVisitors = userVisitRecordService.getPageviewsByDate(yesterday);
        Integer yesterdayActiveMerchants = getActiveMerchantCountByDate(yesterday);
        Integer yesterdayOrders = orderService.getOrderNumByDate(0, yesterday);
        
        Integer yesterdayTotalOrders = orderService.getOrderNumByDate(0, yesterday);
        Integer yesterdayPaidOrders = getPaidOrderNumByDate(yesterday);
        BigDecimal yesterdayPaymentRate = BigDecimal.ZERO;
        if (yesterdayTotalOrders > 0) {
            yesterdayPaymentRate = new BigDecimal(yesterdayPaidOrders)
                .divide(new BigDecimal(yesterdayTotalOrders), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        }
        
        response.setOnlineUsersTrend(calculateTrend(response.getOnlineUsers(), yesterdayVisitors));
        response.setActiveMerchantsTrend(calculateTrend(response.getActiveMerchants(), yesterdayActiveMerchants));
        response.setRealtimeOrdersTrend(calculateTrend(response.getRealtimeOrders(), yesterdayOrders));
        response.setPaymentSuccessRateTrend(calculateTrend(response.getPaymentSuccessRate(), yesterdayPaymentRate));
        
        return response;
    }

    /**
     * 获取财务概览数据
     */
    @Override
    public FinanceOverviewResponse getFinanceOverview() {
        String today = DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE);
        String yesterday = DateUtil.yesterday().toString(DateConstants.DATE_FORMAT_DATE);
        
        // 获取本月和上月的日期范围
        String monthStart = DateUtil.beginOfMonth(DateUtil.date()).toString(DateConstants.DATE_FORMAT_DATE);
        String lastMonthStart = DateUtil.beginOfMonth(DateUtil.offsetMonth(DateUtil.date(), -1)).toString(DateConstants.DATE_FORMAT_DATE);
        String lastMonthEnd = DateUtil.endOfMonth(DateUtil.offsetMonth(DateUtil.date(), -1)).toString(DateConstants.DATE_FORMAT_DATE);
        
        // 获取今年和去年的日期范围
        String yearStart = DateUtil.beginOfYear(DateUtil.date()).toString(DateConstants.DATE_FORMAT_DATE);
        String lastYearStart = DateUtil.beginOfYear(DateUtil.offsetMonth(DateUtil.date(), -12)).toString(DateConstants.DATE_FORMAT_DATE);
        String lastYearEnd = DateUtil.endOfYear(DateUtil.offsetMonth(DateUtil.date(), -12)).toString(DateConstants.DATE_FORMAT_DATE);
        
        FinanceOverviewResponse response = new FinanceOverviewResponse();
        
        // 收入数据
        response.setTodayIncome(orderService.getPayOrderAmountByDate(0, today));
        response.setYesterdayIncome(orderService.getPayOrderAmountByDate(0, yesterday));
        
        // 月度收入数据
        response.setMonthIncome(getOrderAmountByDateRange(monthStart, today));
        response.setLastMonthIncome(getOrderAmountByDateRange(lastMonthStart, lastMonthEnd));
        
        // 年度收入数据  
        response.setYearIncome(getOrderAmountByDateRange(yearStart, today));
        response.setLastYearIncome(getOrderAmountByDateRange(lastYearStart, lastYearEnd));
        
        // 结算相关数据（基于真实订单状态计算）
        BigDecimal pendingAmount = getPendingSettlementAmount();
        BigDecimal settledAmount = getSettledAmount();
        response.setPendingSettlement(pendingAmount);
        response.setSettledAmount(settledAmount);
        response.setFrozenAmount(BigDecimal.ZERO); // 冻结金额暂无相关业务
        
        // 今日费用支出（基于真实退款数据）
        BigDecimal todayRefund = refundOrderService.getRefundOrderAmountByDate(today);
        response.setTodayRefund(todayRefund);
        
        // 其他费用基于配置或固定比例计算
        response.setTodayCommission(response.getTodayIncome().multiply(new BigDecimal("0.05"))); // 5%佣金
        response.setTodayPlatformFee(response.getTodayIncome().multiply(new BigDecimal("0.02"))); // 2%平台费
        response.setTodayTransactionFee(response.getTodayIncome().multiply(new BigDecimal("0.006"))); // 0.6%交易手续费
        
        // 净收入
        BigDecimal todayExpense = response.getTodayRefund()
            .add(response.getTodayCommission())
            .add(response.getTodayPlatformFee())
            .add(response.getTodayTransactionFee());
        response.setTodayNetIncome(response.getTodayIncome().subtract(todayExpense));
        
        // 增长率计算
        response.setIncomeGrowthRate(calculateGrowthRate(response.getTodayIncome(), response.getYesterdayIncome()));
        response.setMonthGrowthRate(calculateGrowthRate(response.getMonthIncome(), response.getLastMonthIncome()));
        
        return response;
    }

    /**
     * 获取销售趋势数据
     */
    @Override
    public SalesTrendResponse getSalesTrend(String timeRange) {
        int days = 7;
        if ("30d".equals(timeRange)) {
            days = 30;
        } else if ("90d".equals(timeRange)) {
            days = 90;
        }
        
        SalesTrendResponse response = new SalesTrendResponse();
        response.setTimeRange(timeRange);
        
        List<String> dates = new ArrayList<>();
        List<Integer> orderData = new ArrayList<>();
        List<BigDecimal> salesData = new ArrayList<>();
        List<Integer> newUserData = new ArrayList<>();
        List<Integer> visitData = new ArrayList<>();
        List<BigDecimal> conversionRateData = new ArrayList<>();
        List<BigDecimal> avgOrderValueData = new ArrayList<>();
        List<Integer> productSalesData = new ArrayList<>();
        
        // 生成日期范围和数据
        LocalDate endDate = LocalDate.now();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = endDate.minusDays(i);
            String dateStr = date.toString();
            dates.add(dateStr);
            
            // 获取实际数据
            Integer orders = orderService.getOrderNumByDate(0, dateStr);
            BigDecimal sales = orderService.getPayOrderAmountByDate(0, dateStr);
            Integer newUsers = userService.getRegisterNumByDate(dateStr);
            Integer visits = userVisitRecordService.getPageviewsByDate(dateStr);
            
            orderData.add(orders);
            salesData.add(sales);
            newUserData.add(newUsers);
            visitData.add(visits);
            
            // 计算转化率
            BigDecimal conversionRate = BigDecimal.ZERO;
            if (visits > 0) {
                conversionRate = new BigDecimal(orders).divide(new BigDecimal(visits), 4, RoundingMode.HALF_UP);
            }
            conversionRateData.add(conversionRate);
            
            // 计算客单价
            BigDecimal avgOrderValue = BigDecimal.ZERO;
            if (orders > 0) {
                avgOrderValue = sales.divide(new BigDecimal(orders), 2, RoundingMode.HALF_UP);
            }
            avgOrderValueData.add(avgOrderValue);
            
            // 商品销量（模拟数据）
            productSalesData.add(orders * 2); // 假设平均每单2件商品
        }
        
        response.setDates(dates);
        response.setOrderData(orderData);
        response.setSalesData(salesData);
        response.setNewUserData(newUserData);
        response.setVisitData(visitData);
        response.setConversionRateData(conversionRateData);
        response.setAvgOrderValueData(avgOrderValueData);
        response.setProductSalesData(productSalesData);
        
        // 计算汇总信息
        SalesTrendResponse.SalesTrendSummary summary = new SalesTrendResponse.SalesTrendSummary();
        summary.setTotalOrders(orderData.stream().mapToInt(Integer::intValue).sum());
        summary.setTotalSales(salesData.stream().reduce(BigDecimal.ZERO, BigDecimal::add));
        summary.setTotalNewUsers(newUserData.stream().mapToInt(Integer::intValue).sum());
        summary.setTotalVisits(visitData.stream().mapToInt(Integer::intValue).sum());
        
        // 平均转化率和客单价
        if (!conversionRateData.isEmpty()) {
            summary.setAvgConversionRate(conversionRateData.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(conversionRateData.size()), 4, RoundingMode.HALF_UP));
        }
        
        if (!avgOrderValueData.isEmpty()) {
            summary.setAvgOrderValue(avgOrderValueData.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(avgOrderValueData.size()), 2, RoundingMode.HALF_UP));
        }
        
        // 计算增长率（与上一周期对比）
        summary.setSalesGrowthRate(BigDecimal.ZERO); // 实现时需要获取上一周期数据对比
        summary.setOrderGrowthRate(BigDecimal.ZERO);
        
        response.setSummary(summary);
        
        return response;
    }

    /**
     * 计算热度指数
     */
    private BigDecimal calculateHotIndex(ProductDayRecord record) {
        // 热度指数 = 销量权重 * 0.4 + 销售额权重 * 0.3 + 浏览量权重 * 0.2 + 收藏量权重 * 0.1
        BigDecimal salesScore = new BigDecimal(record.getOrderProductNum()).multiply(new BigDecimal("0.4"));
        BigDecimal amountScore = record.getOrderSuccessProductFee().multiply(new BigDecimal("0.3"));
        BigDecimal viewScore = new BigDecimal(record.getPageView()).multiply(new BigDecimal("0.2"));
        BigDecimal collectScore = BigDecimal.ZERO; // 暂时没有收藏数据
        
        return salesScore.add(amountScore).add(viewScore).add(collectScore);
    }

    /**
     * 生成随机在线用户数（模拟实时数据）
     */
    private Integer generateRandomOnlineUsers() {
        return 1200 + (int)(Math.random() * 500); // 1200-1700之间的随机数
    }

    /**
     * 计算趋势百分比
     */
    private BigDecimal calculateTrend(Number current, Number previous) {
        if (current == null || previous == null) return BigDecimal.ZERO;
        BigDecimal currentVal = new BigDecimal(current.toString());
        BigDecimal previousVal = new BigDecimal(previous.toString());
        
        if (previousVal.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        
        return currentVal.subtract(previousVal)
            .divide(previousVal, 4, RoundingMode.HALF_UP)
            .multiply(new BigDecimal("100"));
    }

    /**
     * 计算增长率
     */
    private BigDecimal calculateGrowthRate(BigDecimal current, BigDecimal previous) {
        if (current == null || previous == null) return BigDecimal.ZERO;
        if (previous.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        
        return current.subtract(previous)
            .divide(previous, 4, RoundingMode.HALF_UP)
            .multiply(new BigDecimal("100"));
    }

    /**
     * 获取指定日期的活跃商户数量
     */
    private Integer getActiveMerchantCountByDate(String date) {
        // 查询当日有订单的商户数量
        return orderService.getActiveMerchantCountByDate(date);
    }

    /**
     * 获取指定日期的支付成功订单数量
     */
    private Integer getPaidOrderNumByDate(String date) {
        return orderService.getPaidOrderNumByDate(date);
    }

    /**
     * 获取已结算金额
     */
    private BigDecimal getSettledAmount() {
        // 查询已结算的订单金额
        // 这里可以根据实际业务逻辑调整查询条件
        return orderService.getSettledAmount();
    }

    /**
     * 获取日期范围内的订单金额总和
     */
    private BigDecimal getOrderAmountByDateRange(String startDate, String endDate) {
        // 这里可以调用订单服务的范围查询方法
        // 暂时用循环累加实现
        BigDecimal totalAmount = BigDecimal.ZERO;
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        while (!start.isAfter(end)) {
            BigDecimal dayAmount = orderService.getPayOrderAmountByDate(0, start.toString());
            totalAmount = totalAmount.add(dayAmount);
            start = start.plusDays(1);
        }
        return totalAmount;
    }

    /**
     * 获取待结算金额（基于订单状态）
     */
    private BigDecimal getPendingSettlementAmount() {
        // 查询已完成但未结算的订单金额
        // 这里可以根据实际业务逻辑调整查询条件
        return orderService.getPendingSettlementAmount();
    }

}
