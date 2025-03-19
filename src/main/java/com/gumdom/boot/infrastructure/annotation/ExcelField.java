package com.gumdom.boot.infrastructure.annotation;

import java.lang.annotation.*;

/**
 * Excel表单元格定义
 * 示例1: @ExcelField("顶级系统")
 * 示例2: @Excel(headRowAt = 1)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface ExcelField {

    /**
     * 字段名
     */
    String value() default "";

    /**
     * 所在行,从0开始
     *
     * @return
     */
    int row() default 0;


    /**
     * 所在列:从0开始
     */
    int column() default 0;

    /**
     * 是否合并单元格
     *
     * @return
     */
    boolean isMerged() default false;


}
