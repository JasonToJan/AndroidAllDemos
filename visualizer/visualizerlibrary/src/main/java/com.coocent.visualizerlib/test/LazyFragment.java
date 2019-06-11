package com.coocent.visualizerlib.test;

import android.support.v4.app.Fragment;

/**
 * Fragment懒加载
 */
public abstract class LazyFragment extends Fragment {

    protected boolean isVisible;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {//frahment从不可见到完全可见的时候，会调用该方法
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()){
            isVisible = true;
            onVisible();
        }else {
            isVisible = false;
            onInvisible();
        }
    }

    protected abstract void lazyLoad();//懒加载的方法,在这个方法里面我们为Fragment的各个组件去添加数据

    protected void onVisible(){
        lazyLoad();
    }

    protected void onInvisible(){

    }

}