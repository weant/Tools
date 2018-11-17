package com.otn.tool.common.task;

import com.otn.tool.common.mvc.FilterHeaderTable;
import com.otn.tool.common.mvc.ImageButton;
import com.otn.tool.common.mvc.MyView;
import fi.mmm.yhteinen.swing.core.component.YScrollPane;
import fi.mmm.yhteinen.swing.core.component.menu.YMenu;
import fi.mmm.yhteinen.swing.core.component.menu.YMenuBar;
import fi.mmm.yhteinen.swing.core.component.menu.YMenuItem;
import fi.mmm.yhteinen.swing.core.component.table.YColumn;
import fi.mmm.yhteinen.swing.core.tools.YUIToolkit;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TaskView extends MyView {
    private YMenuBar menuBar;
    private YMenu fileMenu;
    private YMenu toolMenu;
    private YMenuItem newGroup;
    private YMenuItem modifyGroup;
    private YMenuItem deleteGroup;
    private YMenuItem manualExec;
    private YMenuItem viewResult;
    private YMenuItem exit;
    private YMenuItem viewTpOp;
    private YMenuItem viewOchOp;
    private JToolBar toolBar;
    private ImageButton newGroupButton;
    private ImageButton modifyGroupButton;
    private ImageButton deleteGroupButton;
    private ImageButton executeButton;
    private ImageButton exportButton;
    private ImageButton exitButton;
    private FilterHeaderTable takGroupTable;
    private int rowSelected;
    private YMenuItem newGroupPop;
    private YMenuItem modifyGroupPop;
    private YMenuItem deleteGroupPop;
    private YMenuItem manualExecPop;
    private YMenuItem viewResultPop;


    public TaskView(){
        init();
        YUIToolkit.guessViewComponents(this);
        YUIToolkit.guessMVCNames(this, false, null);
    }

    public void init(){
        initMenuBar();
        this.setPreferredSize(new Dimension(1250, 600));
        this.setLayout(new BorderLayout());
        this.add(createTop(), BorderLayout.NORTH);
        this.add(createCenter(), BorderLayout.CENTER);
    }

    public Component createTop() {
        JPanel panel = new JPanel(new BorderLayout());
        toolBar = new JToolBar("工具栏");// 创建工具栏对象
        toolBar.setFloatable(false);// 设置为不允许拖动
        ImageIcon newIcon = new ImageIcon("F:/Workspace/Tools/conf/icon/new.png");
        ImageIcon removeIcon = new ImageIcon("F:/Workspace/Tools/conf/icon/remove.png");
        ImageIcon updateIcon = new ImageIcon("F:/Workspace/Tools/conf/icon/update.png");
        ImageIcon exeIcon = new ImageIcon("F:/Workspace/Tools/conf/icon/exe.png");
        ImageIcon downloadIcon = new ImageIcon("F:/Workspace/Tools/conf/icon/download.png");
        ImageIcon exitIcon = new ImageIcon("F:/Workspace/Tools/conf/icon/exit.png");

        newGroupButton = new ImageButton(newIcon, null, null, "创建任务组");
        modifyGroupButton = new ImageButton(updateIcon, null, null, "修改任务组");
        deleteGroupButton = new ImageButton(removeIcon, null, null, "删除任务组");
        executeButton = new ImageButton(exeIcon, null, null, "手动执行");
        exportButton = new ImageButton(downloadIcon, null, null, "导出结果");
        exitButton = new ImageButton(exitIcon, null, null, "退出");

        toolBar.add(newGroupButton);
        toolBar.addSeparator();
        toolBar.add(modifyGroupButton);
        toolBar.addSeparator();
        toolBar.add(deleteGroupButton);
        toolBar.addSeparator();
        toolBar.add(executeButton);
        toolBar.addSeparator();
        toolBar.add(exportButton);
        toolBar.addSeparator();
        toolBar.add(exitButton);

        panel.add(toolBar, BorderLayout.NORTH);

        return panel;

    }

    public Component createCenter(){
        JPanel panel = new JPanel(new BorderLayout());

        YColumn[] tpYColumn = new YColumn[] {
                new YColumn("groupName", "任务组名称", 100, false),
                new YColumn("neName", "时间间隔"),
                new YColumn("localName", "单位"),
                new YColumn("portName", "开始时间"),
                new YColumn("time","结束时间"),
                new YColumn("reciveLuminousPower", "上次执行时间"),
                new YColumn("recieveLimit", "进度")};

        takGroupTable = new FilterHeaderTable(tpYColumn) {

            private static final long serialVersionUID = 32599979272855709L;

            public TableCellRenderer getCellRenderer(int row, int column) {

                if (column == 4 || column == 6) {
                    return new DefaultTableCellRenderer() {
                        private static final long serialVersionUID = -738787952719828357L;

                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,boolean hasFocus, int row, int column) {
                            int modelIndex = takGroupTable.convertRowIndexToModel(row);
                            /*OpBean bean = (OpBean) takGroupTable.getObject(modelIndex);
                            takGroupTable.setSelectionBackground(selectionBackground);
                            if ((column == 4 && bean.isReceiveOverThreshold()) || (column == 6 && bean.isSendOverThreshold())) {
                                this.setBackground(Color.red);
                            }else if (isSelected) {
                                this.setBackground(new Color(169,209,255));
                            } else if (row % 2 == 0) {
                                this.setBackground(Color.white);
                            } else {
                                this.setBackground(new Color(238, 246, 246));
                            }*/
                            this.setText((String)value);
                            this.setFont(new Font("Dialog.plain", 0, 13));
                            return this;
                        }
                    };
                }
                return super.getCellRenderer(row, column);
            }
        };

        JPopupMenu popupMenu = createMenus();
        takGroupTable.setComponentPopupMenu(popupMenu);
        YScrollPane scrollPane = new YScrollPane(takGroupTable);
        //scrollPane.setBorder(BorderFactory.createTitledBorder("任务组列表"));

        //takGroupTable.setFilterColumnIndices(new int[]{3});
        takGroupTable.setEditable(true);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    public void initMenuBar() {
        menuBar =new YMenuBar();
        fileMenu=new YMenu();
        fileMenu.setText("文件");
        toolMenu=new YMenu();
        toolMenu.setText("工具");
        newGroup = new YMenuItem();
        newGroup.setText("新建任务组");
        modifyGroup = new YMenuItem();
        modifyGroup.setText("修改任务组");

        deleteGroup = new YMenuItem();
        deleteGroup.setText("删除任务组");
        manualExec = new YMenuItem();
        manualExec.setText("手动执行");
        viewResult = new YMenuItem();
        viewResult.setText("查看结果");
        exit = new YMenuItem();
        exit.setText("退出");
        viewTpOp = new YMenuItem();
        viewTpOp.setText("查看端口光功率");
        viewOchOp = new YMenuItem();
        viewOchOp.setText("查看OCH光功率");

        menuBar.add(fileMenu);
        menuBar.add(toolMenu);
        fileMenu.add(newGroup);
        fileMenu.add(modifyGroup);
        fileMenu.add(deleteGroup);
        fileMenu.add(manualExec);
        fileMenu.add(viewResult);
        fileMenu.add(exit);
        toolMenu.add(viewTpOp);
        toolMenu.add(viewOchOp);
    }

    private JPopupMenu createMenus() {
        JPopupMenu menu = new JPopupMenu();
        newGroupPop = new YMenuItem();
        newGroupPop.setText("新建任务组");
        modifyGroupPop = new YMenuItem();
        modifyGroupPop.setText("修改任务组");

        deleteGroupPop = new YMenuItem();
        deleteGroupPop.setText("删除任务组");
        manualExecPop = new YMenuItem();
        manualExecPop.setText("手动执行");
        viewResultPop = new YMenuItem();
        viewResultPop.setText("查看结果");
        newGroupPop.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                if(mouseEvent.getButton() == MouseEvent.BUTTON1) {
                    //Point point = tpTable.getSelectedRow();
                    rowSelected = takGroupTable.getSelectedRow();
                }
            }
        });

        modifyGroupPop.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                if(mouseEvent.getButton() == MouseEvent.BUTTON1) {
                    //Point point = tpTable.getSelectedRow();
                    rowSelected = takGroupTable.getSelectedRow();
                }
            }
        });

        deleteGroupPop.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                if(mouseEvent.getButton() == MouseEvent.BUTTON1) {
                    //Point point = tpTable.getSelectedRow();
                    rowSelected = takGroupTable.getSelectedRow();
                }
            }
        });
        manualExecPop.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                if(mouseEvent.getButton() == MouseEvent.BUTTON1) {
                    //Point point = tpTable.getSelectedRow();
                    rowSelected = takGroupTable.getSelectedRow();
                }
            }
        });

        viewResultPop.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                if(mouseEvent.getButton() == MouseEvent.BUTTON1) {
                    //Point point = tpTable.getSelectedRow();
                    rowSelected = takGroupTable.getSelectedRow();
                }
            }
        });

        menu.add(newGroupPop);
        menu.add(modifyGroupPop);
        menu.add(deleteGroupPop);
        menu.add(manualExecPop);
        menu.add(viewResultPop);
        return menu;
    }

    public YMenuBar getMenuBar() {
        return menuBar;
    }

    @Override
    public Object getViewKey() {
        return "task";
    }

    @Override
    public String getViewTitle() {
        return "OTN光功率查询工具";
    }
}
