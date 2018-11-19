package com.otn.tool.task;

import com.otn.tool.common.beans.TaskGroup;
import fi.mmm.yhteinen.swing.core.YModel;

import java.util.List;

public class TaskModel extends YModel {
    private List<TaskGroup> takGroupTable;

    public List<TaskGroup> getTakGroupTable() {
        return takGroupTable;
    }

    public void setTakGroupTable(List<TaskGroup> takGroupTable) {
        this.takGroupTable = takGroupTable;
        notifyObservers("takGroupTable");
    }
}
