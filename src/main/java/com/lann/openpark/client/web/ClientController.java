package com.lann.openpark.client.web;

import com.github.pagehelper.Page;
import com.lann.openpark.camera.dao.entiy.EquipInfo;
import com.lann.openpark.charge.dao.bean.ChargePlanParams;
import com.lann.openpark.client.bean.*;
import com.lann.openpark.client.dao.entiy.ClientRole;
import com.lann.openpark.client.dao.entiy.ParkUserInfo;
import com.lann.openpark.client.dao.entiy.ParkVipInfo;
import com.lann.openpark.client.service.ClientManagerService;
import com.lann.openpark.client.service.ClientService;
import com.lann.openpark.common.Constant;
import com.lann.openpark.common.enums.DetectEnum;
import com.lann.openpark.common.enums.OrderStatusEnum;
import com.lann.openpark.common.enums.PayTypeEnum;
import com.lann.openpark.common.enums.PlateTypeEnum;
import com.lann.openpark.common.vo.ResultVO;
import com.lann.openpark.park.bean.ParkInfoBean;
import com.lann.openpark.park.service.ParkService;
import com.lann.openpark.util.DateUtil;
import com.lann.openpark.util.EhcacheUtil;
import com.lann.openpark.util.ExportUtil;
import io.swagger.annotations.Api;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


