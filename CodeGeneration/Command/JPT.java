package CodeGeneration.Command;

import CodeGeneration.Pcode;

public class JPT extends Pcode {
    private String label;

    public JPT(String label) {
        super("JPT");
        this.label = label;
    }

    public void printPcode() {
        System.out.println(super.getCommand() + " " + label);
    }

    public String getLabel() {
        return label;
    }
}
