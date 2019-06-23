package jan.jason.bulk.helper;


public class Item {

    private String title1;
    private String title2;
    private String title3;
    private boolean isChecked;

    public Item() {
    }

    public Item(String title1, String title2, String title3) {
        this.title1 = title1;
        this.title2 = title2;
        this.title3 = title3;
    }

    public Item(String title1, String title2, String title3, boolean isChecked) {
        this.title1 = title1;
        this.title2 = title2;
        this.title3 = title3;
        this.isChecked = isChecked;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getTitle3() {
        return title3;
    }

    public void setTitle3(String title3) {
        this.title3 = title3;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}