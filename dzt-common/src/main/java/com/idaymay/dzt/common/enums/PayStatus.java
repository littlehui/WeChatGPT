package com.idaymay.dzt.common.enums;

/**
 * 支付状态
 *
 * @author cch on 2022/09/13
 **/
public enum PayStatus {

    WAIT_PAY("0", "待支付");

    public String value;
    public String valueZh;

    PayStatus(String value, String valueZh) {
        this.value = value;
        this.valueZh = valueZh;
    }
}
