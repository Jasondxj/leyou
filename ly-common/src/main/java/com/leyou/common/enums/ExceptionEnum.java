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
    GOODS_NOT_FOUND(404,"商品不存在" );
    private int code;
    private String msg;
}
