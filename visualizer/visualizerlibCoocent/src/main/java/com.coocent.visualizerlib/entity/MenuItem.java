package com.coocent.visualizerlib.entity;

/**
 * desc: 菜单选项
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2019/6/6 9:09
 **/
public class MenuItem {

    /**
     * 菜单编号，参见MenuData
     */
    private int menuCode;

    /**
     * 描述性语言，如横屏，竖屏
     */
    private String description;

    /**
     * 当前频谱类型，参见VisualizerManager中Type
     */
    private String currentType;


    public MenuItem() {
    }

    public MenuItem(int menuCode, String description) {
        this.menuCode = menuCode;
        this.description = description;
    }

    public MenuItem(int menuCode, String description, String currentType) {
        this.menuCode = menuCode;
        this.description = description;
        this.currentType = currentType;
    }

    public int getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(int menuCode) {
        this.menuCode = menuCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrentType() {
        return currentType;
    }

    public void setCurrentType(String currentType) {
        this.currentType = currentType;
    }
}
