package com.travelfourm.entity;

/**
 * @author 34612
 */

public enum ReturnStatusEnum {

    /**
     * 0：成功
     * 1：失败*/
    SUCCESS(0),
    FAILURE(1);

    private final int value;

    ReturnStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
