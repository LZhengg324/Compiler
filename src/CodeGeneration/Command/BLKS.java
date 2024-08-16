package CodeGeneration.Command;

import CodeGeneration.Pcode;

public class BLKS extends Pcode {
    public BLKS() {
        super("BLKS");
    }

    @Override
    public void printPcode() {
        System.out.println(super.getCommand());
    }
}
