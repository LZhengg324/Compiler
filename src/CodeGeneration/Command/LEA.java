package CodeGeneration.Command;

import CodeGeneration.Pcode;

public class LEA extends Pcode {
    private int level;
    private int addr;

    public LEA(int level, int addr) {
        super("LEA");
        this.level = level;
        this.addr = addr;
    }

    @Override
    public void printPcode() {
        System.out.println(super.getCommand() + " " + level + " " + addr);
    }

    public int getAddr() {
        return addr;
    }

    public int getLevel() {
        return level;
    }
}
