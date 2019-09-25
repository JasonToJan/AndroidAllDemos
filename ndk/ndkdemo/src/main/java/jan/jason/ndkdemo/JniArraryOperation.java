package jan.jason.ndkdemo;

/**
 * Description: Jni数组操作
 * *
 * Creator: Wang
 * Date: 2019/9/25 22:48
 */
public class JniArraryOperation {

    static {
        System.loadLibrary("array_operation");
    }

    public native int[] getIntArray(int length);

    public native void sortIntArray(int[] arr);

    interface JniPrinterInterface{
        void printRandomArray(int[] arr);

        void printResultArray(int[] arr);
    }

    public void test(JniPrinterInterface jniPrinterInterface) {
        //获取随机的20个数
        int[] array = getIntArray(20);
        if(jniPrinterInterface!=null){
            jniPrinterInterface.printRandomArray(array);
        }

        //对数组进行排序
        sortIntArray(array);

        if(jniPrinterInterface!=null){
            jniPrinterInterface.printResultArray(array);
        }

    }

}
