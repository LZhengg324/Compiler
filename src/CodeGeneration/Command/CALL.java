package CodeGeneration.Command;

import CodeGeneration.Pcode;

public class CALL extends Pcode {
    private String label;
    private int paranum;

    public CALL(String label, int paranum) {
        super("CALL");
        this.label = label;
        this.paranum = paranum;
    }

    public int getParanum() {
        return paranum;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public void printPcode() {
        System.out.println(super.getCommand() + " " + label + " " + paranum);
    }
}
