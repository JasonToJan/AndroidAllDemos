package jan.jason.ndkdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Date;


public class NdkDemoActivity extends AppCompatActivity implements View.OnClickListener,
        JniArraryOperation.JniPrinterInterface {

    static {
        System.loadLibrary("native-lib");
    }

    public native String stringFromJNI();
    public native void accessField();
    public native void accessStaticField();
    public native String accessMethod();
    public native String accessStaticMethod();
    public native Date accessConstructor();

    /**
     * 计时是否已经运行
     */
    private boolean running;
    private int times=0;
    public String showText="Hello World!";
    public static String staticString="I am a static string.";

    private TextView txtNdk;
    private TextView resultTxt;
    private Button btnChange;
    private Button btnChangeStatic;
    private TextView staticText;
    private Button btnExecuteNormalFun;
    private Button btnExecuteStaticFun;
    private Button btnExecuteConstructFun;
    private Button btnExecuteArrayFun;
    private Button btnTestEncryptor;
    private TextView txtArraryBefore;
    private TextView txtArraryAfter;
    private Button btnExecuteSplitFile;
    private Button btnExecuteListFiles;
    private Button btnExecuteBitmap;
    private Button btnExecuteThreadCallback;

    private JniArraryOperation jniArraryOperation;

    /**
     * jni回调用java计时的实例
     */
    private JniCallbackDemo jniCallbackDemo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ndkdemo_ndk);

        initView();
        initData();
    }

    @Override
    public void onClick(View v) {

       if(v==btnChange){
           if(showText.equals("Hello World!")){
               accessField();
               resultTxt.setText(showText);
           }else{
               showText="Hello World!";
               resultTxt.setText(showText);
           }
       }else if(v==btnChangeStatic){
           if(staticString.equals("I am a static string.")){
               accessStaticField();
               staticText.setText(staticString);
           }else{
               staticString="I am a static string.";
               staticText.setText(staticString);
           }
       }else if(v==btnExecuteNormalFun){
           btnExecuteNormalFun.setText(times++%2==0?accessMethod():"Java中的字符串");
       }else if(v==btnExecuteStaticFun){
           btnExecuteStaticFun.setText(times++%2==0?accessStaticMethod():"Java中的字符串");
       }else if(v==btnExecuteConstructFun){
           btnExecuteConstructFun.setText(times++%2==0?accessConstructor().toString():"JNI调用构造方法返回Date对象为：");
       }else if(v==btnExecuteArrayFun){
           if(times++%2==0){
               jniArraryOperation.test(this);
           }else{
               btnExecuteArrayFun.setText("JNI 生成随机数组然后排序：");
               txtArraryAfter.setText("After");
               txtArraryBefore.setText("Before");
           }
       }else if(v==btnTestEncryptor){
           testEncryptor();
       }else if(v==btnExecuteSplitFile){
           testSplitFile();
       }else if(v==btnExecuteListFiles){
           testListFiles();
       }else if(v==btnExecuteBitmap){
           testBitmap();
       }else if(v==btnExecuteThreadCallback){
           testJniCallback();
       }
    }

    @Override
    public void printRandomArray(int[] arr) {
        txtArraryBefore.setText(Arrays.toString(arr));
    }

    @Override
    public void printResultArray(int[] arr) {
        txtArraryAfter.setText(Arrays.toString(arr));
    }

    private void initView(){
        txtNdk=(TextView) findViewById(R.id.amn_demo_txt1);
        btnChange=(Button) findViewById(R.id.amn_demo_btn1);
        resultTxt=(TextView) findViewById(R.id.amn_demo_txt2);
        staticText=(TextView)findViewById(R.id.amn_demo_static_txt);
        btnChangeStatic=(Button) findViewById(R.id.amn_demo_static_btn);
        btnExecuteNormalFun=(Button) findViewById(R.id.amn_demo_normalfun_btn);
        btnExecuteStaticFun=(Button) findViewById(R.id.amn_demo_staticfun_btn);
        btnExecuteConstructFun=(Button) findViewById(R.id.amn_demo_constructfun_btn);
        btnExecuteArrayFun=(Button) findViewById(R.id.amn_demo_array_btn);
        txtArraryBefore=(TextView) findViewById(R.id.amn_demo_array_before_txt);
        txtArraryAfter=(TextView) findViewById(R.id.amn_demo_array_after_txt);
        btnTestEncryptor=(Button) findViewById(R.id.amn_demo_test_encryptor_btn);
        btnExecuteSplitFile=(Button) findViewById(R.id.amn_demo_test_splitfile_btn);
        btnExecuteListFiles=(Button) findViewById(R.id.amn_demo_test_listall_btn);
        btnExecuteBitmap=(Button) findViewById(R.id.amn_demo_test_bitmap_btn);
        btnExecuteThreadCallback=(Button) findViewById(R.id.amn_demo_test_thread_btn);
    }

    private void initData(){
        String text=stringFromJNI();
        txtNdk.setText(text);

        btnChange.setOnClickListener(this);
        btnChangeStatic.setOnClickListener(this);
        btnExecuteNormalFun.setOnClickListener(this);
        btnExecuteStaticFun.setOnClickListener(this);
        btnExecuteConstructFun.setOnClickListener(this);
        btnExecuteArrayFun.setOnClickListener(this);
        btnTestEncryptor.setOnClickListener(this);
        btnExecuteSplitFile.setOnClickListener(this);
        btnExecuteListFiles.setOnClickListener(this);
        btnExecuteBitmap.setOnClickListener(this);
        btnExecuteThreadCallback.setOnClickListener(this);

        jniArraryOperation=new JniArraryOperation();
    }

    /**
     * 普通函数，供NDK调用
     * @param str
     * @return
     */
    public String normalFunction(String str){
        return "i am a normal function "+str;
    }

    /**
     * 静态函数，供NDK调用
     * @return
     */
    public static String staticFunction(String str){
        return "i am a static function ，"+str;
    }

    /**
     * 测试JNI加密
     */
    private void testEncryptor(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x1024);
                return;
            }
        }
        String encryptPath = new Encryptor().test();
        Toast.makeText(this, "加密文件地址：" + encryptPath, Toast.LENGTH_SHORT).show();
    }

    /**
     * 测试拆分文件
     */
    private void testSplitFile(){
        if (hasFilePermission()) {
            new JniFileOperation().test();
            Toast.makeText(this, "任务完成，测试文件路径:" + Config.getBaseUrl(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 列出所有文件，日志中可见
     */
    private void testListFiles(){
        if (hasFilePermission()) {
            new JniFileOperation().doListDirAllFile();
            Toast.makeText(this, "请查看日志，测试文件路径:" + Config.getBaseUrl()+"/logs", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 测试获取图片信息
     */
    private void testBitmap(){
        new JniBitmapDemo().test();
    }

    /**
     * 测试jni开线程调用java
     */
    private void testJniCallback() {
        jniCallbackRun(times++%2==0);
    }

    /**
     * 测试jni调用java
     */
    private void jniCallbackRun(boolean run) {

        if (jniCallbackDemo == null) {
            jniCallbackDemo = new JniCallbackDemo(this);
        }
        if (run) {
            Toast.makeText(this, "开始计时，请查看控制台日志输出", Toast.LENGTH_SHORT).show();
            jniCallbackDemo.startTiming();
        } else {
            Toast.makeText(this, "停止计时", Toast.LENGTH_SHORT).show();
            jniCallbackDemo.stopTiming();
        }
    }

    /**
     * 申请权限
     */
    private boolean hasFilePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x1024);
                return false;
            }
        }
        return true;
    }
}
