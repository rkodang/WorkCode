package com.gumdom.boot.infrastructure.annotation;

import java.lang.annotation.*;

/**
 * Excel表实体类定义
 * 示例1: @Excel("sheet1")
 * 示例2: @Excel(headRowAt = 1)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface Excel {

    /**
     * sheet 工作表名
     */
    String value() default "";

    /**
     * 表头 所在行,从0开始
     *
     * @return
     */
    int headRowAt() default 0;

    /**
     * 跳过全空行
     *
     * @return
     */
    boolean skipAllEmpty() default true;

    /**
     * 遇到全空行_退出
     *
     * @return
     */
    boolean breakAllEmpty() default true;

    /**
     * 数据起始行
     * 第一行表头,第二行为数据则为 0,Excel 处理器会 +1
     * 第一二行都是表头,第三行为数据则为 1,Excel 处理器会 +1
     */
    int dataStartRow() default 0;

    /**
     * 扫描首个父类字段
     *
     * @return
     */
    boolean scanFirstSuperFields() default false;

}
