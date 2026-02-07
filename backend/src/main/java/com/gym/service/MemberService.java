package com.gym.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.BusinessException;
import com.gym.entity.Member;
import com.gym.entity.MemberCard;
import com.gym.mapper.MemberCardMapper;
import com.gym.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class MemberService extends ServiceImpl<MemberMapper, Member> {

    private final MemberCardMapper memberCardMapper;

    /**
     * 分页查询会员列表
     */
    public Page<Member> pageQuery(Integer pageNum, Integer pageSize, String name, String phone, Integer status) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Member::getName, name)
               .like(StringUtils.hasText(phone), Member::getPhone, phone)
               .eq(status != null, Member::getStatus, status)
               .orderByDesc(Member::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 检查手机号是否已存在
     */
    public void checkPhoneUnique(String phone, Long excludeId) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getPhone, phone);
        if (excludeId != null) {
            wrapper.ne(Member::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException("该手机号已被注册");
        }
    }

    /**
     * 删除会员前检查关联数据
     */
    public void checkBeforeDelete(Long id) {
        long cardCount = memberCardMapper.selectCount(
                new LambdaQueryWrapper<MemberCard>().eq(MemberCard::getMemberId, id));
        if (cardCount > 0) {
            throw new BusinessException("该会员存在开卡记录，无法删除");
        }
    }
}
