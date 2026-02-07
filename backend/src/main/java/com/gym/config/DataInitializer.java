package com.gym.config;

import com.gym.entity.Member;
import com.gym.service.CardTypeService;
import com.gym.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CardTypeService cardTypeService;
    private final MemberService memberService;

    @Override
    public void run(ApplicationArguments args) {
        cardTypeService.initDefaultCardTypes();
        initSampleMembers();
        log.info("系统数据初始化完成");
    }

    private void initSampleMembers() {
        if (memberService.count() > 0) {
            return;
        }
        String[][] members = {
            {"张伟", "13800001001", "1", "1990-05-12"},
            {"李娜", "13800001002", "0", "1995-08-23"},
            {"王磊", "13800001003", "1", "1988-11-05"},
            {"赵敏", "13800001004", "0", "1992-03-17"},
            {"刘洋", "13800001005", "1", "1998-07-30"}
        };
        for (String[] m : members) {
            Member member = new Member();
            member.setName(m[0]);
            member.setPhone(m[1]);
            member.setGender(Integer.parseInt(m[2]));
            member.setBirthday(LocalDate.parse(m[3]));
            member.setStatus(1);
            memberService.save(member);
        }
        log.info("已初始化 {} 条示例会员数据", members.length);
    }
}
