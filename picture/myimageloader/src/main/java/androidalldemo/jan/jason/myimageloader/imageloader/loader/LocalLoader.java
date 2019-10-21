package androidalldemo.jan.jason.myimageloader.imageloader.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;



import java.io.File;

import androidalldemo.jan.jason.myimageloader.imageloader.request.BitmapRequest;
import androidalldemo.jan.jason.myimageloader.imageloader.utils.BitmapDecoder;
import androidalldemo.jan.jason.myimageloader.imageloader.utils.ImageViewHelper;

/**
 * Author:renzhenming
 * Time:2018/6/13 7:22
 * Description:This is LocalLoader
 * 本地图片加载器
 */
public class LocalLoader extends AbstractLoader {

    @Override
    public Bitmap onLoad(BitmapRequest request) {
        //得到本地图片的路径
        final String path = Uri.parse(request.getImageUrl()).getPath();
        File file = new File(path);
        if (!file.exists() || !file.isFile()){
            return null;
        }
        BitmapDecoder decoder = new BitmapDecoder() {
            @Override
            public Bitmap decodeBitmapWithOptions(BitmapFactory.Options options) {
                return BitmapFactory.decodeFile(path,options);
            }
        };
        return decoder.decodeBitmap(ImageViewHelper.getImageViewWidth(request.getImageView()),
                ImageViewHelper.getImageViewHeight(request.getImageView()));
    }
}
