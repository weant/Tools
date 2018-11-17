package com.otn.tool.common.mvc;

import fi.mmm.yhteinen.swing.core.component.YButton;

import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ImageButton extends YButton {

    public ImageButton(ImageIcon normalIcon, ImageIcon rolloverIcon, ImageIcon pressedIcon, String toolTip){
        setSize(normalIcon.getImage().getWidth(null),
                normalIcon.getImage().getHeight(null));
        setIcon(normalIcon);
        setMargin(new Insets(0,0,0,0));//将边框外的上下左右空间设置为0
        setIconTextGap(0);//将标签中显示的文本和图标之间的间隔量设置为0
        setBorderPainted(false);//不打印边框
        setBorder(null);//除去边框
        setText(null);//除去按钮的默认名称
        setToolTipText(toolTip);
        setRolloverIcon(rolloverIcon);
        setPressedIcon(pressedIcon);
        // 设置文字相对于按钮图像的位置，水平居中，垂直居中
        setHorizontalTextPosition(CENTER);
        setVerticalTextPosition(CENTER);
        setFocusPainted(false);//除去焦点的框
        setContentAreaFilled(false);//除去默认的背景填充
        setFocusable(true);
    }
}  