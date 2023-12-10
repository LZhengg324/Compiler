package Grammar.Tables;

import ErrorHandling.Error;
import ErrorHandling.ErrorHandler;
import ErrorHandling.ErrorType;
import Lexical.LexType;

import java.util.ArrayList;

public class SymbolsManager {
    private static SymbolsManager instance = new SymbolsManager();
    private final ArrayList<ArrayList<Symbol>> symbolTables = new ArrayList<>() {{
            add(new ArrayList<>());
        }
    };

    private SymbolsManager() {
    }

    public static SymbolsManager getInstance() {
        return instance;
    }

    public void addBlock() {
        symbolTables.add(new ArrayList<>());
    }

    public void quitBlock() {
        symbolTables.remove(symbolTables.size() - 1);
    }

    public int getCurBlockLevel() {
        return this.symbolTables.size() - 1;
    }

    public void checkVarIsReDefined(Symbol symbol, int lineNum) {
//        if (symbolTables.get(symbolTables.size() - 1).containsKey(symbol.getVarName())) {
//            ErrorHandler.addError(new Error(ErrorType.b, lineNum));
//        } else {
//            symbol.setDepth(symbolTables.size() - 1);
//            symbolTables.get(symbolTables.size() - 1).put(symbol.getVarName(), symbol);
//        }
        ArrayList<Symbol> curTable = symbolTables.get(symbolTables.size() - 1);
        for (Symbol var : curTable) {
            if (var.getVarName().compareTo(symbol.getVarName()) == 0) {
                ErrorHandler.addError(new Error(ErrorType.b, lineNum));
                return;
            }
        }

        if (curTable.isEmpty()) {
            symbol.setAddr(4);
        } else {
            Symbol prev = curTable.get(curTable.size() - 1);
            if (prev.getSize() > 0) {   //参数一维数组/二维中的第一维无实际长度，故用-1代替，getSize会出现负值，且传入的是地址
                symbol.setAddr(prev.getAddr() + prev.getSize());
            } else {
                symbol.setAddr(prev.getAddr() + 1);
            }
        }
        symbol.setDepth(symbolTables.size() - 1);
        symbolTables.get(symbolTables.size() - 1).add(symbol);
//        System.out.println("Symbol Name : " + symbol.getVarName() + " Symbol Start Addr : " + symbol.getAddr());
    }

    public boolean checkVarIsUnDefined(String varName) {
//        for (int i = symbolTables.size() - 1; i >= 0; i--) {
//            for (String name : symbolTables.get(i).keySet()) {
//                if (name.compareTo(varName) == 0) {
//                    return false;
//                }
//            }
//        }
//        return true;
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            for (Symbol symbol : symbolTables.get(i)) {
                if (symbol.getVarName().compareTo(varName) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public ArrayList<Integer> getDimension(String varName) {
//        for (int i = symbolTables.size() - 1; i >= 0; i--) {
//            for (String name : symbolTables.get(i).keySet()) {
//                if (name.compareTo(varName) == 0) {
//                    return symbolTables.get(i).get(name).getDimension();
//                }
//            }
//        }
//        return -1;
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            for (Symbol symbol : symbolTables.get(i)) {
                if (symbol.getVarName().compareTo(varName) == 0) {
                    return symbol.getDimensionLens();
                }
            }
        }
        return null;
    }

    public boolean varIsConst(String varName) {
//        for (int i = symbolTables.size() - 1; i >= 0; i--) {
//            for (String name : symbolTables.get(i).keySet()) {
//                if (name.compareTo(varName) == 0) {
//                    if (symbolTables.get(i).get(varName).getVarType().compareTo(LexType.CONSTTK) == 0) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            for (Symbol symbol : symbolTables.get(i)) {
                if (symbol.getVarName().compareTo(varName) == 0) {
                    if (symbol.getVarType().compareTo(LexType.CONSTTK) == 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public void addConstVarValue(int val) {
//        symbolTables.get(symbolTables.size() - 1).get(target).addValue(val);
        ArrayList<Symbol> curTable = symbolTables.get(symbolTables.size() - 1);
        curTable.get(curTable.size() - 1).addValue(val);
    }

    public int getConstValue(String target, int dim1, int dim2) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            ArrayList<Symbol> table = symbolTables.get(i);
            for (Symbol symbol : table) {
                if (symbol.getVarName().compareTo(target) == 0) {
                    return symbol.getConstValue(dim1, dim2);
                }
            }
        }
        return 0;
    }

    public int getVarLevel(String varName) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            ArrayList<Symbol> findTable = symbolTables.get(i);
            for (Symbol symbol : findTable) {
                if (symbol.getVarName().compareTo(varName) == 0) {
                    return symbolTables.size() - 1 - i;
                }
            }
        }
        return 0;
    }

    public int getVarAddr(String varName) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            ArrayList<Symbol> findTable = symbolTables.get(i);
            for (Symbol symbol : findTable) {
                if (symbol.getVarName().compareTo(varName) == 0) {
                    return symbol.getAddr();
                }
            }
        }
        return 0;
    }

    public void setIsFParamArrayAddr(String varName) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            ArrayList<Symbol> findTable = symbolTables.get(i);
            for (Symbol symbol : findTable) {
                if (symbol.getVarName().compareTo(varName) == 0) {
                    symbol.setIsFParamArrayAddr();
                    return;
                }
            }
        }
    }

    public boolean isFParamArrayAddr(String varName) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            ArrayList<Symbol> findTable = symbolTables.get(i);
            for (Symbol symbol : findTable) {
                if (symbol.getVarName().compareTo(varName) == 0) {
                    return symbol.isFParamArrayAddr();
                }
            }
        }
        return false;
    }
    
    public void printTable() {
        for (int i = 0; i < symbolTables.size(); i++) {
            System.out.println(i + " : ");
            for (Symbol symbol : symbolTables.get(i)) {
                System.out.println(symbol.getVarName() + " : " + symbol.getVarType() + " : " + symbol.getDimensionLens());
            }
        }
    }
}
