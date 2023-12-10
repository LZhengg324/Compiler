package CodeGeneration.Command;

import CodeGeneration.Pcode;

public class LIT extends Pcode {
    private int imm;

    public LIT(int imm) {
        super("LIT");
        this.imm = imm;
    }

    public void printPcode() {
        System.out.println(super.getCommand() + " " + imm);
    }

    public int getImm() {
        return imm;
    }
}
