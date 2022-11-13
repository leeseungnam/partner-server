package kr.wrightbrothers.apps.common.annotation;

import kr.wrightbrothers.apps.common.type.ExcelBodyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelBody {
    int colIndex();
    ExcelBodyType bodyType() default ExcelBodyType.TEXT;
}
