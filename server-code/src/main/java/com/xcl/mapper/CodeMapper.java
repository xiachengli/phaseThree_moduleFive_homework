package com.xcl.mapper;

import com.xcl.pojo.Code;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CodeMapper {

    @Insert({"insert into code values(#{id},#{email},#{code},NOW(),DATE_ADD(NOW(),INTERVAL 10 MINUTE))"})
    int save(@Param("id") Long id, @Param("email") String email, @Param("code") String code);

    @Select("select * from code where email = #{email} ORDER BY create_time DESC ")
    List<Code> queryByEmail(String email);
}
