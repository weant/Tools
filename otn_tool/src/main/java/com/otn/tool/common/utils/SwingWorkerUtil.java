package com.otn.tool.common.utils;

import java.util.concurrent.ExecutionException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * worker 线程运行长时间运行任务,并在完成后向 UI 提供更新
 *
 */
public class SwingWorkerUtil extends SwingWorker<Object, Void>{
	private static Log log = LogFactory.getLog(SwingWorkerUtil.class);
	private JFrame currentFrame;
	private JDialog currentDialog;
	private SwingWorkerHandler hander;
	
	
	public SwingWorkerUtil(JFrame currentFrame, JDialog currentDialog) {
		super();
		this.currentFrame = currentFrame;
		this.currentDialog = currentDialog;
		this.hander = new SwingWorkerHandler(){

			@Override
			public Object backgroundRunnable() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void doneRunnabel(Object values) {
				
				
			}
			
		};
	}

	/**
	 * 构造方法
	 * @param currentFrame //当前的JFrame
	 * @param currentDialog //当前的JDialog
	 * @param hander       //具体的处理类
	 */
	public SwingWorkerUtil(JFrame currentFrame, JDialog currentDialog, SwingWorkerHandler hander){
	    this.currentFrame = currentFrame;
	    this.currentDialog = currentDialog;
		this.hander = hander;
	}

	/**
	 * 后台执行耗时操作
	 */
	@Override
	protected Object doInBackground() throws Exception {
		final long swingStart = System.currentTimeMillis();
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				//显示正在处理并且锁屏
				if(currentFrame != null){
					currentFrame.getGlassPane().setVisible(true);
				} else if(currentDialog != null){
					currentDialog.getGlassPane().setVisible(true);
				}
				long start = System.currentTimeMillis();
				long curTime = start;
				long cost = curTime-swingStart;
				System.out.println("background method -->swingUtilities.run start cost "+cost);
				String message = I18N.getInstance().getString("tool.processing");
				
				 cost = System.currentTimeMillis()-curTime;
				 curTime =System.currentTimeMillis();
				System.out.println("I18N.getMessage cost "+cost);
				final LoadingGlassPane glassPane = new LoadingGlassPane(message);
				cost = System.currentTimeMillis()-curTime;
				System.out.println("new LoadingGlassPane()method cost "+cost);
				//如果子任务里后台执行时间比较短，对GlassPane进行短暂处理
				setGlassPanelOnce(glassPane);
				System.out.println("background method --> glassPane showed cost "+(System.currentTimeMillis()-start));
			}

			private void setGlassPanelOnce(final LoadingGlassPane glassPane) {
				if(currentFrame != null){
					if (currentFrame.getGlassPane() instanceof LoadingGlassPane) {
						currentFrame.getGlassPane().setVisible(false);
						JPanel jPanel = new JPanel();
						jPanel.setVisible(false);
						currentFrame.setGlassPane(jPanel);
					} else {
						setGlassPanelShowOnce(glassPane);
					}
				} else if(currentDialog != null){
					if (currentDialog.getGlassPane()  instanceof LoadingGlassPane) {
						currentDialog.getGlassPane().setVisible(false);
						JPanel jPanel = new JPanel();
						jPanel.setVisible(false);
						currentDialog.setGlassPane(jPanel);
					} else {
						setGlassPanelShowOnce(glassPane);
					}
				}
			}
		});
		
		/*SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				//显示正在处理并且锁屏
				final LoadingGlassPane glassPane = new LoadingGlassPane(I18N.instance().getString("msg_info_system_isProcessing"));
				if(currentFrame != null){
					glassPane.installAsGlassPane(currentFrame);
				}else{
					glassPane.installJDialogAsGlassPane(currentDialog);
				}
				
				glassPane.setVisible(true);
			}
			
		});*/
	
		return hander.backgroundRunnable();
	}
	private void setGlassPanelShowOnce(LoadingGlassPane glassPane) {
		long curTime = System.currentTimeMillis();
		if(currentFrame != null){
			glassPane.installAsGlassPane(currentFrame);
		} else if(currentDialog != null){
			glassPane.installJDialogAsGlassPane(currentDialog);
		}
		
		//glassPane.setVisible(true); 
		long cost = System.currentTimeMillis()-curTime;
		System.out.println("new LoadingGlassPane()method -->LoadingGlassPane().visible cost "+cost);
	}
	/**
	 * doInBackground 方法完成后，在事件指派线程 上执行此方法。
	 */
	@Override
	protected void done() {
		try {
			Object values = super.get();
			hander.doneRunnabel(values);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		} catch (ExecutionException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		} finally {
			setGlassPanelState(false);
		}
	}

	private void setGlassPanelState(boolean flag) {
		if(currentFrame != null){
			if (currentFrame.getGlassPane() instanceof LoadingGlassPane) {
				currentFrame.getGlassPane().setVisible(flag);
				JPanel jPanel = new JPanel();
				jPanel.setVisible(false);
				currentFrame.setGlassPane(jPanel);
			} else {
				//如果子任务里后台执行时间比较短，进行GlassPane短暂初始化
				setGlassPanelFirst();
			}
		}else if (currentDialog != null){
			if (currentDialog.getGlassPane()  instanceof LoadingGlassPane) {
				currentDialog.getGlassPane().setVisible(flag);
				JPanel jPanel = new JPanel();
				jPanel.setVisible(false);
				currentDialog.setGlassPane(jPanel);
			} else {
				setGlassPanelFirst();
			}
		}
	}

	private void setGlassPanelFirst() {
		final LoadingGlassPane glassPane = new LoadingGlassPane(
								I18N.getInstance().getString("tool.processing"));
		setGlassPanelShowOnce(glassPane);
	}
	
	/**
	 *   调度此 SwingWorker 以便在 worker 线程上执行。
	 */
	public void work(){
		this.execute();
	}

	public void setCurrentFrame(JFrame currentFrame) {
		this.currentFrame = currentFrame;
	}

	public void setCurrentDialog(JDialog currentDialog) {
		this.currentDialog = currentDialog;
	}

	public void setHander(SwingWorkerHandler hander) {
		this.hander = hander;
	}
	 
}
