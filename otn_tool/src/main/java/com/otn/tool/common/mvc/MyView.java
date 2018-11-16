package com.otn.tool.common.mvc;

import java.awt.Image;
import fi.mmm.yhteinen.swing.core.component.YPanel;

/**
 */
public abstract class MyView extends YPanel {
	
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 用于标记界面的key值.
	 * 例如多次打开同一个任务,此时只能打开一个窗口.
	 * 则同一个任务 返回的viewKey值是相同的
	 * @return
	 */
	public abstract Object getViewKey();
	
	/**
	 * view的title.这个值将会被作为JFrame的title
	 * @return
	 */
	public abstract String getViewTitle();
	
	/**
	 * 作为JFrame的icon
	 * @return
	 */
	public Image getViewIconImage(){
		return IconMgr.instance().getImage("test.png", "/conf/icons/");
		 
	 }
		

}
