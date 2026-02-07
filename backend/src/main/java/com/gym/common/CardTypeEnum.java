package com.gym.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 卡种类型枚举
 */
@Getter
@AllArgsConstructor
public enum CardTypeEnum {

    COUNT_CARD(1, "次卡"),
    MONTHLY(2, "月卡"),
    QUARTERLY(3, "季卡"),
    YEARLY(4, "年卡");

    private final int code;
    private final String label;

    public static String getLabelByCode(int code) {
        for (CardTypeEnum e : values()) {
            if (e.code == code) {
                return e.label;
            }
        }
        return "未知";
    }
}
