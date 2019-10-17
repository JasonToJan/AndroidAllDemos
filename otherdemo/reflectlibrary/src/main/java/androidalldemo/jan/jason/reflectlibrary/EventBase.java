package androidalldemo.jan.jason.reflectlibrary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2019/10/16 21:21
 */

@Target(ElementType.ANNOTATION_TYPE)//作用在另外一个注解上
@Retention(RetentionPolicy.RUNTIME)
public @interface EventBase {

    //监听方法
    String listenerSetter();

    //监听接口
    Class<?> listenerType();

    //观察到行为后，回调
    String callBackListener();

}
