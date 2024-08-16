package CodeGeneration.Command;

import CodeGeneration.Pcode;

public class LOD extends Pcode {
    private int level;
    private int addr;

    public LOD(int level, int addr) {
        super("LOD");
        this.level = level;
        this.addr = addr;
    }

    @Override
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
