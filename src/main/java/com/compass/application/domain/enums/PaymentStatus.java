package com.compass.application.domain.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING(1, "PENDING"),
    PAID(2, "PAID"),
    CANCELED(3, "CANCELED");

    private final int code;
    private final String description;

    private PaymentStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static PaymentStatus toEnum(Integer code){
        if(code == null){
            return null;
        }

        for(PaymentStatus x : PaymentStatus.values()){
            if(code.equals(x.getCode())){
                return x;
            }
        }

        throw new IllegalArgumentException("Invalid ID: " + code);
    }
}
