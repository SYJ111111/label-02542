package com.gym.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.BusinessException;
import com.gym.entity.CardType;
import com.gym.entity.MemberCard;
import com.gym.mapper.CardTypeMapper;
import com.gym.mapper.MemberCardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardTypeService extends ServiceImpl<CardTypeMapper, CardType> {

    private final MemberCardMapper memberCardMapper;

    /**
     * 分页查询卡种列表
     */
    public Page<CardType> pageQuery(Integer pageNum, Integer pageSize, String name, Integer type, Integer status) {
        LambdaQueryWrapper<CardType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), CardType::getName, name)
               .eq(type != null, CardType::getType, type)
               .eq(status != null, CardType::getStatus, status)
               .orderByDesc(CardType::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 查询全部启用的卡种（用于下拉选择）
     */
    public List<CardType> listEnabled() {
        return lambdaQuery().eq(CardType::getStatus, 1).orderByAsc(CardType::getType).list();
    }

    /**
     * 删除卡种前检查关联数据
     */
    public void checkBeforeDelete(Long id) {
        long cardCount = memberCardMapper.selectCount(
                new LambdaQueryWrapper<MemberCard>().eq(MemberCard::getCardTypeId, id));
        if (cardCount > 0) {
            throw new BusinessException("该卡种已被使用，无法删除");
        }
    }

    /**
     * 系统启动时初始化默认卡种
     */
    public void initDefaultCardTypes() {
        if (count() == 0) {
            CardType monthly = new CardType();
            monthly.setName("月卡");
            monthly.setType(2);
            monthly.setDuration(30);
            monthly.setPrice(new java.math.BigDecimal("299.00"));
            monthly.setStatus(1);
            monthly.setDescription("30天无限次使用");
            save(monthly);

            CardType quarterly = new CardType();
            quarterly.setName("季卡");
            quarterly.setType(3);
            quarterly.setDuration(90);
            quarterly.setPrice(new java.math.BigDecimal("799.00"));
            quarterly.setStatus(1);
            quarterly.setDescription("90天无限次使用");
            save(quarterly);

            CardType yearly = new CardType();
            yearly.setName("年卡");
            yearly.setType(4);
            yearly.setDuration(365);
            yearly.setPrice(new java.math.BigDecimal("2399.00"));
            yearly.setStatus(1);
            yearly.setDescription("365天无限次使用");
            save(yearly);

            CardType countCard = new CardType();
            countCard.setName("次卡(10次)");
            countCard.setType(1);
            countCard.setDuration(180);
            countCard.setTotalCount(10);
            countCard.setPrice(new java.math.BigDecimal("199.00"));
            countCard.setStatus(1);
            countCard.setDescription("10次使用，180天有效");
            save(countCard);
        }
    }
}
