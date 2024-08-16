package CodeGeneration.Command;

import CodeGeneration.Pcode;

public class JPF extends Pcode {
    private String label;

    public JPF(String label) {
        super("JPF");
        this.label = label;
    }

    @Override
    public void printPcode() {
        System.out.println(super.getCommand() + " " + label);
    }

    public String getLabel() {
        return label;
    }
}
