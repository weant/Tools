package com.otn.tool.common.utils;

import com.otn.tool.common.mvc.IconMgr;
import com.otn.tool.common.properties.Conf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * We have to provide our own glass pane so that it can paint a loading dialog
 * and then the user can see the progress but can't operation the GUI, it's a
 * transparent pane so the below contents is visible.
 * 
 * Also please refer to articles by Sun - How to Use Root Panes: {@link http
 * ://java.sun.com/docs/books/tutorial/uiswing/components/rootpane.ht ml}
 * 
 * @author Jacky Liu
 * @version 1.0 2006-08
 */
public class LoadingGlassPane extends JPanel {
	static{
		System.out.println("LoadingGlassPane loaded...,"+System.currentTimeMillis());
	}
	private static final long serialVersionUID = 1L;
	/**
	 * A label displays status text and loading icon.
	 */
	private JLabel statusLabel = new JLabel("Reading data, please wait...");

	public LoadingGlassPane(String message) {
	
		
		
		try {
			statusLabel.setIcon(new ImageIcon(IconMgr.instance().getImage(
				"loading.gif", Conf.instance().getProperty("icon.path"))));
			
			if ((message != null) && (message.trim().length() > 0)) {
				statusLabel.setText(message);
			}

		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Must add a mouse listener, otherwise the event will not be
		// captured
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
			}
		});
		statusLabel.setHorizontalAlignment(JLabel.CENTER);
		this.setLayout(new BorderLayout());
		this.add(statusLabel);
		setOpaque(false);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(10, 10, 10, 50));
		g2.fillRect(0, 0, getWidth(), getHeight());
		
	}

	/**
	 * Set the text to be displayed on the glass pane.
	 * 
	 * @param text
	 */
	public void setStatusText(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				statusLabel.setText(text);
			}
		});
	}

	/**
	 * Install this to the jframe as glass pane.
	 * 
	 * @param frame
	 */
	public void installAsGlassPane(JFrame frame) {
		frame.setGlassPane(this);
	}

	/**
	 * Install this to the JDialog as glass pane.
	 * 
	 * @param
	 */
	public void installJDialogAsGlassPane(JDialog dialog) {
		dialog.setGlassPane(this);
	}
	
	/**
	 * A small demo code of how to use this glasspane.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Test GlassPane");
		final LoadingGlassPane glassPane = new LoadingGlassPane("");
		glassPane.installAsGlassPane(frame);
		JButton button = new JButton("Test Query");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Call in new thread to allow the UI to update
				Thread th = new Thread() {
					public void run() {
						glassPane.setVisible(true);
						glassPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						// TODO Long time operation here
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						glassPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						glassPane.setVisible(false);
					}
				};
				th.start();
			}
		});
		frame.getContentPane().setLayout(new FlowLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(button);
		frame.setSize(200, 200);
		frame.setVisible(true);
	}
}
