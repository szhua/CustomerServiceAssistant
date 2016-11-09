package com.pcjh.assistant.entity;

import java.util.ArrayList;

/**
 * Created by szhua on 2016/11/9.
 */
public class LabelGroup {

    //single
    private Label label  ;

    private ArrayList<RConact> rConacts;


    public LabelGroup(Label label, ArrayList<RConact> rConacts) {
        this.label = label;
        this.rConacts = rConacts;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public ArrayList<RConact> getrConacts() {
        return rConacts;
    }

    public void setrConacts(ArrayList<RConact> rConacts) {
        this.rConacts = rConacts;
    }


    @Override
    public String toString() {
        return "LabelGroup{" +
                "label=" + label +
                ", rConacts=" + rConacts +
                '}';
    }
}
