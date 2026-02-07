package com.gym.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("card_type")
public class CardType {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "卡种名称不能为空")
    private String name;

    /** 1-次卡 2-月卡 3-季卡 4-年卡 */
    @NotNull(message = "卡种类型不能为空")
    private Integer type;

    /** 有效天数 */
    @NotNull(message = "有效天数不能为空")
    private Integer duration;

    /** 次卡总次数 */
    private Integer totalCount;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    private Integer status;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
