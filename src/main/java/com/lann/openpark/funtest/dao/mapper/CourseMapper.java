package com.lann.openpark.funtest.dao.mapper;

import com.github.pagehelper.Page;
import com.lann.openpark.funtest.bean.CourseBase;
import com.lann.openpark.funtest.bean.CourseInfo;
import com.lann.openpark.funtest.bean.CourseListRequest;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by Administrator.
 */
@Mapper
public interface CourseMapper {
   CourseBase findCourseBaseById(String id);
   Page<CourseBase> findCourseList();
   //我的课程查询列表
   Page<CourseInfo> findCourseListPage(CourseListRequest courseListRequest);
}
