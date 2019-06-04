package jan.jason.androidalldemos.visualizer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.coocent.visualizerlib.ActivityVisualizer;
import com.coocent.visualizerlib.VisualizerManager;
import com.coocent.visualizerlib.IVisualizer;
import com.coocent.visualizerlib.MusicVisualizerInter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.carlosrafaelgn.fplay.visualizer.OpenGLVisualizerJni;
import br.com.carlosrafaelgn.fplay.visualizer.SimpleVisualizerJni;
import jan.jason.androidalldemos.R;
import jan.jason.androidalldemos.utils.BlurUtil;
import jan.jason.androidalldemos.utils.LogUtils;
import jan.jason.androidalldemos.utils.StatusBarUtil;


public class VisualizerMusicDetailActivity extends AppCompatActivity implements View.OnClickListener,MusicVisualizerInter {

    private TextView titleTV;//歌名
    private TextView autherTV;//歌手
    private ImageView imageView;//背景图
    private TextView timeTV;//总时长
    private ImageView prveBtn;//上一首歌
    private ImageView nextBtn;//下一首
    private Button visualizerBtn;//频谱
    private int position;
    private ImageView discBtn;
    private ImageView needleBtn;
    private MediaPlayer mediaPlayer;//定义MediaPlayer
    private ImageView pressBtn;
    private ImageView putBtn;
    private SeekBar seekBar;//进度条
    private Thread thread;//线程
    private boolean isStop;//线程标志位
    private TextView currentime;//当前时间
    private long totalTime;
    private ArrayList<Song> allSongList;

    private int visulaizerType=1;

    //运用Handler中的handleMessage方法接收子线程传递的信息
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            // 将SeekBar位置设置到当前播放位置
            seekBar.setProgress(msg.what);
            //获得音乐的当前播放时间
            currentime.setText(formatime(msg.what));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTranslucent(this);

        setContentView(R.layout.activity_visualizer_music_detail);

        bindID();

        prveBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        pressBtn.setOnClickListener(this);
        putBtn.setOnClickListener(this);
        visualizerBtn.setOnClickListener(this);

        allSongList=ApplicationData.getInstance().getAllSongList();
        if(allSongList==null||allSongList.size()==0) finish();

