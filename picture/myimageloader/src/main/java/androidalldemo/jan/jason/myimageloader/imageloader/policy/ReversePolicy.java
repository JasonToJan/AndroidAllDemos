package androidalldemo.jan.jason.myimageloader.imageloader.policy;


import androidalldemo.jan.jason.myimageloader.imageloader.request.BitmapRequest;

/**
 * Author:renzhenming
 * Time:2018/6/13 7:23
 * Description:This is SerialPolicy
 */
public class ReversePolicy implements LoadPolicy {

    @Override
    public int compareTo(BitmapRequest request1, BitmapRequest request2) {
        return request1.getSerialNum()-request2.getSerialNum();
    }
}
