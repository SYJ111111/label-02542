package com.gym.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.CardType;
import com.gym.entity.Member;
import com.gym.entity.MemberCard;
import com.gym.mapper.CardTypeMapper;
import com.gym.mapper.MemberCardMapper;
import com.gym.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberCardService extends ServiceImpl<MemberCardMapper, MemberCard> {

    private final MemberMapper memberMapper;
    private final CardTypeMapper cardTypeMapper;

    /**
     * 分页查询开卡记录（含会员名称和卡种名称）
     */
    public Page<MemberCard> pageQuery(Integer pageNum, Integer pageSize, Long memberId, Long cardTypeId, Integer status) {
        LambdaQueryWrapper<MemberCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(memberId != null, MemberCard::getMemberId, memberId)
               .eq(cardTypeId != null, MemberCard::getCardTypeId, cardTypeId)
               .eq(status != null, MemberCard::getStatus, status)
               .orderByDesc(MemberCard::getCreateTime);

        Page<MemberCard> result = page(new Page<>(pageNum, pageSize), wrapper);
        fillRelatedInfo(result.getRecords());
        return result;
    }

    /**
     * 填充关联的会员和卡种信息
     */
    private void fillRelatedInfo(List<MemberCard> records) {
        if (records == null || records.isEmpty()) {
            return;
        }

        Set<Long> memberIds = records.stream().map(MemberCard::getMemberId).collect(Collectors.toSet());
        Set<Long> cardTypeIds = records.stream().map(MemberCard::getCardTypeId).collect(Collectors.toSet());

        Map<Long, Member> memberMap = memberMapper.selectBatchIds(memberIds).stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));
        Map<Long, CardType> cardTypeMap = cardTypeMapper.selectBatchIds(cardTypeIds).stream()
                .collect(Collectors.toMap(CardType::getId, Function.identity()));

        records.forEach(mc -> {
            Member member = memberMap.get(mc.getMemberId());
            CardType cardType = cardTypeMap.get(mc.getCardTypeId());
            if (member != null) {
                mc.setMemberName(member.getName());
                mc.setMemberPhone(member.getPhone());
            }
            if (cardType != null) {
                mc.setCardTypeName(cardType.getName());
            }
        });
    }

    /**
     * 获取详情（含关联信息）
     */
    public MemberCard getDetailById(Long id) {
        MemberCard memberCard = getById(id);
        if (memberCard != null) {
            fillRelatedInfo(List.of(memberCard));
        }
        return memberCard;
    }

    /**
     * 统计有效卡数量
     */
    public long countActive() {
        return lambdaQuery().eq(MemberCard::getStatus, 1).count();
    }

    /**
     * 统计收入总额
     */
    public java.math.BigDecimal totalRevenue() {
        List<MemberCard> all = list();
        return all.stream()
                .map(mc -> mc.getAmountPaid() != null ? mc.getAmountPaid() : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
}
