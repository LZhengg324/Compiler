package CodeGeneration.Command;

import CodeGeneration.Pcode;

public class WRT extends Pcode {
    public WRT() {
        super("WRT");
    }

    @Override
    public void printPcode() {
        System.out.println(super.getCommand());
    }
}
