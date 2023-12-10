package CodeGeneration.Command;

import CodeGeneration.Pcode;
import Grammar.Parser;

public class JMP extends Pcode {
    private String label;

    public JMP(String label) {
        super("JMP");
        this.label = label;
    }

    @Override
    public void printPcode() {
        System.out.println(super.getCommand()+ " " + label);
    }

    public String getLabel() {
        return label;
    }
}
