//package com.coocent.visualizerlib.ui;
//
//import android.content.Context;
//import android.support.v7.app.AlertDialog;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.coocent.visualizerlib.R;
//import com.coocent.visualizerlib.utils.LogUtils;
//
//import java.util.LinkedList;
//import java.util.List;
//
///**
// * Created by MummyDing on 16-2-5.
// * GitHub: https://github.com/MummyDing
// * Blog: http://blog.csdn.net/mummyding
// */
//public class ColorPickerDialog implements View.OnClickListener{
//
//
//    private int rowCount;
//
//    private int colCount;
//
//    private int buttonCount;
//    /**
//    * unit: dip
//    */
//    private int defaultPadding  = 20;
//    /**
//     * set of ColorButton's color
//     */
//    private int [] mColors;
//
//    /**
//     * set of ColorButton
//     */
//    private List<ColorButton> colorButtonList = new LinkedList<>();
//
//    /**
//     * current checked ColorButton
//     */
//    private ColorButton currentButton;
//
//    private AlertDialog dialog;
//    /**
//     * Dialog Title,default "Theme"
//     */
//    private String title = "Theme";
//
//    /**
//     * if you want the dialog dismissed after picking,set it to true,otherwise false.
//     * default true
//     */
//    private Boolean isDismissAfterClick = true;
//
//    private LinearLayout rootLayout;
//
//    private Context mContext;
//
//    private OnColorChangedListener listener;
//
//    private RelativeLayout bottom;//底部按钮
//
//    private TextView cancelTv;
//
//    private TextView okTv;
//
//
//    public ColorPickerDialog(Context context ,int[] colors) {
//        this(context,colors,colors[0]);
//    }
//
//    public ColorPickerDialog(Context context,  int[] colors, int checkedColor) {
//        if(context == null || colors == null || colors.length ==0 ){
//            throw new IllegalArgumentException("args must not be null");
//        }
//        this.mContext = context;
//        this.mColors = colors;
//        buttonCount = colors.length;
//        defaultPadding = DisplayUtil.dip2px(mContext,defaultPadding);
//        setCheckedColor(checkedColor);
//
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view=inflater.inflate(R.layout.dialog_color_pick_visualizer, null);
//        bottom = (RelativeLayout) view.findViewById(R.id.dcpv_color_picker_rl);
//        okTv=view.findViewById(R.id.dcpv_positiveButton);
//
//    }
//
//
//    public ColorPickerDialog build(){
//        return build(4);
//    }
//    public ColorPickerDialog build(int widthCount){
//        if(widthCount <=2 && widthCount<=6){
//            throw new IllegalArgumentException("widthCount must between 3 and 6 !!!");
//        }
//        colCount = widthCount;
//
//        rowCount = (buttonCount -1)/widthCount + 1;
//
//        /**
//         * create root Layout
//         */
//        rootLayout = new LinearLayout(mContext);
//        rootLayout.setOrientation(LinearLayout.VERTICAL);
//
//        /**
//         * create layout of each row & init it
//         */
//        LinearLayout [] linearLayouts = new LinearLayout[rowCount];
//
//        for(int i= 0 ; i<rowCount ; i++){
//            linearLayouts[i] = new LinearLayout(mContext);
//            linearLayouts[i].setOrientation(LinearLayout.HORIZONTAL);
//            linearLayouts[i].setPadding(defaultPadding,defaultPadding/2,0,defaultPadding/2);
//            rootLayout.addView(linearLayouts[i]);
//        }
//
//        //添加两个按钮
//        if(bottom!=null){
//            rootLayout.addView(bottom);
//        }
//
//        /**
//         * add colorButtons to layout
//         */
//        for(int i=0 ; i<buttonCount ; i++){
//            ColorButton colorButton = new ColorButton(mContext,mColors[i]);
//            colorButton.setOnClickListener(this);
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            lp.setMargins(0,0,defaultPadding,0);
//            linearLayouts[i/widthCount].addView(colorButton,lp);
//            colorButtonList.add(colorButton);
//        }
//
//        return this;
//    }
//
//
//    public ColorPickerDialog show(){
//        if(rootLayout == null){
//            throw new UnsupportedOperationException("you must call build before show");
//        }
//
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setTitle(getTitle());
//        builder.setIcon(R.drawable.ic_dialog_color_pick);
//
//        builder.setView(rootLayout);
//
////        builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
////                if(listener!=null){
////                    listener.onClickOk();
////                }
////            }
////        });
////
////        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
////
////            }
////        });
//
//        dialog = builder.create();
//        dialog.show();
//
//        dialog.getWindow().setLayout((DisplayUtil.dip2px(mContext,30)+defaultPadding)*colCount+defaultPadding*3,DisplayUtil.dip2px(mContext,300+30*rowCount)+defaultPadding*rowCount);
//
//        return this;
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        if(listener != null){
//            ColorButton colorButton = (ColorButton) v;
//            if(colorButton.isChecked() == false){
//                if(currentButton != null){
//                    currentButton.setChecked(false);
//                }
//                colorButton.setChecked(true);
//                listener.onColorChanged(colorButton.getmColor());
//                currentButton = colorButton;
//                if(isDismissAfterClick && dialog != null){
//                    dialog.dismiss();
//                }
//            }
//        }
//    }
//    public ColorPickerDialog setCheckedColor(int color){
//        LogUtils.d("自己的："+color);
//        if(currentButton != null && color == currentButton.getmColor()){
//            return this;
//        }
//        for(ColorButton colorButton: colorButtonList){
//            LogUtils.d("当前颜色值为："+colorButton.getmColor()+" 自己的："+color);
//            if(color == colorButton.getmColor()){
//                if(currentButton != null){
//                    currentButton.setChecked(false);
//                }
//                colorButton.setChecked(true);
//                listener.onColorChanged(colorButton.getmColor());
//                currentButton = colorButton;
//            }
//        }
//        return this;
//    }
//
//    public ColorPickerDialog setOnColorChangedListener(OnColorChangedListener listener) {
//        this.listener = listener;
//        return this;
//    }
//
//    public ColorPickerDialog setTitle(String title) {
//        this.title = title;
//        return this;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public Boolean getDismissAfterClick() {
//        return isDismissAfterClick;
//    }
//
//    public ColorPickerDialog setDismissAfterClick(Boolean dismissAfterClick) {
//        isDismissAfterClick = dismissAfterClick;
//        return this;
//    }
//}
