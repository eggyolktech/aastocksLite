package com.eggyolk.vo;

/**
 * Created by Hin on 3/26/2017.
 */
public class Label {
    @Override
    public String toString() {
        return "Label{" +
                "code='" + code + '\'' +
                ", descEn='" + descEn + '\'' +
                ", descZh='" + descZh + '\'' +
                ", addInfo='" + addInfo + '\'' +
                '}';
    }

    public String code;
    public String descEn;
    public String descZh;
    public String addInfo;
}
