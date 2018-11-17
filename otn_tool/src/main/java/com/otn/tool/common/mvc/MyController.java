package com.otn.tool.common.mvc;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fi.mmm.yhteinen.swing.core.YController;
import fi.mmm.yhteinen.swing.core.YIComponent;
import fi.mmm.yhteinen.swing.core.component.YDialog;
import fi.mmm.yhteinen.swing.core.component.YFrame;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;


public class MyController extends YController {
	
	protected  Log log = LogFactory.getLog(this.getClass());
	protected YFrame frame = new YFrame();
	private YDialog dialog = null;
	private int x = 800;
	private int y = 600;
	
	public void showView(int operation){
		YIComponent component = this.getView();
		if(component instanceof MyView){
			MyView view = (MyView)component;
			if(frame == null){
				frame = new YFrame();
			}else{
				if(frame.getTitle().equals(view.getViewTitle())){
					frame.setVisible(true);
					return;
				}
			}
			frame.setTitle(view.getViewTitle());
			frame.setIconImage(view.getViewIconImage());
			frame.setContentPane(view);
			frame.setSize(view.getWidth(), view.getHeight());
			frame.setDefaultCloseOperation(operation);
			//frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//			frame.addWindowListener(new WindowAdapter() {
//				   public void windowClosing(WindowEvent e) {
//					   hiddenView();
//				  }});
			frame.pack();
			frame.setLocation(GuiUtils.getFrameLocationX(view.getWidth()),
					GuiUtils.getFrameLocationY(view.getHeight()));
			frame.setVisible(true);
			frame.getYProperty().put(YIComponent.MVC_NAME, "baseFrame");
			YUIToolkit.guessViewComponents(this);
			frame.addViewListener(this);
		}else{
			log.error("your view is not subclass of MyView,can't be visible");
		}
	}
	
	public void showDialogView(int operation){
		YIComponent component = this.getView();
		if(component instanceof MyView){
			MyView view = (MyView)component;
			String viewTitle = view.getViewTitle();
			dialog = new YDialog();
		    if(dialog.getTitle() != null && 
					dialog.getTitle().equals(viewTitle)){
				return;
			}
			dialog.setTitle(view.getViewTitle());
			dialog.setIconImage(view.getViewIconImage());
			dialog.setContentPane(view);
			dialog.setSize(view.getWidth(), view.getHeight());
			dialog.setDefaultCloseOperation(operation);
			//dialog.setDefaultCloseOperation(YDialog.DISPOSE_ON_CLOSE);
			dialog.addWindowListener(new WindowAdapter() {
				   public void windowClosing(WindowEvent e) {
					   hiddenDialogView();
				  }});
			dialog.pack();
			dialog.setLocation(GuiUtils.getFrameLocationX(x),
					GuiUtils.getFrameLocationY(y));
			dialog.setModal(true);
			dialog.setVisible(true);
		}else{
			log.error("your view is not subclass of MyView,can't be visible");
		}
	}
	
	public void baseFrameClosing(){
		this.hiddenView();
	}
	
	public void hiddenView(){
		this.frame.setVisible(false);
		this.frame.dispose();
		
		this.frame = null;
		
		breakRelation();
		
	}
	
	
	/**
	 * 从父级controller关系树中删除自身吗，避免内存泄漏
	 */
	public void breakRelation(){
		YController pController = this.getParent();
		if(pController != null){
			pController.removeChild(this);
		}
	}
	
	public void hiddenDialogView(){
		this.dialog.setVisible(false);
		this.dialog.dispose();
		this.dialog = null;
		breakRelation();
	}

	public YFrame getFrame() {
		return frame;
	}
	
	public YDialog getDialog(){
		return dialog;
	}
	
	public void setLocation(int x, int y){
		this.x = x;
		this.y = y;
	}	

	public void open(Object para) {
	}

}
