package Grammar.Tables;

import Lexical.LexType;

import java.util.ArrayList;

public class Symbol {
    private LexType varType;
    private String varName;
    private int depth;
    private int addr;
    private int size = 1;
    private ArrayList<Integer> value;
    private ArrayList<Integer> dimensionLens;
    private boolean isFParamArrayAddr = false;

    public Symbol(LexType varType, String varName) {
        this.varType = varType;
        this.varName = varName;
        this.value = new ArrayList<>();
        this.dimensionLens = new ArrayList<>();
    }

    public void setVarType(LexType varType) {
        this.varType = varType;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public void addDimension(Integer dim) {
        this.dimensionLens.add(dim);
        this.size = size * dim;
    }

    public LexType getVarType() {
        return varType;
    }

    public ArrayList<Integer> getDimensionLens() {
        return this.dimensionLens;
    }

    public int getSize() {
        return this.size;
    }

    public void addValue(int val) {
        value.add(val);
    }

    public int getConstValue(int dim1, int dim2) {
        if (this.dimensionLens.isEmpty()) {
            return value.get(0);
        } else if (this.dimensionLens.size() == 1) {
            return value.get(dim1);
        } else {
            return value.get(dim1*dimensionLens.get(1) + dim2);
        }
    }

    public ArrayList<Integer> getConstValue() {
        return this.value;
    }

    public String getVarName() {
        return varName;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getAddr() {
        return addr;
    }

    public void setAddr(int addr) {
        this.addr = addr;
    }

    public void setIsFParamArrayAddr() {
        this.isFParamArrayAddr = true;
    }

    public boolean isFParamArrayAddr() {
        return this.isFParamArrayAddr;
    }
}
