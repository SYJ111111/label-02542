package com.gym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("member_card")
public class MemberCard {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "会员不能为空")
    private Long memberId;

    @NotNull(message = "卡种不能为空")
    private Long cardTypeId;

    @NotNull(message = "开始日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer remainingCount;

    private BigDecimal amountPaid;

    /** 1-有效 2-已过期 3-已停用 */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /** 会员姓名（非数据库字段） */
    @TableField(exist = false)
    private String memberName;

    /** 会员手机号（非数据库字段） */
    @TableField(exist = false)
    private String memberPhone;

    /** 卡种名称（非数据库字段） */
    @TableField(exist = false)
    private String cardTypeName;
}
