package com.lann.openpark.common;

public class Constant {

    public static int SEND_MSG_TYPE = 3;// 下发语音消息的类型

    public static final String IMG_SERVER = "/FileUpload/";// 图片服务器

    public static final String DRIVE_IN_CODE = "9";// 驶入标志
    public static final String DRIVE_OUT_CODE = "10";// 驶离标志\

    public static final String CAR_COLOR = "A";// 车身颜色，暂时写死
    public static final int UNNORMAL_OUT = 1;// 驶离异常

    public static int UPLOAD_SUCCESS = 10;// 上传成功标志

    ////////////////////支付方式////////////////////////////////
    public static final String PAY_TYPE_HYYE = "1201";// 会员余额
    public static final String PAY_TYPE_ZFB = "1202";// 支付宝
    public static final String PAY_TYPE_YHK = "1204";// 银行卡
    public static final String PAY_TYPE_WX = "1205";// 微信
    public static final String PAY_TYPE_CASH = "1206";// 现金支付
    public static final String PAY_TYPE_TCK = "1211";// 停车卡
    public static final String PAY_TYPE_SHDK = "1226";// 商户代扣
    public static final String PAY_TYPE_ETC = "1229";// ETC扣费
    ////////////////////支付方式////////////////////////////////

    public static final String EQUIP_TYPE_CAMERA = "01";// 智能摄像机

    public static final String PARK_TYPE_WHITELIST = "1";// 白名单
    public static final String PARK_TYPE_TMP = "9";// 临时车
    public static final String EXIT_TYPE_AUTO = "1";// 自动放行
    public static final String EXIT_TYPE_FREE = "4";// 免费放行
    public static final String EXIT_TYPE_EXCEPTION = "6";// 无出场结束订单
    public static final String EXIT_TYPE_TMP = "7";// 欠费驶离
    public static final String EXIT_TYPE_OPEN_EPARK = "8";// openepark通知
    public static final String EXIT_TYPE_OPEN_DEL = "9";// 人工删除
    public static final String MANUAL_FEE = "2";// 人工收费放行

    public static final String VERIFY_URL = "verifyServer.do?";//服务对接验证接口地址
    public static final String BIND_URL = "bindPark.do?";//服务对接验证接口地址
    public static final String CUSTOMDATA_URL = "push/customData.do?";//异步上传接口
    public static final String SYNC_CUSTOMDATA_URL = "/push/synchronousCustomData.do?";//同步上传接口

    public static final String EPARK_TYPE_SMALL = "7201";// 小型车
    public static final String LICENSE_TYPE_SMALL = "2";// 小型车

    public static final String PARK_CACHE = "park_cache";// 停车场设备缓存名称
    public static final String CONFIG_CACHE = "config_cache";// 停车场配置缓存名称
    public static final String PARK_CLIENT = "park_client";//

    public static final String SEND_IN_MESSAGE = "IN_MESSAGE";
    public static final String SEND_OUT_MESSAGE = "OUT_MESSAGE";
    public static final String SEND_PAY_MESSAGE = "PAY_MESSAGE";

    // 区域类型
    public static final String REGION_TYPE_0 = "0";// 独立区域
    public static final String REGION_TYPE_1 = "1";// 嵌套区域


    // camera上传的车牌类型
    // 0：未知车牌:、1：蓝牌小汽车、2：:黑牌小汽车、3：单排黄牌、4：双排黄牌、 5：警车车牌（白色）、
    // 6：武警车牌（白色）、7：个性化车牌（视为蓝牌）、8：单排军车牌（白色）、9：双排军车牌（白色）、10：使馆车牌（黑色）、
    // 11：香港进出中国大陆车牌（视为蓝色）、12：农用车（黄色） 13：教练车牌（蓝色）、14：澳门进出中国大陆车牌（视为蓝色）、15：双层武警车牌（白色）、
    // 16：武警总队车牌（白色）、17：双层武警总队车牌（白色）、18：民航车牌（蓝色）、19：新能源车牌（绿色）
    // 好停车车牌类型
    // 7201 蓝牌（小型车） 7202 黄牌（中型车） 7203 黑牌（大型车） 7204 白绿牌 7205 黄绿牌 7206 纯绿牌 7207 白牌
    // 车辆型号(7201：小型车；7202：中型车；7203：大型车；7204：新能源小型车；7205：新能源中型车；7206：新能源大型车)

