package com.xcl.mapper;

import com.xcl.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("select * from user where email = #{email} and password = #{password}")
    User login(String email, String password);

    @Update("update user set token = #{token} where email = #{email}")
    void saveToken(String email, String token);

    @Select("select * from user where email = #{email}")
    User queryByEmail(String email);

    @Insert("insert into user values(#{id},#{email},#{password},#{token})")
    void save(User user);
}
