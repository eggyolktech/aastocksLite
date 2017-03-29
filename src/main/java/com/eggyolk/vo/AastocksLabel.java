package com.eggyolk.vo;

/**
 * Created by Hin on 3/26/2017.
 */
public class AastocksLabel {
    @Override
    public String toString() {
        return "AastocksLabel{" +
                "code='" + code + '\'' +
                ", descEn='" + descEn + '\'' +
                ", descZh='" + descZh + '\'' +
                '}';
    }

    public String code;
    public String descEn;
    public String descZh;
}
