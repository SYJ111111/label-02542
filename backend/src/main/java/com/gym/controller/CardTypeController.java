package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.annotation.Log;
import com.gym.common.BusinessException;
import com.gym.common.Result;
import com.gym.entity.CardType;
import com.gym.service.CardTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/card-types")
@RequiredArgsConstructor
public class CardTypeController {

    private final CardTypeService cardTypeService;

    @GetMapping
    public Result<Page<CardType>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        return Result.success(cardTypeService.pageQuery(pageNum, pageSize, name, type, status));
    }

    @GetMapping("/list")
    public Result<List<CardType>> listEnabled() {
        return Result.success(cardTypeService.listEnabled());
    }

    @GetMapping("/{id}")
    public Result<CardType> getById(@PathVariable Long id) {
        CardType cardType = cardTypeService.getById(id);
        if (cardType == null) {
            throw new BusinessException("卡种不存在");
        }
        return Result.success(cardType);
    }

    @PostMapping
    @Log(module = "卡种管理", action = "新增卡种")
    public Result<Void> create(@Valid @RequestBody CardType cardType) {
        cardTypeService.save(cardType);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Log(module = "卡种管理", action = "修改卡种")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CardType cardType) {
        cardType.setId(id);
        cardTypeService.updateById(cardType);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Log(module = "卡种管理", action = "删除卡种")
    public Result<Void> delete(@PathVariable Long id) {
        cardTypeService.checkBeforeDelete(id);
        cardTypeService.removeById(id);
        return Result.success();
    }
}
