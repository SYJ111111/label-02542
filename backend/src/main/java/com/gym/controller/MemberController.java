package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.annotation.Log;
import com.gym.common.BusinessException;
import com.gym.common.Result;
import com.gym.entity.Member;
import com.gym.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public Result<Page<Member>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status) {
        return Result.success(memberService.pageQuery(pageNum, pageSize, name, phone, status));
    }

    @GetMapping("/{id}")
    public Result<Member> getById(@PathVariable Long id) {
        Member member = memberService.getById(id);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        return Result.success(member);
    }

    @PostMapping
    @Log(module = "会员管理", action = "新增会员")
    public Result<Void> create(@Valid @RequestBody Member member) {
        memberService.checkPhoneUnique(member.getPhone(), null);
        memberService.save(member);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Log(module = "会员管理", action = "修改会员")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody Member member) {
        memberService.checkPhoneUnique(member.getPhone(), id);
        member.setId(id);
        memberService.updateById(member);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Log(module = "会员管理", action = "删除会员")
    public Result<Void> delete(@PathVariable Long id) {
        memberService.checkBeforeDelete(id);
        memberService.removeById(id);
        return Result.success();
    }
}
