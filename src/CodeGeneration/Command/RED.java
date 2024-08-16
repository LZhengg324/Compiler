package CodeGeneration.Command;

import CodeGeneration.Pcode;

public class RED extends Pcode {
    public RED() {
        super("RED");
    }

    @Override
    public void printPcode() {
        System.out.println(super.getCommand());
    }
}
