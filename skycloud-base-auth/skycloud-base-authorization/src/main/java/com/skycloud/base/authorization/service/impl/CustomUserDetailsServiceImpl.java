package com.skycloud.base.authorization.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashSet;

/**
 * @author
 */
@Service("userDetailsService")
@Slf4j
public class CustomUserDetailsServiceImpl implements UserDetailsService {

//    @Autowired
//    private UserService userService;
//    @Autowired
//    private RoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String username) {
//        User user = userService.getByUsername(username);
//        if(user == null){
//            throw new UsernameNotFoundException("Invalid username or password.");
//        }
        //password
        return new org.springframework.security.core.userdetails.User(
                username,
                "$2a$10$aoPFu8shqiTcxJvrDA/.MeABbg/bhi8nEfahNYlsNfsBAAZ47cRYa",
                true,
                true,
                true,
                true,
                new HashSet<>());
//        return new CustomUserDetail(0L, "admin", "password");
    }

    /**
     * 获得登录者所有角色的权限集合.
     *
     * @param user
     * @return
     */
//    private Set<GrantedAuthority> obtainGrantedAuthorities(User user) {
//        Set<Role> roles = roleService.queryUserRolesByUserId(user.getId());
//        log.info("user:{},roles:{}" , user.getUsername(), roles);
//        return roles.stream()
//                .map(role -> new SimpleGrantedAuthority(role.getCode()))
//                .collect(Collectors.toSet());
//    }
}
