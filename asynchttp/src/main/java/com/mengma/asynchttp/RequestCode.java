package com.mengma.asynchttp;

/**
 * http请求编号，由于像登录这种接口，可能会在不同的地方调用，
 * 因此需要设置独特的编号，已防止与其他编号冲突,独特编号从100开始
 * Create by szhua 2016/3/11
 */
public class RequestCode {
    public final static int CODE_0 = 0;
    public final static int CODE_1 = 1;
    public final static int CODE_2 = 2;
    public final static int CODE_3 = 3;
    public final static int CODE_4 = 4;
    public final static int CODE_5 =5 ;

    public final static int UPLOADFILE =101 ;
    public final static int UPLOADFILE1 =102 ;
    public final static int INITSUCESS =103 ;

    public final static int CODE_LOGIN = 100;  //登录
    public final static int CODE_CREATE_ORDER_DINGGOU = 101;
    public final static int CODE_CREATE_ORDER_DINGZHI = 102;
    public final static int CODE_GET_PHONE_CODE = 103;
    public final static int CODE_GET_TOKEN = 104;
    public final static int CODE_WEIXIN_PAY = 105;
    public final static int CODE_RONG_USER = 106;
    public final static int CODE_ZHIFUBAO_PAY = 107;
    public final static int CODE_UNION_PAY = 108;
    public final static int GET_BANNER =109 ;

    public final static int GET_LOGISTICS = 119;

}