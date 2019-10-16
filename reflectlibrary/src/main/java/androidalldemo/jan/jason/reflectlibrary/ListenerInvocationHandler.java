package androidalldemo.jan.jason.reflectlibrary;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2019/10/16 21:43
 */
public class ListenerInvocationHandler implements InvocationHandler {

    private Object target;//拦截的目标
    private HashMap<String,Method> map=new HashMap<>();

    public ListenerInvocationHandler(Object target) {
        this.target = target;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //将本应该回调的onClick或者onLongClick方法拦截，执行开发者定义的方法
        if(target!=null){
            String name = method.getName();

            //阻塞事件，1s N次点击只算1次

            method = map.get(name);//强行赋值,如果有拦截的

            if(method!=null){
                if(method.getGenericParameterTypes().length==0){
                    return method.invoke(target);
                }
                return method.invoke(target,args);
            }

        }

        return null;
    }

    public void add(String callBackListener,Method method){
        map.put(callBackListener,method);
    }
}
