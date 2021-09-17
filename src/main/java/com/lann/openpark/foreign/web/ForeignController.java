package com.lann.openpark.foreign.web;

import com.lann.openpark.camera.dao.entiy.EquipInfo;
import com.lann.openpark.camera.dao.entiy.LedAndVoiceMsg;
import com.lann.openpark.camera.dao.repository.EquipInfoRepository;
import com.lann.openpark.client.service.ClientService;
import com.lann.openpark.common.Constant;
import com.lann.openpark.common.vo.PageVO;
import com.lann.openpark.common.vo.ResultVO;
import com.lann.openpark.foreign.service.ForeignService;
import com.lann.openpark.util.EhcacheUtil;
import com.lann.openpark.util.VoiceLedUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "客户端接口")
@RequestMapping(value = "/foreign")
public class ForeignController {

    @Autowired
    ForeignService foreignService;

    @Autowired
    EquipInfoRepository equipInfoRepository;

    @Autowired
    ClientService clientService;

    /**
     * 获取剩余车位数
     *
     * @return
     * @Author songqiang
     * @Description
     * @Date 2021/1/28 9:04
     */
    @RequestMapping(value = "/getRemainingSpaces")
    public ResultVO getRemainingSpaces(HttpServletRequest request, HttpServletResponse response) {
        return foreignService.getRemainingSpaces();
    }

    @RequestMapping(value = "/queryOrderList")
    public PageVO queryOrderList(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate,
                                 String plateNo, String orderState, Integer pagenum, Integer pagesize) {
        return foreignService.queryOrderList(startDate, endDate, plateNo, orderState, pagenum, pagesize);
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
     * 屏显下发
     * lineNum 行数 0 1 2 3
     * text 下发内容
     * disMode 显示模式
     * 0 立即显示，整行显示不完的字符，自动下一屏显示。<br/>1 从右向左移动显示，每移动完一满行会停留。<br/>2 从左向右移动显示，每移动完一满行会停留。<br/>3 从下往上移动显示，每移动完一满行会停留。<br/>4 从上往下移动显示，每移动完一满行会停留。<br/>5 从上下往中间拉开显示，每满行会停留。<br/>6 从中间往上下拉开显示，每满行会停留。<br/>7 从左右往中间拉开显示，每满行会停留。<br/>8 从中间往左右拉开显示，每满行会停留。<br/>13 逐字出现显示，每满行会停留。<br/>21 连续往左移动显示，中间不会停留，直到最后满行会停留。
     * enterSpeed 显示的速度 单位为毫秒
     * delayTime 停留时间 单位为秒
     * textColor 字体颜色 红色，255，绿色，65280
     * disTimes 显示的次数 0为一直循环显示,最大255次
     * cameraIds 多个设备，按照“，”连接 例：12c0ac2f-b4849bc6，a0c2b933-221a74d2
     *
     * @Author songqiang
     * @Description
     * @Date 2021/7/27 15:02
     **/
    @RequestMapping(value = "/updateLedDisplay")
    public void updateLedDisplay(int lineNum, String text,
                                 int disMode, int enterSpeed, int delayTime, int textColor, int disTimes, String cameraIds) {
        // 生成下发格式的base字符串
        Map map = VoiceLedUtil.getLedText((byte) lineNum, text, (byte) disMode, (byte) enterSpeed, (byte) delayTime, textColor, (byte) disTimes);
        // Map map = VoiceLedUtil.getLedText((byte) 2, "安全为天平安是福", (byte) 8, (byte) 0, (byte) 5, 65280, (byte) 0);
        int length = (int) map.get("length");
        String baseStr = (String) map.get("baseStr");
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        // 生成缓存消息
        LedAndVoiceMsg ledAndVoiceMsg = new LedAndVoiceMsg();
        ledAndVoiceMsg.setDataLength(length);
        ledAndVoiceMsg.setContent(baseStr);
        List<EquipInfo> equipList = equipInfoRepository.findAllById(Arrays.asList(cameraIds));
        // 循环下发
        for (EquipInfo equipInfo : equipList) {
            ledAndVoiceMsg.setChannel(equipInfo.getVoiceChannel());
            ledAndVoiceMsg.setDevicecode(equipInfo.getDevicecode());
            ehcacheUtil.put(Constant.PARK_CACHE, "update_time_" + equipInfo.getDevicecode(), ledAndVoiceMsg);
        }

    }

}
