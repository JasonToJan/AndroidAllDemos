package jan.jason.ndkdemo;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

public class Config {

    public static final String BASE_URL = Environment.getExternalStorageDirectory().getAbsolutePath();

    private static String DIR_NAME = "NDKDemo";//文件夹名字

    private static String DIR_PATH;//操作目录+"/"

    /**
     * 获取文件存储目录,如果为空才创建，创建文件夹
     */
    public static String getBaseUrl() {
        if (!TextUtils.isEmpty(DIR_PATH)) {
            return DIR_PATH;
        }
        File file = new File(BASE_URL + File.separator + DIR_NAME);
        if (!file.exists()) {
            file.mkdir();//创建文件夹
        }
        DIR_PATH = file.getAbsolutePath() + File.separator;
        return DIR_PATH;
    }
}