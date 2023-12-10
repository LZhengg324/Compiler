package CodeGeneration.Command;

import CodeGeneration.Pcode;

public class STO extends Pcode {
    private int level;
    private int addr;

    public STO(int level, int addr) {
        super("STO");
        this.level = level;
        this.addr = addr;
    }

    public void printPcode() {
        System.out.println(super.getCommand() + " " + level + " " + addr);
    }

    public int getLevel() {
        return level;
    }

    public int getAddr() {
        return addr;
    }
}
