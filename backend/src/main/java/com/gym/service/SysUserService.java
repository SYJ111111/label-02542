package com.gym.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.SysUser;
import com.gym.mapper.SysUserMapper;
import com.gym.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Order(1)
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> implements ApplicationRunner {

    /**
     * 根据用户名查询用户
     */
    public SysUser getByUsername(String username) {
        return lambdaQuery().eq(SysUser::getUsername, username).one();
    }

    /**
     * 系统启动时初始化管理员账号
     */
    @Override
    public void run(ApplicationArguments args) {
        if (count() == 0) {
            SysUser admin = new SysUser();
            admin.setUsername("admin");
            admin.setPassword(PasswordUtil.encode("123456"));
            admin.setRealName("系统管理员");
            admin.setStatus(1);
            save(admin);
            log.info("已初始化管理员账号: admin / 123456");
        }
    }
}
