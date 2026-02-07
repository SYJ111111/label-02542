package com.gym.controller;

import com.gym.common.Result;
import com.gym.service.CardTypeService;
import com.gym.service.MemberCardService;
import com.gym.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final MemberService memberService;
    private final CardTypeService cardTypeService;
    private final MemberCardService memberCardService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("memberCount", memberService.count());
        stats.put("cardTypeCount", cardTypeService.count());
        stats.put("activeCardCount", memberCardService.countActive());
        stats.put("totalRevenue", memberCardService.totalRevenue());
        return Result.success(stats);
    }
}
