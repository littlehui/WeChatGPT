package com.idaymay.dzt.common.ajax;


import lombok.Getter;
import lombok.Setter;

public enum ResponseEnum {

    /**
     * 200：请求成功。
     * 204: 重复的操作
     * 401：权限验证不通过。
     * 403：无权访问的数据资源。
     * 404：未找到需要的资源。
     * 640：参数异常
     * 641: 非正式用户。
     * 642: 参数。
     * 500：未知异常。
     * 502: 网关或者业务异常一般是触发了业务规则
     */

    /**
     * 系统总体相关
     */
    SUCCESS(200, "操作成功"),
    REPAIR_ORDER_REPEAT(20001, "已经补缴"),
    UNKNOW_ERROR(-1, "网络异常请重试"),
    AUTH_FAIL(401, "权限验证不通过"),
    FORBIDDEN(403, "无权限"),
    FORBIDDEN_ALL(40030, "无权限使用本系统"),
    NONE(40040, "未找到需要的资源"),
    ORDER_INVALID(40041, "检测到系统环境发生变化，请刷新页面后重试。如果已经完成支付，您可以进入游戏中查看。"),
    PARAM_INVALID(40020, "参数错误"),
    PARAM_INVALID_GAME_CODE(40021, "游戏维护中，暂时无法充值"),
    PARAM_INVALID_GAME_CODE_NOT_SAME(40022, "游戏编码参数错误"),
    NEED_LOGIN(40010, "需要登录"),
    WHITELIST_TXT_EMPTY(70010, "文本内容为空"),
    WHITELIST_GAME_EMPTY(70011, "所属游戏不能为空"),
    WHITELIST_UID_EXCEED(70020, "不能超过1000个uid"),
    ORDER_ERROR(60010, "订单操作失败"),
    DUPLICATE_ORDER(60011, "重复的下单请求"),
    REPAY_ORDER_NEED_PAY(60012, "存在补缴信息，需要补缴"),
    REPAY_ORDER_NEED_RELEASE(60013, "您已交清补缴金额，请耐心等待或者联系客服"),

    NEED_PRIVILEGE(40011, "权限校验不通过"),
    NEED_REAL_NAME(40012, "未实名认证"),

    COMMODITY_ERROR(60020, "商品操作失败"),
    LOGIN_FIELD(40023, "用户名或者密码错误");


    @Setter
    @Getter
    private Integer code;

    @Setter
    @Getter
    private String message;

    ResponseEnum(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }
}
