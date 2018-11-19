package com.otn.tool.task;

import com.otn.tool.common.mvc.MyController;

import javax.swing.*;

public class TaskController extends MyController {
    private TaskModel model = new TaskModel();
    private TaskView view = new TaskView();

    public TaskController() {
        super();
        getFrame().setJMenuBar(view.getMenuBar());
        this.setUpMVC(model, view);
    }

    public static void main(String[] args) {
        TaskController controller = new TaskController();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        controller.showView(JFrame.EXIT_ON_CLOSE);
    }
}
