package com.coocent.visualizerlib;


import android.app.Application;

/**
 * Description: 管理应用
 * *
 * Creator: Wang
 * Date: 2019/6/3 21:50
 */
public class ApplicationProxy {

    static ApplicationProxy instance=null;

    public Application application;

    private void ApplicationProxy(){}

    public static ApplicationProxy getInstance(){
        return SingletonTool.instance;
    }

    /****静态内部类****/
    private static class SingletonTool{
        private static final ApplicationProxy instance=new ApplicationProxy();
    }

    public void init(Application application){
        this.application=application;
    }

    public Application getApplication(){
        if(application==null) {
            LogUtils.d("糟了，这里的Application为空了！！！");
        }
        return application;
    }

}