        final Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);


        mediaPlayer = new MediaPlayer();

        play();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //参数 ： fromUser即b 是用来标识是否来自用户的手动操作  true 用户动过手动方式更改的进度条
                if (b){

                    //seekto方法是异步方法
                    //seekto方法的参数是毫秒，而不是秒
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        VisualizerManager.getInstance().setMusicVisualizerInter(this);

    }


    private void play() {
        isStop = false;
        //Common是自己定义的一个工具类，里面获得的是本地音乐的信息
        Song music = ApplicationData.getInstance().getAllSongList().get(position);
        titleTV.setText(music.getTitle());
        autherTV.setText(music.getArtist());
        pressBtn.setImageResource(R.mipmap.music_play_white_48);
        timeTV.setText(formatime(music.getDuration()));
        //获得音乐时长
        totalTime=music.getDuration();
        //将专辑图片放入黑圈中
        Bitmap discBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.music_page_bg);
        if (music.getArtwork() != null) {

            //       imageView.setImageBitmap(music.albumbtm);
            Bitmap bgbm = BlurUtil.doBlur(music.getArtwork(), 15, 10);
            imageView.setImageBitmap(bgbm);
            Bitmap albumdiscBit = BlurUtil.mergeThumbnailBitmap(discBitmap, music.getArtwork());
            discBtn.setImageBitmap(albumdiscBit);

        } else {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.music_page_cover_1);
            imageView.setImageBitmap(BlurUtil.doBlur(bm, 10, 5));
            Bitmap albumdiscBit = BlurUtil.mergeThumbnailBitmap(discBitmap, bm);
            discBtn.setImageBitmap(albumdiscBit);
        }
        //重置，当切换音乐时不会放前一首歌的歌曲
        mediaPlayer.reset();
        try {
            // 设置音乐播放源
            mediaPlayer.setDataSource(music.getPath());
            // 准备
            mediaPlayer.prepare();
            // 启动
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        // 设置seekbar的最大值
        seekBar.setMax(mediaPlayer.getDuration());
        // 创建一个线程
        thread = new Thread(new MuiscThread());
        // 启动线程
        thread.start();


    }
    //时间转换类，将得到的音乐时间毫秒转换为时分秒格式
    private String formatime(long lengrh) {
        Date date = new Date(lengrh);
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String totalTime = sdf.format(date);
        return totalTime;
    }
    //绑定id的类
    private void bindID() {
        titleTV = findViewById(R.id.music_title);
        autherTV = findViewById(R.id.music_auther);
        imageView = findViewById(R.id.music_img);
        timeTV = findViewById(R.id.music_time);
        prveBtn = findViewById(R.id.music_prevbtn);
        nextBtn = findViewById(R.id.music_nextbtn);
        discBtn = findViewById(R.id.music_disc);
        needleBtn=findViewById(R.id.music_needle);
        pressBtn = findViewById(R.id.music_pressbtn);
        putBtn = findViewById(R.id.music_putdown);
        currentime=findViewById(R.id.music_firsttime);
        seekBar = findViewById(R.id.playSeekBar);
        visualizerBtn= findViewById(R.id.music_visualizer_btn);
    }

    //点击事件
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.music_prevbtn://点击上一首歌曲
                position--;
                if (position == -1) {
                    position =allSongList.size() - 1;
                }
                play();
                break;
            case R.id.music_nextbtn://点击下一首歌曲
                position++;
                if (position ==allSongList.size()) {
                    position = 0;
                }
                play();
                break;
            case R.id.music_pressbtn:
                //用if语句判断音乐播放的的状态
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    pressBtn.setImageResource(R.mipmap.music_pause_white_48);
                } else {
                    mediaPlayer.start();
                    //点击下一首时会继续出现暂停的按钮
                    pressBtn.setImageResource(R.mipmap.music_play_white_48);

                }
                break;
            case R.id.music_putdown:
                this.finish();
                break;

            case R.id.music_visualizer_btn:
                //ToastUtils.show("点击频谱，即将跳转到频谱库中~");
                keepToVisualizer();
                break;

            default:
                break;

        }
    }

    //销毁时所做的工作
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.reset();
    }

    public void keepToVisualizer(){
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 101);
                LogUtils.d("这里开始申请权限了");
                return;
            }
        }

        VisualizerManager.getInstance().setSessionId(mediaPlayer.getAudioSessionId());

        switch(visulaizerType){
            case 1:
                //简单频谱
                visulaizerType++;
                startActivity((new Intent(this, ActivityVisualizer.class)).
                putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, SimpleVisualizerJni.class.getName()));
                break;

            case 2:
                //OpenGL类型频谱
                visulaizerType++;
                startActivity((new Intent(this, ActivityVisualizer.class)).
                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName()));
                break;

            case 3:
                //上下部分
                visulaizerType++;
                startActivity((new Intent(this, ActivityVisualizer.class)).
                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName())
                        .putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_SPECTRUM2));
                break;

            case 4:
                //液体
                visulaizerType++;
                startActivity((new Intent(this, ActivityVisualizer.class)).
                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName())
                        .putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_LIQUID));
                break;

            case 5:
                //液体升级版
                visulaizerType++;
                startActivity((new Intent(this, ActivityVisualizer.class)).
                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName())
                        .putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_LIQUID_POWER_SAVER));
                break;

            case 6:
                //多彩版
                visulaizerType++;
                startActivity((new Intent(this, ActivityVisualizer.class)).
                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName())
                        .putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_COLOR_WAVES));
                break;
            case 7:
                //spin版
                visulaizerType++;
                startActivity((new Intent(this, ActivityVisualizer.class)).
                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName())
                .putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_SPIN));
                break;
            case 8:
                //Particle版
                visulaizerType++;
                startActivity((new Intent(this, ActivityVisualizer.class)).
                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName())
                .putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_PARTICLE));
                break;
            case 9:
                //沉浸式Particle版
                visulaizerType++;
                startActivity((new Intent(this, ActivityVisualizer.class)).
                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName())
                .putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_IMMERSIVE_PARTICLE));
                break;
            case 10:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 102);
                        return;
                    }
                }
                //沉浸式Particle_VR版
                visulaizerType=1;
                startActivity((new Intent(this, ActivityVisualizer.class)).
                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName())
                        .putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_IMMERSIVE_PARTICLE_VR));
                break;

        }

    }

    @Override
    public void vi_next() {
        position++;
        if (position ==allSongList.size()) {
            position = 0;
        }
        play();
    }

    @Override
    public void vi_prev() {
        position--;
        if (position == -1) {
            position =allSongList.size() - 1;
        }
        play();
    }

    @Override
    public boolean vi_isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public void vi_playorpause() {
        //用if语句判断音乐播放的的状态
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            pressBtn.setImageResource(R.mipmap.music_pause_white_48);
        } else {
            mediaPlayer.start();
            //点击下一首时会继续出现暂停的按钮
            pressBtn.setImageResource(R.mipmap.music_play_white_48);

        }
    }

    @Override
    public int vi_getSessionId() {
        return mediaPlayer.getAudioSessionId();
    }

    @Override
    public String vi_artist() {
        return  ApplicationData.getInstance().getAllSongList().get(position).getArtist();
    }

    @Override
    public String vi_title() {
        return ApplicationData.getInstance().getAllSongList().get(position).getTitle();
    }

    @Override
    public String vi_metachange() {
        return null;
    }

    @Override
    public String vi_playstatechange() {
        return null;
    }


    //建立一个子线程实现Runnable接口
    class MuiscThread implements Runnable {

        @Override
        //实现run方法
        public void run() {
            //判断音乐的状态，在不停止与不暂停的情况下向总线程发出信息
            while (mediaPlayer != null && isStop == false) {

                try {
                    // 每100毫秒更新一次位置
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //发出的信息
                handler.sendEmptyMessage(mediaPlayer.getCurrentPosition());
            }

        }

    }
}
