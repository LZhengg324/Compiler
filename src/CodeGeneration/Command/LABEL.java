package CodeGeneration.Command;

import CodeGeneration.Pcode;

public class LABEL extends Pcode {
    private String label;

    public LABEL(String label) {
        super("LABEL");
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public void printPcode() {
        System.out.println(super.getCommand() + " " + label);
    }
}
