package jan.jason.androidalldemos.visualizer;

import java.util.ArrayList;

/**
 * Description: 全局数据类
 * *
 * Creator: Wang
 * Date: 2019/6/2 21:38
 */
public class ApplicationData{

    static ApplicationData instance=null;

    private static ArrayList<Song> allSongList;

    private void ApplicationData(){}

    public static ApplicationData getInstance(){

        return SingletonTool.instance;

    }
    /****静态内部类****/
    private static class SingletonTool{
        private static final ApplicationData instance=new ApplicationData();
    }

    public void setAllSongList(ArrayList<Song> allSongList){
        this.allSongList=allSongList;
    }

    public ArrayList<Song> getAllSongList(){
        return allSongList;
    }

}
