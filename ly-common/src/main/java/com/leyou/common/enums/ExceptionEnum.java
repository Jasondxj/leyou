package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {
    PRINCE_CANNOT_BE_NULL(400,"价格不能为空"),
    CATEGORY_NOT_FOUND(404,"商品分类未找到"),
    BRAND_NOT_FOUND(404,"品牌未找到"),
    BRAND_SAVE_ERROR(500,"新增品牌失败"),
    FILE_UPLOAD_FAIL(500,"文件上传失败"),
    FILE_TYPE_ERROR(400,"文件类型不匹配"),
    SPEC_GROUP_NOT_FOUND(400,"商品规格组没查到"),
    SPEC_PARAM_NOT_FOUND(400,"商品规格参数没查到"),
    GROUP_SAVE_ERROR(500,"新增规格组失败" ),
    PARAM_SAVE_ERROR(500,"新增参数失败" ),
    GROUP_DELETE_ERROR(500,"规格组删除失败" ),
    GOODS_NOT_FOUND(404,"商品不存在" ),
    GOODS_DETAIL_NOT_FOUND(500,"商品不存在" ),
    GOODS_SKU_NOT_FOUND(500,"商品SKU不存在" ),
    GOODS_STOCK_NOT_FOUND(500,"商品库存不存在" ),
    GOOD_ADD_ERROR(500,"新增商品失败" ),
    GOOD_ID_ERROR(400,"商品ID不能为空" ),
    GOOD_UPDATE_ERROR(500,"新增修改失败" ),
    INVALID_USER_DATA_TYPE(400,"无效的参数类型"),
    INVALID_VERIFY_CODE(400,"验证码错误"),
    INVALID_USERNAME_PASSWORD(400,"无效的用户名和密码"),
    CREATE_TOKEN_ERROR(500,"用户凭证生成失败"),
    UNAUTHORIZED(403,"未授权"),
    CART_NOT_FOUND(404,"购物车为空"),
    CREATE_ORDER_ERROR(500,"创建订单失败"),
    STOCK_NOT_ENOUGH(500,"库存不足"),
    ORDER_NOT_FOUND(404,"订单未找到"),
    ORDER_DETAIL_NOT_FOUND(404,"订单详情未找到"),
    ORDER_STATUS_NOT_FOUND(404,"订单状态未找到"),
    WX_PAY_SIGN_INVALID(500,"微信签名无效"),
    WX_PAY_NOTIFY_PARAM_ERROR(500,"微信支付回调参数错误"),
    WX_PAY_ORDER_FAIL(500,"微信下单失败"),
    ORDER_STATUS_ERROR(400,"订单状态有误"),
    SIGN_ERROR(400,"签名错误"),
    ORDER_PARAM_ERROR(400,"订单参数有误"),
    UPDATE_ORDER_STATUS_ERROR(500,"更新订单状态失败");
    private int code;
    private String msg;
}
