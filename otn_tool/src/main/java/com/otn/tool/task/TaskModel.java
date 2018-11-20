package com.otn.tool.task;

import com.otn.tool.common.beans.TaskGroup;
import fi.mmm.yhteinen.swing.core.YModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskModel extends YModel {
    private List<TaskGroup> takGroupTable;

    public List<TaskGroup> getTakGroupTable() {
        return takGroupTable;
    }

    public void setTakGroupTable(List<TaskGroup> takGroupTable) {
        if(takGroupTable != null && !takGroupTable.isEmpty()) {
            this.takGroupTable = takGroupTable;
            sortGroups();
        } else if(takGroupTable == null) {
            this.takGroupTable = new ArrayList<>(10);
        } else
            this.takGroupTable = takGroupTable;
        notifyObservers("takGroupTable");
    }

    public void sortGroups(){
        Collections.sort(takGroupTable,(a, b)->a.getName().compareTo(b.getName()));
    }
}
