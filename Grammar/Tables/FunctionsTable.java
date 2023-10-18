package Grammar.Tables;

import Lexical.LexType;

import java.util.ArrayList;
import java.util.HashMap;

public class FunctionsTable {
    private static final HashMap<String, Function> functionsTable = new HashMap<>();    //函数定义表
    private Function currentFunctionBlock = null;

    public FunctionsTable() {
    }

    public void newFunction() {
        currentFunctionBlock = new Function();
    }
    public void addFunction() {
        functionsTable.put(currentFunctionBlock.getFuncName(), currentFunctionBlock);
    }

    public Function writeFunction() {
        return currentFunctionBlock;
    }

    public static boolean funcHasDefined(String name) {
        return functionsTable.containsKey(name);
    }

    public static LexType getFuncRetType(String name) {
        if (functionsTable.containsKey(name)) {
            return functionsTable.get(name).getFuncType();
        }
        return null;
    }

    public ArrayList<Integer> getFuncFParams(String name) {
        return functionsTable.get(name).getParams();
    }

    public int getFuncFParamsSize(String name) {
        return functionsTable.get(name).getParamsSize();
    }

    public LexType getCurDefFuncRetType() {
        return currentFunctionBlock.getFuncType();
    }

    public void curIntFuncRetMatched() {
        currentFunctionBlock.retMatched();
    }

    public boolean curIntFuncNoRet() {
        return currentFunctionBlock.checkRet();
    }

    public void printFunctions() {
        for (Function f : functionsTable.values()) {
            f.printFunction();
        }
    }
}
