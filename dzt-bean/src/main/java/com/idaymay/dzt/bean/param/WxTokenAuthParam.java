package com.idaymay.dzt.bean.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WxTokenAuthParam {

    private String signature;

    private String timestamp;

    private String nonce;

    private String echostr;

}
