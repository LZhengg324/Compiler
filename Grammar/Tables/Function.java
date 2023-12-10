package Grammar.Tables;

import Lexical.LexType;

import java.util.ArrayList;

public class Function {
    private LexType funcType;   //void/int
    private String funcName;
    private boolean noReturn = false;
    private final ArrayList<Integer> parameters;

    public Function() {
        this.funcType = null;
        this.funcName = null;
        this.parameters = new ArrayList<>();
    }

    public Function(LexType funcType, String funcName) {
        this.funcType = funcType;
        this.funcName = funcName;
        this.parameters = new ArrayList<>();
    }

    public LexType getFuncType() {
        return this.funcType;
    }

    public void setFuncType(LexType funcType) {
        this.funcType = funcType;
        this.noReturn = true;
    }

    public void retMatched() {
        this.noReturn = false;
    }

    public boolean checkRet() {
        return this.noReturn;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public void insertParam(Integer dimension) {
        parameters.add(dimension);
    }

    public int getParamsSize() {
        return this.parameters.size();
    }

    public ArrayList<Integer> getParams() {
        return this.parameters;
    }

    public void printFunction() {
        System.out.print("type : " + funcType + " name : " + funcName + " params : ");
        for (Integer dimension : parameters) {
            System.out.print(dimension + " ");
        }
        System.out.println(" ");
    }
}
