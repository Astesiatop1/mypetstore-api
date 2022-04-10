package org.csu.mypetstore.api.common;

import lombok.Getter;

@Getter
public enum ResponseCode {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"SUCCESS"),
    NEED_LOGIN(10,"SUCCESS"),
    ILLEGAL_ARGUMENT(2,"SUCCESS");



    private final int code;
    private final String description;

    ResponseCode(int code,String description){
        this.code = code;
        this.description  = description;
    }
}
