package cn.liaozh.core.model;

import lombok.Data;

@Data
public class QRCodeParams {

    // 二维码扫码类型： 1 - 统一下单的聚合二维码
    public static final byte TYPE_PAY_ORDER = 1;
    public static final byte TYPE_QRC = 2;

    private String id;

    private Byte type;

}
