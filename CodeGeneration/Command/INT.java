package CodeGeneration.Command;

import CodeGeneration.Pcode;

public class INT extends Pcode {
    private int imm;

    public INT(int imm) {
        super("INT");
        this.imm = imm;
    }

    @Override
    public void printPcode() {
        System.out.println(super.getCommand() + " " + imm);
    }

    public int getImm() {
        return imm;
    }
}
