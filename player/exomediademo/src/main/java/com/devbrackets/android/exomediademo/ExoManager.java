package com.devbrackets.android.exomediademo;


import android.app.Application;
import android.support.annotation.Nullable;

import com.devbrackets.android.exomediademo.manager.PlaylistManager;

/**
 * Description: 管理频谱类，全局单例实现
 * *
 * Creator: Wang
 * Date: 2019/6/3 21:50
 */
public class ExoManager {

    private Application application;

    static ExoManager instance=null;

    /**
     * 播放列表管理类，管理视频，音频，全局的一个实例，在App中初始化，确保只初始化一次
     */
    @Nullable
    private  PlaylistManager playlistManager;

    private void ExoManager(){}

    public static ExoManager getInstance(){
        return SingletonTool.instance;
    }

    /****静态内部类****/
    private static class SingletonTool{
        private static final ExoManager instance=new ExoManager();
    }

    public void init(Application application){
        this.application=application;
        playlistManager = new PlaylistManager(application);
    }

    public Application getApplication(){
        if(application==null) {

        }
        return application;
    }

    @Nullable
    public PlaylistManager getPlaylistManager() {
        return playlistManager;
    }
}
