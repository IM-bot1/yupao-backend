package com.yupi.yupao.model.domain.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * ClassName:TeamStatusEnum
 * Package:com.yupi.yupao.model.domain.enums
 * User:HP
 * Date:2025/3/12
 * Time:20:34
 *
 * @Author 周东汉
 * @Version 1.0
 * Description:
 */
@Getter
public enum TeamStatusEnum {
    /**
     * 公开
     */
    PUBLIC(0,"公开"),
    /**
     * 私密
     */
    PRIVATE(1,"私密"),
    /**
     * 加密
     */
    SECRET(2,"加密");

    private int value;
    private String desc;
    TeamStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
//    public int getValue() {
//        return value;
//    }
//    public String getDesc() {
//        return desc;
//    }
    public static TeamStatusEnum getEnumByValue(Integer value) {
        if (value == null){
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum valueEnum : values) {
            if (valueEnum.getValue() == value){
                return valueEnum;
            }
        }
        return null;
    }
}
