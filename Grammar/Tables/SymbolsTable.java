package Grammar.Tables;

import ErrorHandling.Error;
import ErrorHandling.ErrorHandler;
import ErrorHandling.ErrorType;
import Lexical.LexType;
import Lexical.tokenNode;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolsTable {
    private static final ArrayList<HashMap<String, Symbol>> symbolTables = new ArrayList<>() {{
            add(new HashMap<>());
        }
    };

    public SymbolsTable() {
    }

    public void addBlock() {
        symbolTables.add(new HashMap<>());
    }

    public void quitBlock() {
        symbolTables.remove(symbolTables.size() - 1);
    }

    public static void checkVarIsReDefined(Symbol symbol, int lineNum) {
        if (symbolTables.get(symbolTables.size() - 1).containsKey(symbol.getVarName())) {
            ErrorHandler.addError(new Error(ErrorType.b, lineNum));
        } else {
            symbolTables.get(symbolTables.size() - 1).put(symbol.getVarName(), symbol);
        }
    }

    public static boolean checkVarIsUnDefined(String varName) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            for (String name : symbolTables.get(i).keySet()) {
                if (name.compareTo(varName) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int getDimension(String varName) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            for (String name : symbolTables.get(i).keySet()) {
                if (name.compareTo(varName) == 0) {
                    return symbolTables.get(i).get(name).getDimension();
                }
            }
        }
        return -1;
    }

    public boolean varIsConst(String varName) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            for (String name : symbolTables.get(i).keySet()) {
                if (name.compareTo(varName) == 0) {
                    if (symbolTables.get(i).get(varName).getVarType().compareTo(LexType.CONSTTK) == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public void printTable() {
        for (int i = 0; i < symbolTables.size(); i++) {
            System.out.println(i + " : ");
            for (Symbol symbol : symbolTables.get(i).values()) {
                System.out.println(symbol.getVarName() + " : " + symbol.getVarType() + " : " + symbol.getDimension());
            }
        }
    }
}
