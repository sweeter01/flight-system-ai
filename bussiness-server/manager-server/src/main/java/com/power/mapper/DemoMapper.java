package com.power.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DemoMapper {

    @Select("select username from sys_user where is_deleted = 0")
    String getDemo();
}