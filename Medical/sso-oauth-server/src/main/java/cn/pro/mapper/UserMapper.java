package cn.pro.mapper;

import cn.pro.entity.Role;
import cn.pro.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    List<Role> getUserRolesByUid(int id);

    User loadUserByUsername(String s);
}
