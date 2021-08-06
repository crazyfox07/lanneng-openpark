package com.lann.openpark.foreign.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lann.openpark.client.dao.entiy.ParkRegionInfo;
import com.lann.openpark.client.dao.repository.ParkRegionInfoRepository;
import com.lann.openpark.common.enums.ResultEnum;
import com.lann.openpark.common.vo.PageVO;
import com.lann.openpark.common.vo.ResultVO;
import com.lann.openpark.foreign.bean.OrderBean;
import com.lann.openpark.foreign.bean.RemainSpaceBean;
import com.lann.openpark.order.dao.mapper.ParkChargeInfoMapper;
import com.lann.openpark.util.DateUtil;
import com.lann.openpark.util.PropertiesUtil;
import com.lann.openpark.util.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ForeignService {

    @Autowired
    ParkRegionInfoRepository parkRegionInfoRepository;
    @Autowired
    ParkChargeInfoMapper parkChargeInfoMapper;

    /**
     * 获取剩余车位数
     *
     * @return
     * @Author songqiang
     * @Description
     * @Date 2021/1/28 9:05
     */
    public ResultVO getRemainingSpaces() {
        try {
            // 查询停车场信息
            String parkCode = PropertiesUtil.getPropertiesByName("park_code");
            int berthCount = 0;// 车位数
            int remainBerthCount = 0; // 剩余车位数
            // 查询region信息，获取车位数和剩余车位数
            List<ParkRegionInfo> list = parkRegionInfoRepository.findParkRegionInfoList();
            for (ParkRegionInfo region : list) {
                berthCount = berthCount + region.getBerthCount();// 总车位数
                remainBerthCount = remainBerthCount + region.getRegionRectifyCount();// 剩余车位数
            }
            RemainSpaceBean remainSpaceBean = new RemainSpaceBean();
            remainSpaceBean.setParkCode(parkCode);
            remainSpaceBean.setTotalSpace(berthCount);
            remainSpaceBean.setRemainSpace(remainBerthCount);
            return ResultVOUtil.success(remainSpaceBean);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === 获取剩余车位数接口异常 ==");
            return ResultVOUtil.error(ResultEnum.EXCEPTION);
        }
    }

    /**
     * 获取订单列表接口
     * staplateNortDate endDate车辆入场时间起止 格式yyyy-mm-dd hh24:mi:ss
     * 车牌号为空的情况下，起止时间不能为空
     * plateNo 车牌号码
     * orderState 订单状态 0正在进行中，1订单完成
     * pagenum 当前页码 pagesize, 每页数
     * orderState -1场内，1自动放行，2人工放行，4免费放行，6软件结束，8epark放行，9人工删除
     * pagenum pagesize 页数 每页数据量
     *
     * @Author songqiang
     * @Description
     * @Date 2021/7/27 10:52
     **/
    public PageVO queryOrderList(String startDate, String endDate, String plateNo, String orderState, Integer pagenum, Integer pagesize) {
        try {
            // 车牌号为空的情况下，起止时间不能为空
            if (StringUtils.isEmpty(plateNo)) {
                if (StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate)) {
                    return ResultVOUtil.pageError(ResultEnum.PARAM_ERROR);
                }
            }
            if (null == pagenum) pagenum = 1;// 当前页
            if (null == pagesize) pagesize = 20;// 默认每页数
            if (pagesize > 100) {
                return ResultVOUtil.pageError(ResultEnum.PAGE_SIZE_OVER100);
            }
            // 查询数据
            PageHelper.startPage(pagenum, pagesize);
            Page<OrderBean> pages = parkChargeInfoMapper.queryOrders4Foreign(startDate, endDate, plateNo, orderState);
            log.info(pages.getTotal() + "==" + pages.getPageNum() + "==" + pages.getPageSize());
            return ResultVOUtil.pageResult(pages);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === 获取订单信息接口异常 ==");
            return ResultVOUtil.pageError(ResultEnum.EXCEPTION);
        }
    }
}
