package Grammar.Tables;

import Lexical.LexType;

public class Symbol {
    private LexType varType;
    private String varName;
    private int dimension;

    public Symbol() {
    }

    public Symbol(LexType varType, String varName) {
        this.varType = varType;
        this.varName = varName;
        this.dimension = 0;
    }

    public void setVarType(LexType varType) {
        this.varType = varType;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public void addDimension() {
        this.dimension++;
    }

    public LexType getVarType() {
        return varType;
    }

    public int getDimension() {
        return dimension;
    }

    public String getVarName() {
        return varName;
    }
}
