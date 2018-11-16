package com.otn.tool.common.task;

import com.otn.tool.common.mvc.MyView;
import fi.mmm.yhteinen.swing.core.component.menu.YMenu;
import fi.mmm.yhteinen.swing.core.component.menu.YMenuBar;
import fi.mmm.yhteinen.swing.core.component.menu.YMenuItem;

import javax.swing.*;

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
    private JToolBar toolbar;


    @Override
    public Object getViewKey() {
        return "task";
    }

    @Override
    public String getViewTitle() {
        return "OTN光功率查询工具";
    }
}
