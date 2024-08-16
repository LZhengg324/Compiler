package Grammar.Tables;

import Lexical.LexType;

import java.util.ArrayList;
import java.util.HashMap;

public class FunctionsManager {
    private static FunctionsManager instance = new FunctionsManager();
    private final HashMap<String, Function> functionsTable = new HashMap<>();    //函数定义表
    private Function currentFunctionBlock = null;

    private FunctionsManager() {
    }

    public static FunctionsManager getInstance() {
        return instance;
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

    public boolean funcHasDefined(String name) {
        return functionsTable.containsKey(name);
    }

    public LexType getFuncRetType(String name) {
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

    public void curFuncHasReturn() {
        currentFunctionBlock.retMatched();
    }

    public boolean curFuncNoReturn() {
        return currentFunctionBlock.checkRet();
    }

    public void printFunctions() {
        for (Function f : functionsTable.values()) {
            f.printFunction();
        }
    }
}
