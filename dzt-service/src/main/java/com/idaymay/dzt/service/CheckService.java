package com.idaymay.dzt.service;

import com.idaymay.dzt.bean.param.WxTokenAuthParam;

public interface CheckService {

    /**
     * 公众号配置认证
     *
     * @param wxTokenAuthParam
     * @return
     */
    public String checkIndexSign(WxTokenAuthParam wxTokenAuthParam);
}
