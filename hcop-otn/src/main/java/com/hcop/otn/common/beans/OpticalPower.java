package com.hcop.otn.common.beans;

public class OpticalPower {
    private InternalNe ne;
    private InternalTp tp;
    private String opr;
    private String opt;

    public InternalNe getNe() {
        return ne;
    }

    public void setNe(InternalNe ne) {
        this.ne = ne;
    }

    public InternalTp getTp() {
        return tp;
    }

    public void setTp(InternalTp tp) {
        this.tp = tp;
    }

    public String getOpr() {
        return opr;
    }

    public void setOpr(String opr) {
        this.opr = opr;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }
}
