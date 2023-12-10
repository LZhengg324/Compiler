package CodeGeneration.Command;

import CodeGeneration.Pcode;

public class RET extends Pcode {
    public RET() {
        super("RET");
    }

    @Override
    public void printPcode() {
        System.out.println(super.getCommand());
    }
}
