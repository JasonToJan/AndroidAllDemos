package androidalldemo.jan.jason.reflectlibrary;

import android.app.Activity;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2019/10/16 20:37
 */
public class InjectManager {

    public static void inject(Activity activity){

        injectLayout(activity);

        injectViews(activity);

        injectEvents(activity);
    }

    private static void injectLayout(Activity activity) {

        Class<? extends  Activity> claszz=activity.getClass();
        ContentView contentView = claszz.getAnnotation(ContentView.class);
        if(contentView!=null){
            int layoutId=contentView.value();
            try {
                final Method setContentView = claszz.getMethod("setContentView",
                        int.class);
                setContentView.invoke(activity,layoutId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void injectViews(Activity activity) {

        Class<? extends Activity> clazz=activity.getClass();

        Field[] declaredFields = clazz.getDeclaredFields();
        //遍历所有方法
        for (Field declaredField : declaredFields) {

            InjectView annotation = declaredField.getAnnotation(InjectView.class);
            if(annotation!=null){
                int viewId=annotation.value();
                try {
                    Method findViewById = clazz.getMethod("findViewById", int.class);

                    //返回值
                    Object view = findViewById.invoke(activity, viewId);

                    //私有属性赋值不了
                    declaredField.setAccessible(true);
                    declaredField.set(activity,view);//属性赋值

                }catch (Throwable e){

                }
            }

        }


    }

    private static void injectEvents(Activity activity) {
        Class<? extends Activity> clazz=activity.getClass();

        Method[] methods = clazz.getDeclaredMethods();

        //Activity中所有方法
        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();

            //当前方法所有注解
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                if(annotationType!=null){

                    //获取第二层注解，这个bean是对Android 长按，短按的一个封装
                    EventBase eventBase=annotationType.getAnnotation(EventBase.class);
                    //点击，长按，条目点击的封装注解，Android的长按或者短按
                    if(eventBase!=null){

                        String listenerSetter = eventBase.listenerSetter();
                        Class<?> listenerType = eventBase.listenerType();
                        String callBackListener = eventBase.callBackListener();//android定义好的，比如短按是onClick 长按是onLongClick



                        //通过AnnotationType获取onClick注解的value值，添加了注解的 布局id组
                        try {
                            Method valueMethod = annotationType.getDeclaredMethod("value");//通过注解获取方法
                            int[] viewIds=(int[])valueMethod.invoke(annotation);//获取注解中的id组

                            ListenerInvocationHandler listenerInvocationHandler = new ListenerInvocationHandler(activity);
                            listenerInvocationHandler.add(callBackListener,method);//method为用户自己写的方法

                            //需要一个代理 weisheme 不用onClick.value()
                            Object listener = Proxy.newProxyInstance(listenerType.getClassLoader(),
                                    new Class[]{listenerType}
                                    , listenerInvocationHandler
                            );

                            for (int viewId : viewIds) {
                                View view=activity.findViewById(viewId);
                                //找方法
                                Method setxxx = view.getClass().getMethod(listenerSetter, listenerType);
                                setxxx.invoke(view,listener);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }
}
