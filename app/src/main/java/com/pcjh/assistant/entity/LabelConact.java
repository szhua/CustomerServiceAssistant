package com.pcjh.assistant.entity;

/**
 * Created by 单志华 on 2016/11/10.
 */
public class LabelConact {

    private Label label  ;
    private RConact rconact ;


    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }


    public RConact getRconact() {
        return rconact;
    }

    public void setRconact(RConact rconact) {
        this.rconact = rconact;
    }

    @Override
    public String toString() {
        return "LabelConact{" +
                "label=" + label +
                ", rconact=" + rconact +
                '}';
    }
}