@RestController
@Api(value = "客户端接口")
@RequestMapping(value = "/api/client")
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    ClientService clientService;

    @Autowired
    ParkService parkService;

    @Autowired
    ClientManagerService clientManagerService;

    /**
     * 桌面客户端登录接口
     *
     * @Author songqiang
     * @Description
     * @Date 2020/10/29 9:24
     **/
    @RequestMapping(value = "/login")
    public String clinetLogin(HttpServletRequest request, HttpServletResponse response,
                              @RequestParam(value = "userName") String userName, @RequestParam(value = "passWord") String passWord) {
        return clientService.clinetLogin(userName, passWord);
    }

    /**
     * 获取停车场基本信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/10/29 14:06
     **/
    @RequestMapping(value = "/parkInfo4Clent")
    public String parkInfo4Clent(HttpServletRequest request, HttpServletResponse response) {
        return clientService.parkInfo4Clent();
    }

    /**
     * 出入口抬杆
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/2 16:41
     **/
    @RequestMapping(value = "/controlgate")
    public String controlgate(HttpServletRequest request, HttpServletResponse response, String deviceNo, String userid) {
        return clientService.controlgate(deviceNo, userid);
    }

    /**
     * 现金放行或者免费放行
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/2 16:42
     **/
    @RequestMapping(value = "/opengatebyfee")
    public String opengatebyfee(HttpServletRequest request, HttpServletResponse response, String orderNo, String fee, boolean isFree, String userid) {
        return clientService.opengatebyfee(orderNo, fee, isFree, userid);
    }

    /**
     * 订单信息查询
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/3 11:02
     **/
    @RequestMapping(value = "/queryorders")
    public String queryOrders(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate, String startDate1, String endDate1, String plateNo,
                              String orderState, String regionCode, int pagenum, int pagesize) {
        return clientService.queryOrders(startDate, endDate, startDate1, endDate1, plateNo, orderState, regionCode, pagenum, pagesize);
    }

    /**
     * 查询出入记录
     *
     * @Author songqiang
     * @Description
     * @Date \ 10:25
     **/
    @RequestMapping(value = "/queryDetectList")
    public String queryDetectList(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate, String plateNo,
                                  String direction, int pagenum, int pagesize) {
        return clientService.queryDetectList(startDate, endDate, plateNo, direction, pagenum, pagesize);
    }

    /**
     * 场内车查询
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/17 9:24
     **/
    @RequestMapping(value = "/queryInnerCarList")
    public String queryInnerCarList(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate, String plateNo,
                                    int pagenum, int pagesize) {
        return clientService.queryInnerCarList(startDate, endDate, plateNo, pagenum, pagesize);
    }

    /**
     * 查询支付记录
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/15 14:21
     **/
    @RequestMapping(value = "/queryPayList")
    public String queryPayList(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate,
                               String plateNo, String userid, String payType, int pagenum, int pagesize) {
        return clientService.queryPayList(startDate, endDate, plateNo, payType, userid, pagenum, pagesize);
    }


    @RequestMapping(value = "/queryOrderPay")
    public String queryOrderPay(HttpServletRequest request, HttpServletResponse response, String nid) {
        return clientService.queryOrderPay(nid);
    }

    /**
     * 查询停车场信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/3 11:02
     **/
    @RequestMapping(value = "/getparkinfo")
    public String getParkInfo() {
        JSONObject jb = new JSONObject();
        try {
            ParkInfoBean park = parkService.findParkInfo();
            if (null == park) {
                jb.put("code", "1");
                jb.put("message", "未查询到停车场信息");
                jb.put("park", new JSONObject());
                return jb.toString();
            }
            jb.put("code", "0");
            jb.put("message", "查询成功");
            jb.put("park", JSONObject.fromObject(park));
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === 停车场信息查询异常 ==");
            jb.put("code", "99");
            jb.put("message", "Exception：" + e.getMessage());
            jb.put("park", new JSONObject());
            return jb.toString();
        }
    }

    /**
     * 车位数调整
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/4 8:41
     **/
    @RequestMapping(value = "/updateParkBerth")
    public String updateParkBerth(String berthNum) {
        return clientService.updateParkBerth(berthNum);
    }

    /**
     * 区域车位数调整
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/11 11:15
     **/
    @RequestMapping(value = "/updateRegionBerth")
    public String updateRegionBerth(String regionCode, String berthNum) {
        return clientService.updateRegionBerth(regionCode, berthNum);
    }

    /**
     * 重置停车场信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/4 8:41
     **/
    @RequestMapping(value = "/updateParkCode")
    public String updateParkCode(String parkCode, String parkname, String berthcount, String address, String principal, String phone) {
        return clientService.updateParkCode(parkCode, parkname, berthcount, address, principal, phone);
    }

    /**
     * 更新停车场信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/4 14:38
     **/
    @RequestMapping(value = "/updateparkinfo")
    public String updateParkInfo(String parkname, String address, String principal, String phone) {
        return clientService.updateParkInfo(parkname, address, principal, phone);
    }

    /**
     * 获取停车场配置信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/4 14:53
     **/
    @RequestMapping(value = "/getSysConfig")
    public String getSysConfig() {
        return clientService.getSysConfig();
    }

    /**
     * 获取停车场配置信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/4 14:53
     **/
    @RequestMapping(value = "/updateParkConfig")
    public String updateParkConfig(SysConfigValueBean sysconfigValue) {
        return clientService.updateParkConfig(sysconfigValue);
    }


    /**
     * 查询所有区域信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/11 10:03
     **/
    @RequestMapping(value = "/queryrRegions")
    public String queryrRegions(HttpServletRequest request, HttpServletResponse response) {
        return clientService.queryrRegions();
    }

    /**
     * 查询出入口信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/4 16:51
     **/
    @RequestMapping(value = "/querygates")
    public String querygates(HttpServletRequest request, HttpServletResponse response) {
        return clientService.querygates();
    }

    /**
     * 删除区域信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/11 10:36
     **/
    @RequestMapping(value = "/deleteRegion")
    public String deleteRegion(HttpServletRequest request, HttpServletResponse response, String regionCode) {
        return clientService.deleteRegion(regionCode);
    }


    /**
     * 删除出入口信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/5 8:36
     **/
    @RequestMapping(value = "/deleteGate")
    public String deleteGate(HttpServletRequest request, HttpServletResponse response, String pointcode) {
        return clientService.deleteGate(pointcode);
    }

    /**
     * 添加出入口信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/5 9:53
     **/
    @RequestMapping(value = "/addGateInfo")
    public String addGateInfo(HttpServletRequest request, HttpServletResponse response, String pointcode, String pointname, String regioncode, String pointFunc) {
        return clientService.addGateInfo(pointcode, pointname, regioncode, pointFunc);
    }

    /**
     * 添加区域信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/11 10:12
     **/
    @RequestMapping(value = "/addRegionInfo")
    public String addRegionInfo(HttpServletRequest request, HttpServletResponse response, String regionCode, String regionName, String berthCount,
                                String regionType, String parentRegion, String restrictedAccess, String whitelistPrivileges) {
        return clientService.addRegionInfo(regionCode, regionName, berthCount, regionType, parentRegion, restrictedAccess, whitelistPrivileges);
    }

    /**
     * 查询所有设备信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/5 9:04
     **/
    @RequestMapping(value = "/queryEquipInfo")
    public String queryEquipInfo(HttpServletRequest request, HttpServletResponse response) {
        return clientService.queryEquipInfo();
    }

    /**
     * 删除相机信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/5 9:13
     **/
    @RequestMapping(value = "/deleteEquipInfo")
    public String deleteEquipInfo(HttpServletRequest request, HttpServletResponse response, String devicecode) {
        return clientService.deleteEquipInfo(devicecode);
    }


    /**
     * 添加相机信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/5 10:50
     **/
    @RequestMapping(value = "/addEquipInfo")
    public ResultVO addEquipInfo(HttpServletRequest request, HttpServletResponse response, EquipInfo equipInfo) {
        return clientService.addEquipInfo(equipInfo);
    }

    /**
     * 查询会员列表
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/5 15:29
     **/
    @RequestMapping(value = "/queryLeaguerList")
    public String queryLeaguerList(HttpServletRequest request, HttpServletResponse response, String plateNo, String phoneNo, int pagenum, int pagesize) {
        return clientService.queryLeaguerList(plateNo, phoneNo, pagenum, pagesize);
    }

    /**
     * @Author songqiang
     * @Description
     * @Date 2021/5/26 15:56
     **/
    @RequestMapping(value = "/queryParkLeaguerList")
    public String queryParkLeaguerList(HttpServletRequest request, HttpServletResponse response, String plateNo, String phoneNo, int pagenum, int pagesize) {
        return clientService.queryParkLeaguerList(plateNo, phoneNo, pagenum, pagesize);
    }

    /**
     * 查询客户下车辆列表
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/4 11:03
     **/
    @RequestMapping(value = "/queryParkLeaguerCars")
    public String queryParkLeaguerCars(HttpServletRequest request, HttpServletResponse response, String vipId) {
        return clientService.queryParkLeaguerCars(vipId);
    }

    /**
     * 查询黑名单列表
     *
     * @Author songqiang
     * @Description
     * @Date 2021/3/31 9:39
     **/
    @RequestMapping(value = "/queryBlackList")
    public String queryBlackList(HttpServletRequest request, HttpServletResponse response, String plateNo, String phoneNo, int pagenum, int pagesize) {
        return clientService.queryBlackList(plateNo, phoneNo, pagenum, pagesize);
    }


    /**
     * 查询异常订单车辆列表
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/1 11:04
     **/
    @RequestMapping(value = "/queryAbnormalkList")
    public String queryAbnormalkList(HttpServletRequest request, HttpServletResponse response, String plateNo, String num, int pagenum, int pagesize) {
        return clientService.queryAbnormalkList(plateNo, num, pagenum, pagesize);
    }


    /**
     * 异常车辆详细列表
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/1 14:58
     **/
    @RequestMapping(value = "/queryDetailList")
    public String queryDetailList(HttpServletRequest request, HttpServletResponse response, String aid) {
        return clientService.queryDetailList(aid);
    }

    /**
     * 添加会员信息（按车牌号唯一）
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/5 15:29
     **/
    @RequestMapping(value = "/addLeaguerInfo")
    public String addLeaguerInfo(HttpServletRequest request, HttpServletResponse response, ParkVipInfoParams parkVipInfoParams) {
        return clientService.addLeaguerInfo(parkVipInfoParams);
    }

    /**
     * 只增的白名单（任意添加，便于统计明细）
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/4 10:06
     **/
    @RequestMapping(value = "/addLeaguerInfo1")
    public String addLeaguerInfo1(HttpServletRequest request, HttpServletResponse response, ParkVipInfoParams parkVipInfoParams) {
        return clientService.addLeaguerInfo1(parkVipInfoParams);
    }

    /**
     * 客户方式的白名单，先维护客户，再维护客户下的车牌号码
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/4 10:07
     **/
    @RequestMapping(value = "/addLeaguerInfo4Client")
    public String addLeaguerInfo4Client(HttpServletRequest request, HttpServletResponse response, ParkVipInfoParams parkVipInfoParams) {
        return clientService.addLeaguerInfo4Client(parkVipInfoParams);
    }

    /**
     * 添加黑名单
     *
     * @Author songqiang
     * @Description
     * @Date 2021/3/31 10:45
     **/
    @RequestMapping(value = "/addBlackInfo")
    public String addBlackInfo(HttpServletRequest request, HttpServletResponse response, ParkVipInfoParams parkVipInfoParams) {
        return clientService.addBlackInfo(parkVipInfoParams);
    }


    /**
     * 会员信息删除
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/5 16:27
     **/
    @RequestMapping(value = "/deleteLeaguer")
    public String deleteLeaguer(HttpServletRequest request, HttpServletResponse response, String vipId) {
        return clientService.deleteLeaguer(vipId);
    }

    /**
     * 删除黑名单信息
     *
     * @Author songqiang
     * @Description
     * @Date 2021/3/31 11:17
     **/
    @RequestMapping(value = "/deleteBlack")
    public String deleteBlack(HttpServletRequest request, HttpServletResponse response, String blackId) {
        return clientService.deleteBlack(blackId);
    }

    /**
     * 删除异常订单车辆
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/1 11:37
     **/
    @RequestMapping(value = "/deleteAbnormal")
    public String deleteAbnormal(HttpServletRequest request, HttpServletResponse response, String id) {
        return clientService.deleteAbnormal(id);
    }

    /**
     * 清空异常订单车辆
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/1 14:31
     **/
    @RequestMapping(value = "/clearAbnormal")
    public String clearAbnormal(HttpServletRequest request, HttpServletResponse response, int num) {
        return clientService.clearAbnormal(num);
    }

    /**
     * 会员续期
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/5 16:56
     **/
    @RequestMapping(value = "/xqLeaguer")
    public String xqLeaguer(HttpServletRequest request, HttpServletResponse response, String vipId, String validBegintime, String expiryDate) {
        return clientService.xqLeaguer(vipId, validBegintime, expiryDate);
    }

    /**
     * 黑名单续期
     *
     * @Author songqiang
     * @Description
     * @Date 2021/3/31 11:08
     **/
    @RequestMapping(value = "/xqBlack")
    public String xqBlack(HttpServletRequest request, HttpServletResponse response, String blackId, String validBegintime, String expiryDate) {
        return clientService.xqBlack(blackId, validBegintime, expiryDate);
    }

    /**
     * 查询用户列表
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/6 9:21
     **/
    @RequestMapping(value = "/queryUserList")
    public String queryUserList(HttpServletRequest request, HttpServletResponse response) {
        return clientService.queryUserList();
    }

    /**
     * 查询角色列表
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/2 15:51
     **/
    @RequestMapping(value = "/queryRoleList")
    public String queryRoleList(HttpServletRequest request, HttpServletResponse response) {
        return clientService.queryRoleList();
    }


    /**
     * 添加用户信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/6 9:58
     **/
    @RequestMapping(value = "/addUserInfo")
    public String addUserInfo(HttpServletRequest request, HttpServletResponse response, ParkUserInfo userinfo) {
        return clientService.addUserInfo(userinfo);
    }

    /**
     * 添加角色
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/2 15:58
     **/
    @RequestMapping(value = "/addRoleInfo")
    public String addRoleInfo(HttpServletRequest request, HttpServletResponse response, ClientRole info) {
        return clientService.addRoleInfo(info);
    }

    /**
     * 删除用户信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/6 10:07
     **/
    @RequestMapping(value = "/deleteUserInfo")
    public String deleteUserInfo(HttpServletRequest request, HttpServletResponse response, String userid) {
        return clientService.deleteUserInfo(userid);
    }

    /**
     * 删除角色信息
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/2 16:02
     **/
    @RequestMapping(value = "/deleteRoleInfo")
    public String deleteRoleInfo(HttpServletRequest request, HttpServletResponse response, String id) {
        return clientService.deleteRoleInfo(id);
    }

    /**
     * 保存角色菜单权限
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/2 16:24
     **/
    @RequestMapping(value = "/saveRoleMenu")
    public String saveRoleMenu(HttpServletRequest request, HttpServletResponse response, String id, String roleMenu) {
        return clientService.saveRoleMenu(id, roleMenu);
    }

    /**
     * 保存用户授权信息
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/6 9:36
     **/
    @RequestMapping(value = "/saveUserRoleData")
    public String saveUserRoleData(HttpServletRequest request, HttpServletResponse response, String userid, String transValue) {
        return clientService.saveUserRoleData(userid, transValue);
    }


    /**
     * 用户密码修改
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/6 10:48
     **/
    @RequestMapping(value = "/changeUserPassword")
    public String changeUserPassword(HttpServletRequest request, HttpServletResponse response, String userid, String password) {
        return clientService.changeUserPassword(userid, password);
    }

    /**
     * 计费规则列表查询
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/6 11:26
     **/
    @RequestMapping(value = "/queryRuleList")
    public String queryRuleList(HttpServletRequest request, HttpServletResponse response) {
        return clientService.queryRuleList();
    }

    /**
     * 计费方案列表查询
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/6 11:26
     **/
    @RequestMapping(value = "/querySchmeneList")
    public String querySchmeneList(HttpServletRequest request, HttpServletResponse response) {
        return clientService.querySchmeneList();
    }

    /**
     * 计费规则保存
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/11 14:02
     **/
    @RequestMapping(value = "/saveChargePolicy")
    public String saveChargePolicy(HttpServletRequest request, HttpServletResponse response) {
        StringBuffer bodyString = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                bodyString.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("计费规则参数接收异常：" + DateUtil.formatDateYMDHMS(new Date()));
            JSONObject jb = new JSONObject();
            jb.put("code", "99");
            jb.put("message", "Exception：" + e.getMessage());
            return jb.toString();
        }
        return clientService.saveChargePolicy(bodyString.toString());
    }

    /**
     * @Author songqiang
     * @Description
     * @Date 2020/11/12 9:27
     **/
    @RequestMapping(value = "/deleteChargePolicy")
    public String deleteChargePolicy(HttpServletRequest request, HttpServletResponse response, String nid) {
        return clientService.deleteChargePolicy(nid);
    }

    /**
     * 计费方案保存
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/12 11:41
     **/
    @RequestMapping(value = "/saveChargePlan")
    public String saveChargePlan(HttpServletRequest request, HttpServletResponse response, ChargePlanParams chargePlanParams) {
        return clientService.saveChargePlan(chargePlanParams);
    }

    /**
     * 计费方案删除
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/12 15:41
     **/
    @RequestMapping(value = "/deleteChargePlan")
    public String deleteChargePlan(HttpServletRequest request, HttpServletResponse response, String nid) {
        return clientService.deleteChargePlan(nid);
    }

    @RequestMapping(value = "/calcParkfee")
    public String calcParkfee(HttpServletRequest request, HttpServletResponse response, String timeIn, String timeOut, String policyId) {
        return clientService.calcParkfee(timeIn, timeOut, policyId);
    }

    /**
     * 手动获取当前离场订单
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/17 8:31
     **/
    @RequestMapping(value = "/getCurrentOrder")
    public String getCurrentOrder(HttpServletRequest request, HttpServletResponse response, String deviceNo) {
        return clientService.getCurrentOrder(deviceNo);
    }


    /**
     * 导出所有白名单列表
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/16 9:35
     **/
    @RequestMapping(value = "/exportVipList")
    public void exportVipList(HttpServletRequest request, HttpServletResponse response, String deviceNo) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        OutputStream os = null;
        try {
            // 获取会员数据
            List<ParkVipInfo> list = clientService.queryLeaguerAll();
            // 初始化excel
            HSSFSheet sheet = wb.createSheet("月租车");
            HSSFRow row = ExportUtil.creatTitleRow(sheet, Arrays.asList("车牌号码", "车牌颜色", "手机号码",
                    "姓名", "有效期起", "有效期止"));

            CreationHelper createHelper = wb.getCreationHelper();
            CellStyle cellStyle = wb.createCellStyle(); //单元格样式类
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyy/mm/dd"));

            // 设置数据
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 1);
                row.setHeight((short) (16.50 * 30));//设置行高
                ParkVipInfo vip = list.get(i);
                row.createCell(0).setCellValue(vip.getCarno());
                row.createCell(1).setCellValue(vip.getLicenceType());
                row.createCell(2).setCellValue(vip.getPhone() == null ? "" : vip.getPhone());
                row.createCell(3).setCellValue(vip.getName() == null ? "" : vip.getName());
                HSSFCell cell4 = row.createCell(4);
                HSSFCell cell5 = row.createCell(5);
                Date validBegintime = vip.getValidBegintime();
                Date expiryDate = vip.getExpiryDate();
                if (null != validBegintime) {
                    cell4.setCellValue(DateUtil.parseDateYMD(DateUtil.formatDateYMD(validBegintime)));
                    cell4.setCellStyle(cellStyle);
                } else {
                    cell4.setCellValue("");
                }
                if (null != expiryDate) {
                    cell5.setCellValue(DateUtil.parseDateYMD(DateUtil.formatDateYMD(expiryDate)));
                    cell5.setCellStyle(cellStyle);
                } else {
                    cell5.setCellValue("");
                }
            }

            for (int i = 0; i <= 6; i++) {
                sheet.setColumnWidth(i, 4000);
            }

            // 数据刘返回
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            os = response.getOutputStream();
            response.setHeader("Content-disposition", "attachment;filename=yuezuche.xls");//默认Excel名称

            wb.write(os);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != os) {
                os.close();
            }
            if (null != wb) {
                wb.close();
            }
        }
    }

    /**
     * 保存导入
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/16 14:57
     **/
    @RequestMapping(value = "/saveImportVipInfo")
    public String saveImportVipInfo(HttpServletRequest request, HttpServletResponse response, String jsonData) {
        return clientService.saveImportVipInfo(jsonData);
    }

    /**
     * 场内车删除
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/17 10:23
     **/
    @RequestMapping(value = "/deleteInnerCar")
    public String deleteInnerCar(HttpServletRequest request, HttpServletResponse response, String orderNo, String userid) {
        return clientService.deleteInnerCar(orderNo, userid);
    }

    /**
     * 场内车清空
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/17 10:23
     **/
    @RequestMapping(value = "/clearInnerCar")
    public String clearInnerCar(HttpServletRequest request, HttpServletResponse response, String orderNo, String userid, String regionCode, String start, String end) {
        return clientService.clearInnerCar(userid, regionCode, start, end);
    }

    /**
     * 场内车删除查询
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/17 11:52
     **/
    @RequestMapping(value = "/queryDelOrderList")
    public String queryDelOrderList(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate,
                                    String plateNo, String userid, String regionCode, int pagenum, int pagesize) {
        return clientService.queryDelOrderList(startDate, endDate, plateNo, userid, regionCode, pagenum, pagesize);
    }

    /**
     * 抬杆记录查询
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/17 15:22
     **/
    @RequestMapping(value = "/queryPoleList")
    public String queryPoleList(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate,
                                String userid, int pagenum, int pagesize) {
        return clientService.queryPoleList(startDate, endDate, userid, pagenum, pagesize);
    }

    /**
     * 手动匹配车号记录查询
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/21 10:44
     **/
    @RequestMapping(value = "/queryCarnoList")
    public String queryCarnoList(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate,
                                 String userid, int pagenum, int pagesize) {
        return clientService.queryCarnoList(startDate, endDate, userid, pagenum, pagesize);
    }

    /**
     * 免费放行记录查询
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/17 16:13
     **/
    @RequestMapping(value = "/queryFreeGoList")
    public String queryFreeGoList(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate,
                                  String plateNo, String userid, int pagenum, int pagesize) {
        return clientService.queryFreeGoList(startDate, endDate, plateNo, userid, pagenum, pagesize);
    }

    /**
     * 停车费用打折（免费第一天、最后一天费用）
     *
     * @Author songqiang
     * @Description
     * @Date 2021/1/13 14:15
     **/
    @RequestMapping(value = "/parkFeeDiscount")
    public String parkFeeDiscount(HttpServletRequest request, HttpServletResponse response, String orderNo, String day, String flag) {
        return clientService.parkFeeDiscount(orderNo, day, flag);
    }

    /**
     * 停车场信息统计，图表展示信息
     *
     * @Author songqiang
     * @Description
     * @Date 2021/1/29 9:23
     **/
    @RequestMapping(value = "/parkStatistics")
    public String parkStatistics(HttpServletRequest request, HttpServletResponse response, String start, String end) {
        return clientService.parkStatistics(start, end);
    }

    /**
     * 手输车号离场
     *
     * @Author songqiang
     * @Description
     * @Date 2021/3/24 15:02
     **/
    @RequestMapping(value = "/manaulDriveOut")
    public String manaulDriveOut(HttpServletRequest request, HttpServletResponse response, String plateNo,
                                 String licenceType, String deviceNo, String userId) {
        return clientService.manaulDriveOut(plateNo, licenceType, deviceNo, userId);
    }

    /**
     * 获取菜单数据
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/2 10:53
     **/
    @RequestMapping(value = "/getMenu")
    public String getMenu(HttpServletRequest request, HttpServletResponse response, String userId) {
        return clientService.getMenu(userId);
    }

    /**
     * 获取授权页面菜单选择树数据
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/2 15:16
     **/
    @RequestMapping(value = "/getMenuTreeData")
    public String getMenuTreeData(HttpServletRequest request, HttpServletResponse response) {
        return clientService.getMenuTreeData();
    }

    /**
     * 导出订单信息
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/2 9:56
     **/

    @RequestMapping(value = "/exportOrderList")
    public void exportOrderList(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate, String startDate1, String endDate1, String plateNo,
                                String orderState, String regionCode) throws IOException {

        HSSFWorkbook wb = new HSSFWorkbook();
        OutputStream os = null;
        try {
            // 获取订单数据
            Page<OrderInfo> orderPages = clientService.queryOrdersForExport(startDate, endDate, startDate1, endDate1, plateNo, orderState, regionCode);
            List<OrderInfo> orderList = orderPages.getResult();
            HSSFSheet sheet = wb.createSheet("停车场订单");
            if (orderList.size() > ExportUtil.maxExportNum) {// 最大导出数据
                // 初始化excel
                ExportUtil.creatErrorExport(sheet, "超过最大导出数据，请细分时间导出");
            } else {
                // 初始化excel
                HSSFRow row = ExportUtil.creatTitleRow(sheet, Arrays.asList("车牌号码", "驶入时间", "驶离时间",
                        "时长（分钟）", "订单状态", "订单金额", "支付金额", "车牌类型"));
                CreationHelper createHelper = wb.getCreationHelper();
                CellStyle cellStyle = wb.createCellStyle(); //单元格样式类
                cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyy/mm/dd"));

                // 设置数据
                for (int i = 0; i < orderList.size(); i++) {
                    row = sheet.createRow(i + 1);
                    row.setHeight((short) (16.50 * 30));//设置行高
                    OrderInfo order = orderList.get(i);
                    row.createCell(0).setCellValue(order.getPlateNo());// 车牌号码
                    row.createCell(1).setCellValue(order.getTimeIn());// 驶入时间
                    row.createCell(2).setCellValue(order.getTimeOut());// 驶离时间
                    row.createCell(3).setCellValue(order.getParkDuration());// 停车时长
                    // row.createCell(4).setCellValue(order.getOrderStatus());// 订单状态
                    AtomicReference<String> orderTypeAtom = new AtomicReference<>("");
                    Arrays.asList(OrderStatusEnum.values()).forEach(p -> {
                        if (p.getCode().equals(order.getOrderStatus())) {
                            orderTypeAtom.set(p.getMsg());
                        }
                    });
                    row.createCell(4).setCellValue(orderTypeAtom.toString());
                    row.createCell(5).setCellValue(order.getOrderFee());// 订单金额
                    row.createCell(6).setCellValue(order.getPayFee());// 支付金额
                    String plateType = order.getPlateType();
                    String palteTypeStr = "其他";
                    if (plateType.equals(String.valueOf(PlateTypeEnum.BLUE.getCode()))) {
                        palteTypeStr = PlateTypeEnum.BLUE.getMsg();
                    } else if (plateType.equals(String.valueOf(PlateTypeEnum.YELLOW.getCode()))) {
                        palteTypeStr = PlateTypeEnum.YELLOW.getMsg();
                    } else if (plateType.equals(String.valueOf(PlateTypeEnum.GREEN.getCode()))) {
                        palteTypeStr = PlateTypeEnum.GREEN.getMsg();
                    }
                    row.createCell(7).setCellValue(palteTypeStr);// 车牌类型
                }

                for (int i = 0; i <= 6; i++) {
                    sheet.setColumnWidth(i, 4000);
                }
            }


            // 数据刘返回
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            os = response.getOutputStream();
            String filename = "order_" + new Date().getTime() + ".xls";
            response.setHeader("Content-disposition", "attachment;filename=" + filename);//默认Excel名称

            wb.write(os);
            os.flush();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != os) {
                os.close();
            }
            if (null != wb) {
                wb.close();
            }
        }
    }

    /**
     * 导出出入记录
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/3 9:02
     **/
    @RequestMapping(value = "/exportDetectList")
    public void exportDetectList(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate, String plateNo,
                                 String direction) throws IOException {

        HSSFWorkbook wb = new HSSFWorkbook();
        OutputStream os = null;
        try {
            // 获取出入记录数据
            List<DetectInfo> detectList = clientService.queryDetectList4Export(startDate, endDate, plateNo, direction);
            HSSFSheet sheet = wb.createSheet("停车场出入记录");
            if (detectList.size() > ExportUtil.maxExportNum) {// 最大导出数据
                // 初始化excel
                ExportUtil.creatErrorExport(sheet, "超过最大导出数据，请细分时间导出");
            } else {
                // 初始化excel
                HSSFRow row = ExportUtil.creatTitleRow(sheet, Arrays.asList("车牌号码", "过车时间", "过车方向", "出入口", "区域"));
                CreationHelper createHelper = wb.getCreationHelper();
                CellStyle cellStyle = wb.createCellStyle(); //单元格样式类
                cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyy/mm/dd"));

                // 设置数据
                for (int i = 0; i < detectList.size(); i++) {
                    row = sheet.createRow(i + 1);
                    row.setHeight((short) (16.50 * 30));//设置行高
                    DetectInfo bean = detectList.get(i);
                    row.createCell(0).setCellValue(bean.getCarno());// 车牌号码
                    row.createCell(1).setCellValue(bean.getDriveTime());// 过车时间
                    String directionRow = "";
                    if (bean.getDirection().equals(DetectEnum.IN.getCode().toString())) {
                        directionRow = DetectEnum.IN.getMsg();
                    } else if (bean.getDirection().equals(DetectEnum.OUT.getCode().toString())) {
                        directionRow = DetectEnum.OUT.getMsg();
                    }
                    row.createCell(2).setCellValue(directionRow);// 过车方向
                    row.createCell(3).setCellValue(bean.getPointname());// 出入口
                    row.createCell(4).setCellValue(bean.getRegionName());// 区域
                }

                for (int i = 0; i <= 6; i++) {
                    sheet.setColumnWidth(i, 4000);
                }
            }

            // 数据刘返回
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            os = response.getOutputStream();
            String filename = "detect_" + new Date().getTime() + ".xls";
            response.setHeader("Content-disposition", "attachment;filename=" + filename);//默认Excel名称

            wb.write(os);
            os.flush();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != os) {
                os.close();
            }
            if (null != wb) {
                wb.close();
            }
        }
    }

    /**
     * 支付数据导出
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/3 9:37
     **/
    @RequestMapping(value = "/exportOrderPayList")
    public void exportOrderPayList(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate,
                                   String plateNo, String userid, String payType) throws IOException {

        HSSFWorkbook wb = new HSSFWorkbook();
        OutputStream os = null;
        try {
            // 获取出入记录数据
            List<OrderPayInfo> payList = clientService.queryOrderPay4Export(startDate, endDate, plateNo, payType, userid);
            HSSFSheet sheet = wb.createSheet("停车场支付记录");
            if (payList.size() > ExportUtil.maxExportNum) {// 最大导出数据
                // 初始化excel
                ExportUtil.creatErrorExport(sheet, "超过最大导出数据，请细分时间导出");
            } else {
                // 初始化excel
                HSSFRow row = ExportUtil.creatTitleRow(sheet, Arrays.asList("车牌号码", "驶入时间", "驶离时间",
                        "停车时长（分钟）", "支付金额", "支付时间", "支付方式", "操作人"));
                CreationHelper createHelper = wb.getCreationHelper();
                CellStyle cellStyle = wb.createCellStyle(); //单元格样式类
                cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyy/mm/dd"));

                // 设置数据
                for (int i = 0; i < payList.size(); i++) {
                    row = sheet.createRow(i + 1);
                    row.setHeight((short) (16.50 * 30));//设置行高
                    OrderPayInfo bean = payList.get(i);
                    row.createCell(0).setCellValue(bean.getCarno());// 车牌号码
                    row.createCell(1).setCellValue(bean.getCollectiondate1());// 驶入时间
                    row.createCell(2).setCellValue(bean.getCollectiondate2());// 驶离时间
                    row.createCell(3).setCellValue(bean.getDuration() == null ? 0 : bean.getDuration());// 停车时长
                    row.createCell(4).setCellValue(bean.getPayFee());// 支付金额
                    row.createCell(5).setCellValue(bean.getPayTime());// 支付时间
                    AtomicReference<String> payTypeAtom = new AtomicReference<>("");
                    Arrays.asList(PayTypeEnum.values()).forEach(p -> {
                        if (p.getCode().equals(bean.getPayType())) {
                            payTypeAtom.set(p.getMsg());
                        }
                    });
                    row.createCell(6).setCellValue(payTypeAtom.toString());// 支付方式
                    row.createCell(7).setCellValue(bean.getUsername());// 操作者
                }

                for (int i = 0; i <= 6; i++) {
                    sheet.setColumnWidth(i, 4000);
                }
            }

            // 数据刘返回
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            os = response.getOutputStream();
            String filename = "pay_" + new Date().getTime() + ".xls";
            response.setHeader("Content-disposition", "attachment;filename=" + filename);//默认Excel名称

            wb.write(os);
            os.flush();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != os) {
                os.close();
            }
            if (null != wb) {
                wb.close();
            }
        }
    }

    /**
     * @Author songqiang
     * @Description
     * @Date 2021/6/3 15:17
     **/
    @RequestMapping(value = "/exportCarsdelList")
    public void exportCarsdelList(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate,
                                  String plateNo, String userid, String regionCode) throws IOException {

        HSSFWorkbook wb = new HSSFWorkbook();

        OutputStream os = null;
        try {
            // 获取出入记录数据
            List<DelCars> delCarsList = clientService.queryCarsdel4Export(startDate, endDate, plateNo, userid, regionCode);
            HSSFSheet sheet = wb.createSheet("停车场场内车删除记录");
            if (delCarsList.size() > ExportUtil.maxExportNum) {// 最大导出数据
                // 初始化excel
                ExportUtil.creatErrorExport(sheet, "超过最大导出数据，请细分时间导出");
            } else {
                // 初始化excel
                HSSFRow row = ExportUtil.creatTitleRow(sheet, Arrays.asList("车牌号码", "驶入时间", "删除时间", "区域", "操作人"));
                CreationHelper createHelper = wb.getCreationHelper();
                CellStyle cellStyle = wb.createCellStyle(); //单元格样式类
                cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyy/mm/dd"));

                // 设置数据
                for (int i = 0; i < delCarsList.size(); i++) {
                    row = sheet.createRow(i + 1);
                    row.setHeight((short) (16.50 * 30));//设置行高
                    DelCars bean = delCarsList.get(i);
                    row.createCell(0).setCellValue(bean.getCarno());// 车牌号码
                    row.createCell(1).setCellValue(bean.getInTime());// 驶入日期
                    row.createCell(2).setCellValue(bean.getLogTime());// 删除日期
                    row.createCell(3).setCellValue(bean.getRegionName());// 区域
                    row.createCell(4).setCellValue(bean.getUserName());// 操作着
                }

                for (int i = 0; i <= 6; i++) {
                    sheet.setColumnWidth(i, 4000);
                }
            }

            // 数据刘返回
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            os = response.getOutputStream();
            String filename = "carsinparkdel_" + new Date().getTime() + ".xls";
            response.setHeader("Content-disposition", "attachment;filename=" + filename);//默认Excel名称

            wb.write(os);
            os.flush();
            os.close();

        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            if (null != os) {
                os.close();
            }
            if (null != wb) {
                wb.close();
            }
        }
    }

    /**
     * 抬杆记录导出
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/3 17:48
     **/
    @RequestMapping(value = "/exportManaulPoleList")
    public void exportManaulPoleList(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate, String userid) throws IOException {

        HSSFWorkbook wb = new HSSFWorkbook();
        OutputStream os = null;
        try {
            // 获取出入记录数据
            List<PoleControl> dataList = clientService.exportManaulPoleList(startDate, endDate, userid);
            HSSFSheet sheet = wb.createSheet("停车场抬杆记录");
            if (dataList.size() > ExportUtil.maxExportNum) {// 最大导出数据
                // 初始化excel
                ExportUtil.creatErrorExport(sheet, "超过最大导出数据，请细分时间导出");
            } else {
                // 初始化excel
                HSSFRow row = ExportUtil.creatTitleRow(sheet, Arrays.asList("设备名称", "抬杆时间", "区域", "操作人"));
                CreationHelper createHelper = wb.getCreationHelper();
                CellStyle cellStyle = wb.createCellStyle(); //单元格样式类
                cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyy/mm/dd"));

                // 设置数据
                for (int i = 0; i < dataList.size(); i++) {
                    row = sheet.createRow(i + 1);
                    row.setHeight((short) (16.50 * 30));//设置行高
                    PoleControl bean = dataList.get(i);
                    row.createCell(0).setCellValue(bean.getDeviceName());// 设备名称
                    row.createCell(1).setCellValue(bean.getLogTime());// 操作日期
                    row.createCell(2).setCellValue(bean.getRegionName());// 区域
                    row.createCell(3).setCellValue(bean.getUserName());// 操作着
                }

                for (int i = 0; i <= 6; i++) {
                    sheet.setColumnWidth(i, 4000);
                }
            }

            // 数据刘返回
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            os = response.getOutputStream();
            String filename = "pole_" + new Date().getTime() + ".xls";
            response.setHeader("Content-disposition", "attachment;filename=" + filename);//默认Excel名称

            wb.write(os);
            os.flush();
            os.close();

        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            if (null != os) {
                os.close();
            }
            if (null != wb) {
                wb.close();
            }
        }
    }

    /**
     * 免费放行记录导出
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/4 9:19
     **/
    @RequestMapping(value = "/exportFreeGoList")
    public void exportFreeGoList(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate,
                                 String plateNo, String userid) throws IOException {

        HSSFWorkbook wb = new HSSFWorkbook();
        OutputStream os = null;
        try {
            // 获取出入记录数据
            List<FreeGos> dataList = clientService.exportFreeGoList(startDate, endDate, plateNo, userid);
            HSSFSheet sheet = wb.createSheet("停车场免费放行记录");
            if (dataList.size() > ExportUtil.maxExportNum) {// 最大导出数据
                // 初始化excel
                ExportUtil.creatErrorExport(sheet, "超过最大导出数据，请细分时间导出");
            } else {
                // 初始化excel
                HSSFRow row = ExportUtil.creatTitleRow(sheet, Arrays.asList("车牌号码", "驶入时间", "驶离时间", "操作时间", "区域"));
                CreationHelper createHelper = wb.getCreationHelper();
                CellStyle cellStyle = wb.createCellStyle(); //单元格样式类
                cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyy/mm/dd"));

                // 设置数据
                for (int i = 0; i < dataList.size(); i++) {
                    row = sheet.createRow(i + 1);
                    row.setHeight((short) (16.50 * 30));//设置行高
                    FreeGos bean = dataList.get(i);
                    row.createCell(0).setCellValue(bean.getCarno());// 车牌号码
                    row.createCell(1).setCellValue(bean.getInTime());// 驶入时间
                    row.createCell(2).setCellValue(bean.getOutTime());// 驶离时间
                    row.createCell(3).setCellValue(bean.getLogTime());// 操作日期
                    row.createCell(4).setCellValue(bean.getRegionName());// 区域
                }

                for (int i = 0; i <= 6; i++) {
                    sheet.setColumnWidth(i, 4000);
                }
            }

            // 数据刘返回
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            os = response.getOutputStream();
            String filename = "freego_" + new Date().getTime() + ".xls";
            response.setHeader("Content-disposition", "attachment;filename=" + filename);//默认Excel名称

            wb.write(os);
            os.flush();
            os.close();

        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            if (null != os) {
                os.close();
            }
            if (null != wb) {
                wb.close();
            }
        }
    }

    /**
     * 手动放行记录导出
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/4 9:19
     **/
    @RequestMapping(value = "/exportCarnoList")
    public void exportCarnoList(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate,
                                String userid) throws IOException {

        HSSFWorkbook wb = new HSSFWorkbook();
        OutputStream os = null;
        try {
            // 获取出入记录数据
            List<CarnoLogBean> dataList = clientService.exportCarnoList(startDate, endDate, userid);
            HSSFSheet sheet = wb.createSheet("停车场手动放行记录");
            if (dataList.size() > ExportUtil.maxExportNum) {// 最大导出数据
                // 初始化excel
                ExportUtil.creatErrorExport(sheet, "超过最大导出数据，请细分时间导出");
            } else {
                // 初始化excel
                HSSFRow row = ExportUtil.creatTitleRow(sheet, Arrays.asList("车牌号码", "设备名称", "操作时间", "操作人"));
                CreationHelper createHelper = wb.getCreationHelper();
                CellStyle cellStyle = wb.createCellStyle(); //单元格样式类
                cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyy/mm/dd"));

                // 设置数据
                for (int i = 0; i < dataList.size(); i++) {
                    row = sheet.createRow(i + 1);
                    row.setHeight((short) (16.50 * 30));//设置行高
                    CarnoLogBean bean = dataList.get(i);
                    row.createCell(0).setCellValue(bean.getCarno());// 车牌号码
                    row.createCell(1).setCellValue(bean.getDeviceName());// 设备名称
                    row.createCell(2).setCellValue(bean.getOptTime());// 操作时间
                    row.createCell(3).setCellValue(bean.getUserName());// 操作人
                }

                for (int i = 0; i <= 6; i++) {
                    sheet.setColumnWidth(i, 4000);
                }
            }

            // 数据刘返回
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            os = response.getOutputStream();
            String filename = "manaulgo_" + new Date().getTime() + ".xls";
            response.setHeader("Content-disposition", "attachment;filename=" + filename);//默认Excel名称

            wb.write(os);
            os.flush();
            os.close();

        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            if (null != os) {
                os.close();
            }
            if (null != wb) {
                wb.close();
            }
        }
    }

    /**
     * 客户车辆添加
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/4 15:58
     **/
    @RequestMapping(value = "/leaguerCarAdd")
    public String leaguerCAddar(HttpServletRequest request, HttpServletResponse response, String leaguerId, String plateNo, String plateType) {
        return clientManagerService.leaguerCarAdd(leaguerId, plateNo, plateType);
    }

    /**
     * 客户车辆删除
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/4 15:58
     **/
    @RequestMapping(value = "/leaguerCarDelete")
    public String leaguerCarDelete(HttpServletRequest request, HttpServletResponse response, String nid) {
        return clientManagerService.leaguerCarDelete(nid);
    }

    /**
     * 客户信息删除
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/4 15:58
     **/
    @RequestMapping(value = "/deleteLeaguer4Client")
    public String deleteLeaguer4Client(HttpServletRequest request, HttpServletResponse response, String leaguerId) {
        return clientManagerService.deleteLeaguer4Client(leaguerId);
    }

    /**
     * 更新parkdb.sys_config
     *
     * @param body
     * @return
     */
    @PostMapping(value = "updateSysConfig")
    public String updateSysConfig(@RequestBody Map body) {
        String config_key = (String) body.get("config_key");
        String config_value = (String) body.get("config_value");
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
        properties.setProperty(config_key, config_value);
        return clientManagerService.updateSysConfig(config_key, config_value);
    }

}
