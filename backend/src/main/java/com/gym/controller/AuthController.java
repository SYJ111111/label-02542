package com.gym.controller;

import com.gym.annotation.Log;
import com.gym.common.BusinessException;
import com.gym.common.Result;
import com.gym.dto.LoginDTO;
import com.gym.entity.OperationLog;
import com.gym.entity.SysUser;
import com.gym.mapper.OperationLogMapper;
import com.gym.service.SysUserService;
import com.gym.util.JwtUtil;
import com.gym.util.PasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;
    private final JwtUtil jwtUtil;
    private final OperationLogMapper operationLogMapper;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        // 解码前端 Base64 加密的密码
        String rawPassword;
        try {
            rawPassword = new String(Base64.getDecoder().decode(dto.getPassword()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            rawPassword = dto.getPassword();
        }

        SysUser user = sysUserService.getByUsername(dto.getUsername());
        if (user == null || !PasswordUtil.matches(rawPassword, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        log.info("用户登录成功: {}", user.getUsername());

        // 记录登录日志
        saveLog("系统", "登录", user.getUsername(), getClientIp(request));

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", user);
        return Result.success(result);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        log.info("用户退出登录: {}", username);
        
        // 记录退出日志
        saveLog("系统", "退出登录", username, getClientIp(request));
        
        return Result.success();
    }

    @GetMapping("/info")
    public Result<SysUser> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }
        return Result.success(user);
    }

    private void saveLog(String module, String action, String operator, String ip) {
        try {
            OperationLog operationLog = new OperationLog();
            operationLog.setModule(module);
            operationLog.setAction(action);
            operationLog.setDetail(action);
            operationLog.setOperator(operator);
            operationLog.setIp(ip);
            operationLog.setCreateTime(LocalDateTime.now());
            operationLogMapper.insert(operationLog);
        } catch (Exception e) {
            log.error("记录操作日志失败: ", e);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
