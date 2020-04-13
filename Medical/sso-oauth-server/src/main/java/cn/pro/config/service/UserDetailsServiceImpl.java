package cn.pro.config.service;

import cn.pro.entity.TbPermission;
import cn.pro.entity.TbUser;
import cn.pro.mapper.UserMapper;
import cn.pro.service.TbPermissionService;
import cn.pro.service.TbUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
     /*@Resource
     UserMapper userMapper;*/

     @Autowired
    private TbUserService userService;

    @Autowired
    private TbPermissionService permissionService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TbUser tbUser = userService.getUserByUsername(username);
        //验证账户为username的用户是否存在
        if (null == tbUser){
            throw new UsernameNotFoundException("username:  " + username + "is not exist!");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        //获取用户权限
        List<TbPermission> permissions=permissionService.getByUserid(tbUser.getId());
         //设置用户权限
        permissions.forEach(permission -> {
            authorities.add(new SimpleGrantedAuthority(permission.getEname()));
        });

        //返回认证用户
        return new User(tbUser.getUsername(), tbUser.getPassword(), authorities);
    }
    /* @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.loadUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("账号不存在");
        }   user.setRoles(userMapper.getUserRolesByUid(user.getId()));
        return user ;
    }*/

}
