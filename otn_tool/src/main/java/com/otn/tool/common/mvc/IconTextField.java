package com.otn.tool.common.mvc;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.text.Document;


public class IconTextField extends JTextField {
	  
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Icon icon = null;
	  
	  /**
	   * 
	   */
	  public IconTextField() {
	    super();  
	  }
	  /**
	   * @param columns
	   */
	  public IconTextField(int columns) {
	    super(columns);    
	  }
	  /**
	   * @param text
	   */
	  public IconTextField(String text) {
	    super(text);
	    
	  }
	  /**
	   * @param text
	   * @param columns
	   */
	  public IconTextField(String text, int columns) {
	    super(text, columns);
	    
	  }
	  /**
	   * @param doc
	   * @param text
	   * @param columns
	   */
	  public IconTextField(Document doc, String text, int columns) {
	    super(doc, text, columns);    
	  }
	  
	  /**
	   * @return Returns the current Icon.
	   */
	  public Icon getIcon() {
	    return icon;
	  }
	  
	  /**
	   * @param aIcon set the Icon to display.
	   */
	  public void setIcon(Icon aIcon) {
	    Insets i = getMargin();
	    int wplus = 0;
	    int wminus = 0;
	    
	    if (aIcon!=null){
	      wplus = aIcon.getIconWidth()+2;
	    }      
	    if (icon!=null){
	      wminus= icon.getIconWidth()+2;
	    }
	    setMargin(new Insets(i.top, i.left + wplus - wminus, i.bottom, i.right));  
	    this.icon = aIcon;
	    validate();
	    repaint();
	  }
	  
	  /**
	   * paintComponent
	   * @param g
	   * @see javax.swing.JComponent#paintComponent(Graphics)
	   */
	  protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    if(getIcon() !=null) {
	      int hei = getHeight();
	      int wid = getWidth();
	      
	      getIcon().paintIcon(this, g, 2, Math.max(0, (hei-getIcon().getIconHeight())/2));
	    }
	  }
	  /**
	   * getPreferredSize
	   * @return the preferred size + the image width
	   * @see JTextField#getPreferredSize()
	   */
	  public Dimension getPreferredSize() {
	    if (getIcon()==null) {
	      return super.getPreferredSize();
	    }
	    Dimension d = new Dimension(super.getPreferredSize().width +getIcon().getIconWidth()+4, super.getPreferredSize().height);
	    return d;    
	  }
	  
	  /**
	   * setBorder
	   * @param border
	   * @see javax.swing.JComponent#setBorder(Border)
	   */
	  public void setBorder(Border border) {
	    Border marginBorder = new BasicBorders.MarginBorder();
	    if (border ==null){
	      super.setBorder(marginBorder);
	    }
	    else if (border instanceof BasicBorders.MarginBorder 
	            || border instanceof BasicBorders.FieldBorder
	            || border.getClass().getName() .equals("com.sun.java.swing.plaf.windows.XPStyle$XPFillBorder") ){
	      super.setBorder(border);      
	    }
	    else{
	        System.err.println(border.getClass().getName());
	      super.setBorder(new CompoundBorder(border, marginBorder));
	    }
	  }
	  
	

	  public static void main(String[] args) {
	    try {
	      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch (ClassNotFoundException e) {
	    } catch (InstantiationException e) {
	    } catch (IllegalAccessException e) {
	    } catch (UnsupportedLookAndFeelException e) {
	    }
	    
	    Runnable r = new Runnable(){
	      public void run() {
	        JFrame f = new JFrame("Image test");
	        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        f.setSize(600,400);
	        JPanel p = new JPanel();
	    
	        IconTextField vo = new IconTextField("okey dokey");
	        ImageIcon icon = new ImageIcon("funnel.png");

	        vo.setIcon(icon);

	        p.add(vo);
	        f.getContentPane().add(p);
	        f.setVisible(true);
	      }
	    };
	    EventQueue.invokeLater(r);
	  }
	}

