package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.annotation.Log;
import com.gym.common.BusinessException;
import com.gym.common.Result;
import com.gym.entity.CardType;
import com.gym.entity.MemberCard;
import com.gym.service.CardTypeService;
import com.gym.service.MemberCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/member-cards")
@RequiredArgsConstructor
public class MemberCardController {

    private final MemberCardService memberCardService;
    private final CardTypeService cardTypeService;

    @GetMapping
    public Result<Page<MemberCard>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Long cardTypeId,
            @RequestParam(required = false) Integer status) {
        return Result.success(memberCardService.pageQuery(pageNum, pageSize, memberId, cardTypeId, status));
    }

    @GetMapping("/{id}")
    public Result<MemberCard> getById(@PathVariable Long id) {
        MemberCard memberCard = memberCardService.getDetailById(id);
        if (memberCard == null) {
            throw new BusinessException("开卡记录不存在");
        }
        return Result.success(memberCard);
    }

    @GetMapping("/date-range/{cardTypeId}")
    public Result<Map<String, LocalDate>> getDateRangeByCardType(@PathVariable Long cardTypeId) {
        CardType cardType = cardTypeService.getById(cardTypeId);
        if (cardType == null) {
            throw new BusinessException("卡种不存在");
        }
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(cardType.getDuration());
        Map<String, LocalDate> dateRange = new HashMap<>();
        dateRange.put("startDate", startDate);
        dateRange.put("endDate", endDate);
        return Result.success(dateRange);
    }

    @PostMapping
    @Log(module = "开卡管理", action = "新增开卡")
    public Result<Void> create(@Valid @RequestBody MemberCard memberCard) {
        validateDateRange(memberCard);
        memberCardService.save(memberCard);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Log(module = "开卡管理", action = "修改开卡")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody MemberCard memberCard) {
        validateDateRange(memberCard);
        memberCard.setId(id);
        memberCardService.updateById(memberCard);
        return Result.success();
    }

    private void validateDateRange(MemberCard memberCard) {
        if (memberCard.getStartDate() != null) {
            if (memberCard.getStartDate().isBefore(java.time.LocalDate.now())) {
                throw new BusinessException("开始日期不能小于当前日期");
            }
        }
        if (memberCard.getStartDate() != null && memberCard.getEndDate() != null) {
            if (!memberCard.getEndDate().isAfter(memberCard.getStartDate())) {
                throw new BusinessException("结束日期必须大于开始日期");
            }
        }
    }

    @DeleteMapping("/{id}")
    @Log(module = "开卡管理", action = "删除开卡记录")
    public Result<Void> delete(@PathVariable Long id) {
        memberCardService.removeById(id);
        return Result.success();
    }
}
