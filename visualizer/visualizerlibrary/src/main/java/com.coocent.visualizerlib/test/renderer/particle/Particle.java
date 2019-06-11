package com.coocent.visualizerlib.test.renderer.particle;//声明包语句
//每个Particle对象代表一个粒子对象
public class Particle{
	int color;//粒子颜色
	float r;//粒子半径
	double vertical_v;//垂直速度
	double horizontal_v;//水平速度
	int startX;//开始x坐标
	int startY;//开始y坐标
	int x;//当前x坐标
	int y;//当前y坐标
	int alpha;
	double startTime;//起始时间
	//构造器，初始化成员变量
	public Particle(int color, float r, double vertical_v,
			double horizontal_v, int x, int y, double startTime,int alpha){
		this.color = color;	//初始化粒子颜色
		this.r = r;	//初始化粒子半径
		this.vertical_v = vertical_v;	//初始化竖直方向速度
		this.horizontal_v = horizontal_v;	//初始化水平方向上速度
		this.startX = x;		//初始化开始位置的X坐标
		this.startY = y;		//初始化开始位置的Y坐标
		this.x = x;						//初始化实时X坐标
		this.y = y;							//初始化实时Y坐标
		this.startTime = startTime;			//初始化开始运动的时间
		this.alpha = alpha;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public float getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public double getVertical_v() {
		return vertical_v;
	}

	public void setVertical_v(double vertical_v) {
		this.vertical_v = vertical_v;
	}

	public double getHorizontal_v() {
		return horizontal_v;
	}

	public void setHorizontal_v(double horizontal_v) {
		this.horizontal_v = horizontal_v;
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double getStartTime() {
		return startTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
}