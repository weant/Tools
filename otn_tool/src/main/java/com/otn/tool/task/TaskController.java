package com.otn.tool.task;

import com.otn.tool.common.mvc.MyController;
import com.otn.tool.common.utils.I18N;

import javax.swing.*;

public class TaskController extends MyController {
    private TaskModel model = new TaskModel();
    private TaskView view = new TaskView();

    public TaskController() {
        super();
        getFrame().setJMenuBar(view.getMenuBar());
        this.setUpMVC(model, view);
    }


    public void exitButtonPressed(){
        int choice = JOptionPane.showConfirmDialog(getFrame(), getString("tool.exit.confirm"), getString("tool.message"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(choice == 0) {
            System.exit(0);
        }
    }

    public String getString(String string) {
        return I18N.getInstance().getString(string);
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
