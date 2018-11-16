package com.otn.tool.common.mvc;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

public class GuiUtils {

	public static int getFrameLocationY(int frameHeight) {
		//获取屏幕的高
		int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		
		return (screenHeight - frameHeight)/2;
	}
	
	public static int getFrameLocationX(int framewidth) {
		//获取屏幕的宽
		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		
		return (screenWidth - framewidth)/2;
	}
	
	/**
	 * 获取屏幕大小
	 * @return
	 */
	public static Dimension getScreenSize(){
		return Toolkit.getDefaultToolkit().getScreenSize();
	}
	
	
	/**
	 * 按照屏幕的宽度比例获取宽度
	 * @param widthScale
	 * @return
	 */
	public static int getPreferWidth(double widthScale)
			throws NumberFormatException {
		double size = Toolkit.getDefaultToolkit().getScreenSize().width * widthScale;
		return (int)size;
	}
	
	
	/**
	 * 按照屏幕的高度比例获取高度
	 * @param hightScale
	 * @return
	 */
	public static int getPreferHight(double hightScale)
			throws NumberFormatException {
		double size = Toolkit.getDefaultToolkit().getScreenSize().height* hightScale;
		return (int)size;
	}
	
	
	/**
	 * 按照组件的宽度比例获取宽度
	 * @param widthScale
	 * @return
	 */
	public static int getComponentPreferWidth(Component parent, double widthScale)
			throws NumberFormatException {
		double size =parent.getPreferredSize().getWidth() * widthScale;
		return (int)size;
	}
	
	/**
	 * 按照组件的高度比例获取高度
	 * @param hightScale
	 * @return
	 */
	public static int getComponentPreferHight(Component parent, double hightScale)
			throws NumberFormatException {
		double size = parent.getPreferredSize().getHeight() * hightScale;
		return (int)size;
	}

	/**
	 * 按照比例设置组件大小
	 * @param parent 参照组件，如果为NULL则以屏幕为参照计算大小
	 * @param comp 目标组件
	 * @param width 宽比例，值小于或等于1
	 * @param height 高比例，值小于或等于1
	 */
	public static void setSizebyScale(Component parent, Component comp, double width, double height) throws Exception {
		if(width <= 1 && height <= 1) {
			double w = 0;
			double h = 0;
			Dimension dim = new Dimension();
			if(parent == null) {
				w = getScreenSize().width * width;
				h = getScreenSize().height * height;
				
			} else {
				w = parent.getPreferredSize().getWidth() * width;
				h = parent.getPreferredSize().getHeight() * height;
				
			}
			dim.setSize(w, h);
			comp.setPreferredSize(dim);
		} else {
			throw new Exception("Wide scale or high scale must be less than or equal to 1");
		}
	}
}
