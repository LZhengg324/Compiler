package CodeGeneration.Command;

import CodeGeneration.Pcode;

public class OPR extends Pcode {

    private OPRType type;

    public static enum OPRType {
        ADD,    //+
        SUB,    //-
        MULT,   //*
        DIV,    // /
        MOD,    //%
        CMPEQ,  //==
        CMPNEQ, //!=
        CMPGE,  //>=
        CMPLE,  //<=
        CMPGT,  //>
        CMPLT,  //<
        NOT
    }
    public OPR(OPRType type) {
        super("OPR");
        this.type = type;
    }

    public OPRType getType() {
        return type;
    }

    @Override
    public void printPcode() {
        System.out.println(super.getCommand() + " " + type);
    }
}
