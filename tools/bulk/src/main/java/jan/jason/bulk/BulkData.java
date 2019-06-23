package jan.jason.bulk;

import java.util.ArrayList;

import jan.jason.bulk.helper.Item;

/**
 * desc: 批量操作数据类
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2019/6/21 17:06
 **/
public class BulkData {

    static BulkData instance=null;

    private void BulkData(){}

    public static BulkData getInstance(){
        return SingletonTool.instance;
    }

    private static class SingletonTool{
        private static final BulkData instance=new BulkData();
    }

    /**
     * 批量操作源数据
     */
    public ArrayList<Item> bulkList=new ArrayList<>();

    /**
     * 主题色
     */
    public int primaryColor;

    /**
     * 强调色
     */
    public int accentColor;

    /**
     * 底部操作面板颜色
     */
    public int operatorColor;

    /**
     * recyclerView背景颜色
     */
    public int recyclerViewBgColor;

    /**
     * 文本颜色
     */
    public int textColor;

    /**
     * 设置数据
     */
    public void setBulkList(ArrayList<Item> list){
        if(bulkList!=null){
            bulkList.clear();
        }
        if(bulkList!=null&&list!=null&&list.size()>0){
            bulkList.addAll(list);
        }
    }

    /**
     * 返回批量操作数据
     * @return
     */
    public ArrayList<Item> getBulkList(){
        return bulkList;
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public int getAccentColor() {
        return accentColor;
    }

    public void setAccentColor(int accentColor) {
        this.accentColor = accentColor;
    }

    public int getOperatorColor() {
        return operatorColor;
    }

    public void setOperatorColor(int operatorColor) {
        this.operatorColor = operatorColor;
    }

    public int getRecyclerViewBgColor() {
        return recyclerViewBgColor;
    }

    public void setRecyclerViewBgColor(int recyclerViewBgColor) {
        this.recyclerViewBgColor = recyclerViewBgColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
