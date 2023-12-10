package CodeGeneration.Command;

import CodeGeneration.Pcode;

public class BLKE extends Pcode {

    public BLKE() {
        super("BLKE");
    }

    @Override
    public void printPcode() {
        System.out.println(super.getCommand());
    }
}
