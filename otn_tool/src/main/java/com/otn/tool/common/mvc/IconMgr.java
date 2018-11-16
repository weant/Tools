package com.otn.tool.common.mvc;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 *
 */
public class IconMgr {
	private static IconMgr instance;
	private static Log log = LogFactory.getLog(IconMgr.class);
	
	private IconMgr() {
	}
	
	
	public static IconMgr instance() {
		if (instance == null) {
			instance = new IconMgr();
		}
		return instance;
	}

	
	/**
	 * get Image
	 * @param iconFileName
	 * @param dir,存放图片的相对路径，如："/res/icon/"
	 * @return
	 */
	public Image getImage(String iconFileName, String dir) {
		File currentDir = new File(".");
		String iconDir = currentDir.getPath() + dir;
		String imageFileName = iconDir + iconFileName;
		Image image = Toolkit.getDefaultToolkit().getImage(imageFileName);
		return image;
	}
	
	
	/**
	 * get buffer Image
	 * @param iconFileName
	 * @param dir,存放图片的相对路径，如："/res/icon/"
	 * @return
	 */
	public BufferedImage getBufferedImage(String iconFileName, String dir) {
		File currentDir = new File(".");
		String iconDir = currentDir.getPath() + dir;
		String imageFileName = iconDir + iconFileName;
		File file = new File(imageFileName);
		BufferedImage image = null;
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
		} 
		return image;
	}
	
	
	/**
	 * get Image ICON
	 * @param iconFileName
	 * @param dir,存放图片的相对路径，如："/res/icon/"
	 * @return
	 */
	public ImageIcon getImageIcon(String iconFileName, String dir){
		String imageFileName = "";
		if(dir.trim().indexOf(".") == 0) {
			imageFileName =  dir + iconFileName;
		}else {
			File currentDir = new File(".");
			String iconDir = currentDir.getPath() + dir;
			imageFileName =  iconDir + iconFileName;
		}
		return new ImageIcon(imageFileName);
	}
	
}
