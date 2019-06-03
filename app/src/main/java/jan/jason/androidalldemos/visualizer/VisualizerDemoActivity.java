package jan.jason.androidalldemos.visualizer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

import jan.jason.androidalldemos.R;
import jan.jason.androidalldemos.databinding.ActivityVisualizerDemoBinding;
import jan.jason.androidalldemos.utils.LogUtils;

public class VisualizerDemoActivity extends AppCompatActivity {

    private ActivityVisualizerDemoBinding mainBinding;
    private RecyclerView songListRecyclerView;

    //用于存放音乐信息
    private ArrayList<Song> songList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding=DataBindingUtil.setContentView(this,R.layout.activity_visualizer_demo);

        initSongs();
        initRecyclerView();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    LogUtils.d("测试", "onRequestPermissionsResult: 成功授权");
                else {
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void initSongs(){
        //运行时权限的获取
        if(ContextCompat.checkSelfPermission(VisualizerDemoActivity.
                this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(VisualizerDemoActivity.this,new
                    String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            return;
        }

        //读取本地音乐
        Cursor mAudioCursor = this.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Audio.AudioColumns.TITLE
        );
        for (int i = 0; i < mAudioCursor.getCount(); i++) {
            mAudioCursor.moveToNext();
            //歌名
            int indexTitle = mAudioCursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE);
            //歌唱者
            int indexARTIST = mAudioCursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST);
            //音乐图片的id
            int indexALBUM = mAudioCursor
                    .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

            int indexPath = mAudioCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            int indexDuration=mAudioCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            String strTitle = mAudioCursor.getString(indexTitle);
            String strARTIST = mAudioCursor.getString(indexARTIST);
            String strALBUM = mAudioCursor.getString(indexALBUM);
            String path = mAudioCursor.getString(indexPath);
            long duration= mAudioCursor.getLong(indexDuration);
            int pic = Integer.valueOf(strALBUM);

            //根据音乐图片的id获得图片
            Bitmap bitmap = getAlbumArt(pic);

            Song song = new Song(strTitle,strARTIST,path,duration,bitmap);
            songList.add(song);
        }
        mAudioCursor.close();

        ApplicationData.getInstance().setAllSongList(songList);
    }

    private void initRecyclerView(){
        songListRecyclerView=mainBinding.avdRecyclerView;
        songListRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        songListRecyclerView.setLayoutManager(layoutManager);
        SongAdapter adapter = new SongAdapter(this,songList);
        songListRecyclerView.setAdapter(adapter);
    }

    private Bitmap getAlbumArt(int album_id) {
        //前面我们只是获取了专辑图片id，在这里实现通过id获取掉专辑图片，albums存储的是专辑的信息
        String mUriAlbums = "content://media/external/audio/albums";

        //album_art字段存储的是音乐图片的路径
        String[] projection = new String[]{"album_art"};
        Cursor cur = getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);

        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
            LogUtils.d("liukun", "getAlbumArt: "+album_art);
        }
        cur.close();
        Bitmap bm = null;
        if (album_art != null) {
            bm = BitmapFactory.decodeFile(album_art);
        } else {
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
        }
        return bm;
    }
}