    /**
     * openEpark车牌类型转Epark
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/2 10:18
     **/
    public static String convertPlateOpenEpark4Epark(int plateColor) {

        switch (plateColor) {
            case 0:
                return "7207";
            case 1:
                return "7202";
            case 2:
                return "7201";
            case 3:
                return "7203";
            case 4:
                return "7206";
            case 5:
                return "7204";
            case 6:
                return "7205";
        }
        return "7201";
    }

//    /**Epark车牌类型转OpenEpark
//     * @Author songqiang
//     * @Description
//     * @Date 2020/7/2 10:18
//    **/
//    public static String convertPlateEpark4OpenEpark(String plateType) {
//        switch (plateType) {
//            case "7207":
//                return "0";
//            case "7202":
//                return "1";
//            case "7201":
//                return "2";
//            case "7203":
//                return "3";
//            case "7206":
//                return "4";
//            case "7204":
//                return "5";
//            case "7205":
//                return "6";
//        }
//        return "2";
//    }


//    /**
//     * 相机上报车牌类型转换成epark车牌类型
//     * @Author songqiang
//     * @Description
//     * @Date 2020/7/3 15:48
//    **/
//    public static String convertCamera4OpenEpark(int plateType) {
//        // camera上传的车牌类型
//        // 0：未知车牌:、1：蓝牌小汽车、2：:黑牌小汽车、3：单排黄牌、4：双排黄牌、 5：警车车牌（白色）、
//        //        // 6：武警车牌（白色）、7：个性化车牌（视为蓝牌）、8：单排军车牌（白色）、9：双排军车牌（白色）、10：使馆车牌（黑色）、
//        //        // 11：香港进出中国大陆车牌（视为蓝色）、12：农用车（黄色） 13：教练车牌（蓝色）、14：澳门进出中国大陆车牌（视为蓝色）、15：双层武警车牌（白色）、
//        //        // 16：武警总队车牌（白色）、17：双层武警总队车牌（白色）、18：民航车牌（蓝色）、19：新能源车牌（绿色）
//        //        // 好停车车牌类型
//        //        // 7201 蓝牌（小型车） 7202 黄牌（中型车） 7203 黑牌（大型车） 7204 白绿牌 7205 黄绿牌 7206 纯绿牌 7207 白牌
//        //        // 车辆型号(7201：小型车；7202：中型车；7203：大型车；7204：新能源小型车；7205：新能源中型车；7206：新能源大型车)
//        // openEpark车牌类型
//        // 01 黄牌 02 蓝牌 03 黑牌 04 绿牌 05 白绿 06 黄绿
//        if(0==plateType||1==plateType||2==plateType||
//                5==plateType||6==plateType||7==plateType||10==plateType||
//                11==plateType||13==plateType||14==plateType||16==plateType||18==plateType){
//            return "7201";
//        }else if(3==plateType||4==plateType||8==plateType||9==plateType
//                ||10==plateType||12==plateType||15==plateType||17==plateType){
//            return "7202";
//        }
//        else if(19==plateType){// 新能源小型车
//            return "7204";
//        }
//        else{
//            return "02";
//        }
//    }

    /**
     * 相机识别车牌颜色转换成好停车车牌颜色
     * // openEpark车牌类型
     * // 01 黄牌 02 蓝牌 03 黑牌 04 绿牌 05 白绿 06 黄绿
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/6 10:14
     **/
    public static int convertColorType4OpenEpark(int colorType) {
        switch (colorType) {
            case 1:
                return 2;
            case 2:
                return 1;
            case 3:
                return 0;
            case 4:
                return 3;
            case 5:
                return 5;// 新能源都是白绿
        }
        return 2;
    }

    /**
     * 车牌颜色转换成ETC车牌颜色
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/4 17:58
     **/
    // 0：未知、1：蓝色、2：黄色、3：白色、4：黑 色、5：绿色
    // ETC 0:蓝色、1:黄色、2:黑色、3: 白色、4:渐变绿色、5:黄绿双拼、6:绿色、7: 蓝白渐变
    public static String convertColorType4Etc(int colorType) {
        switch (colorType) {
            case 1:
                return "0";
            case 2:
                return "1";
            case 3:
                return "3";
            case 4:
                return "2";
            case 5:
                return "4";// 新能源都是白绿
        }
        return "0";
    }

    // 车辆型号(7201：小型车；7202：中型车；7203：大型车；7204：新能源小型车；7205：新能源中型车；7206：新能源大型车)
    // 0:小车 1:大车 2:超大车
    public static String convertVehicleType(String vehicleType) {
        if ("7201".equals(vehicleType) || "7204".equals(vehicleType)) {
            return "0";
        } else if ("7202".equals(vehicleType) || "7205".equals(vehicleType)) {
            return "1";
        } else if ("7203".equals(vehicleType) || "7206".equals(vehicleType)) {
            return "2";
        } else {
            return "1";
        }
    }

    /**
     * 获取支付方式名称
     *
     * @Author songqiang
     * @Description
     * @Date 2020/10/30 15:32
     **/
    public static String getPayTypeName(String payType) {
        if ("1201".equals(payType)) {
            return "帐户储值";
        } else if ("1202".equals(payType)) {
            return "支付宝";
        } else if ("1203".equals(payType)) {
            return "信用卡";
        } else if ("1204".equals(payType)) {
            return "银行卡";
        } else if ("1205".equals(payType)) {
            return "微信";
        } else if ("1206".equals(payType)) {
            return "现金";
        } else if ("1211".equals(payType)) {
            return "停车卡";
        } else if ("1212".equals(payType)) {
            return "公众号支付";
        } else if ("1229".equals(payType)) {
            return "ETC支付";
        } else {
            return "其他支付方式";
        }
    }

    /**
     * 获取车牌类型名
     *
     * @Author songqiang
     * @Description
     * @Date 2020/10/30 16:45
     **/
    public static String getCarTypeName(String licenseType) {
        if ("1".equals(licenseType)) {
            return "黄牌车";
        } else if ("2".equals(licenseType)) {
            return "蓝牌车";
        } else if ("03".equals(licenseType)) {
            return "黑牌车";
        } else if ("04".equals(licenseType)) {
            return "新能源车";
        } else if ("05".equals(licenseType)) {
            return "新能源车";
        } else if ("06".equals(licenseType)) {
            return "新能源大车";
        } else {
            return "其他车牌";
        }
    }

}
