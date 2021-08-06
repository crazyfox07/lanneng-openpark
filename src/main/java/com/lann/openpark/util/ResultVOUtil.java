package com.lann.openpark.util;


import com.github.pagehelper.Page;
import com.lann.openpark.common.enums.ResultEnum;
import com.lann.openpark.common.vo.PageVO;
import com.lann.openpark.common.vo.ResultVO;

public class ResultVOUtil {

    /**
     * 请求成功带data
     *
     * @Author songqiang
     * @Description
     * @Date 2021/7/30 10:28
     **/
    public static ResultVO success(Object o) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(0);
        resultVO.setMessage("请求成功");
        resultVO.setData(o);
        return resultVO;
    }

    /**
     * 请求成功不带data
     *
     * @Author songqiang
     * @Description
     * @Date 2021/7/30 10:28
     **/
    public static ResultVO success() {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(0);
        resultVO.setMessage("请求成功");
        return resultVO;
    }

    /**
     * 返回错误
     *
     * @Author songqiang
     * @Description
     * @Date 2021/7/30 10:28
     **/
    public static ResultVO error(ResultEnum resultEnum) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(resultEnum.getCode());
        resultVO.setMessage(resultEnum.getMessage());
        return resultVO;
    }

    /**
     * 分页请求数据
     *
     * @Author songqiang
     * @Description
     * @Date 2021/7/30 10:28
     **/
    public static PageVO pageResult(Page page) {
        PageVO pageVO = new PageVO();
        pageVO.setCode(0);
        pageVO.setMessage("请求成功");
        pageVO.setData(page.getResult());
        pageVO.setTotal(page.getTotal());
        pageVO.setPagenum(page.getPageNum());
        pageVO.setPagesize(page.getPageSize());
        return pageVO;
    }

    /**
     * 分页请求数据错误信息
     *
     * @Author songqiang
     * @Description
     * @Date 2021/7/30 10:29
     **/
    public static PageVO pageError(ResultEnum resultEnum) {
        PageVO pageVO = new PageVO();
        pageVO.setCode(resultEnum.getCode());
        pageVO.setMessage(resultEnum.getMessage());
        return pageVO;
    }
}
