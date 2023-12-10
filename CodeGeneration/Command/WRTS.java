package CodeGeneration.Command;

import CodeGeneration.Pcode;

public class WRTS extends Pcode {
    private String str;

    public WRTS(String str) {
        super("WRTS");
        this.str = str;
    }

    @Override
    public void printPcode() {
        System.out.println(super.getCommand() + " " + str);
    }

    public String getStrCon() {
        return str;
    }
}
