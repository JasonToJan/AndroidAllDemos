package androidalldemo.jan.jason.reflectlibrary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2019/10/16 20:43
 */

@Target(ElementType.METHOD)//作用在属性上
@Retention(RetentionPolicy.RUNTIME)//运行时通过反射技术
public @interface OnLongClick {

    int[] value();
}
