package com.idaymay.dzt.service.impl;

import com.idaymay.dzt.bean.param.WxTokenAuthParam;
import com.idaymay.dzt.service.CheckService;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndeServiceImpl implements CheckService {

    @Autowired
    WxMpService wxMpService;

    @Override
    public String checkIndexSign(WxTokenAuthParam wxTokenAuthParam) {
        boolean fromWxFlag = wxMpService.checkSignature(wxTokenAuthParam.getTimestamp(), wxTokenAuthParam.getNonce(), wxTokenAuthParam.getSignature());
        if (fromWxFlag) {
            return wxTokenAuthParam.getEchostr();
        } else {
            return "NO";
        }
    }
}
