package com.otn.tool.task;

import com.otn.tool.common.mvc.FilterHeaderTable;
import com.otn.tool.common.mvc.ImageButton;
import com.otn.tool.common.mvc.MyView;
import com.otn.tool.common.utils.I18N;
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
    private ImageButton viewButton;
    private ImageButton exitButton;
    private FilterHeaderTable takGroupTable;
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
        toolBar = new JToolBar();// 创建工具栏对象
        toolBar.setFloatable(false);// 设置为不允许拖动
        ImageIcon newIcon = new ImageIcon("./icon/new.png");
        ImageIcon removeIcon = new ImageIcon("./icon/remove.png");
        ImageIcon updateIcon = new ImageIcon("./icon/update.png");
        ImageIcon downloadIcon = new ImageIcon("./icon/view.png");
        ImageIcon exeIcon = new ImageIcon("./icon/exe.png");
        ImageIcon exitIcon = new ImageIcon("./icon/exit.png");

        newGroupButton = new ImageButton(newIcon, null, null, getString("task.group.new"));
        modifyGroupButton = new ImageButton(updateIcon, null, null, getString("task.group.alter"));
        deleteGroupButton = new ImageButton(removeIcon, null, null, getString("task.group.delete"));
        executeButton = new ImageButton(exeIcon, null, null, getString("task.group.execute"));
        viewButton = new ImageButton(downloadIcon, null, null, getString("task.group.view.result"));
        exitButton = new ImageButton(exitIcon, null, null, getString("tool.exit"));

        toolBar.add(newGroupButton);
        toolBar.addSeparator();
        toolBar.add(modifyGroupButton);
        toolBar.addSeparator();
        toolBar.add(deleteGroupButton);
        toolBar.addSeparator();
        toolBar.add(viewButton);
        toolBar.addSeparator();
        toolBar.add(executeButton);
        toolBar.addSeparator();
        toolBar.add(exitButton);

        panel.add(toolBar, BorderLayout.NORTH);

        return panel;

    }

    public Component createCenter(){
        JPanel panel = new JPanel(new BorderLayout());

        YColumn[] tpYColumn = new YColumn[] {
                new YColumn("name", getString("task.group.name"), 100, false),
                new YColumn("interval", getString("task.group.interval"), 20, false),
                new YColumn("unit", getString("task.group.interval.unit"),10, false),
                new YColumn("state", getString("task.group.state"),10, false),
                new YColumn("startTime", getString("task.group.start.time"),100, false),
                new YColumn("endTime", getString("task.group.end.time"),100, false),
                new YColumn("lastExecuteTime", getString("task.group.last.execute.time"),100, false),
                new YColumn("progress", getString("stask.group.progres"),10, false)};

        takGroupTable = new FilterHeaderTable(tpYColumn) {

            private static final long serialVersionUID = 32599979272855709L;

            public TableCellRenderer getCellRenderer(int row, int column) {

                if (column == 4 || column == 6) {
                    return new DefaultTableCellRenderer() {
                        private static final long serialVersionUID = -738787952719828357L;

                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,boolean hasFocus, int row, int column) {
                            Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                            if (row % 2 == 0) {
                                comp.setBackground(Color.white);
                            } else if (row % 2 == 1) {
                                comp.setBackground(new Color(238, 246, 255));
                            }

                            //int modelIndex = takGroupTable.convertRowIndexToModel(row);
                            this.setText((String)value);
                            comp.setFont(new Font("Dialog.plain", 0, 13));
                            return comp;
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
        fileMenu.setText(getString("tool.file"));
        toolMenu=new YMenu();
        toolMenu.setText(getString("tool.tool"));
        newGroup = new YMenuItem();
        newGroup.setText(getString("task.group.new"));
        modifyGroup = new YMenuItem();
        modifyGroup.setText(getString("task.group.alter"));

        deleteGroup = new YMenuItem();
        deleteGroup.setText(getString("task.group.delete"));
        manualExec = new YMenuItem();
        manualExec.setText(getString("task.group.execute"));
        viewResult = new YMenuItem();
        viewResult.setText(getString("task.group.view.result"));
        exit = new YMenuItem();
        exit.setText(getString("tool.exit"));
        viewTpOp = new YMenuItem();
        viewTpOp.setText(getString("tool.function.view.tp.op"));
        viewOchOp = new YMenuItem();
        viewOchOp.setText(getString("tool.function.view.och.op"));

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
        newGroupPop.setText(getString("task.group.new"));
        modifyGroupPop = new YMenuItem();
        modifyGroupPop.setText(getString("task.group.alter"));

        deleteGroupPop = new YMenuItem();
        deleteGroupPop.setText(getString("task.group.delete"));
        manualExecPop = new YMenuItem();
        manualExecPop.setText(getString("task.group.execute"));
        viewResultPop = new YMenuItem();
        viewResultPop.setText(getString("task.group.view.result"));

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
        return getString("tool.name");
    }

    public String getString(String string) {
        return I18N.getInstance().getString(string);
    }
}
