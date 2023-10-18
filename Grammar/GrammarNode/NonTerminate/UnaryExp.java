package Grammar.GrammarNode.NonTerminate;

import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;
import Grammar.Tables.Function;
import Grammar.Tables.FunctionsTable;
import Lexical.LexType;
import Lexical.tokenNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class UnaryExp extends NonTerminalNode {
    //UnaryExp -> PrimaryExp
    //          | Ident '(' [FuncRParams] ')'
    //          | UnaryOp UnaryExp
    public UnaryExp() {
        super();
    }

    public void addChild(ASTNode node) {
        super.addChild(node);
    }

    public int getFuncRParamsSize() {
        for (ASTNode node : getChildNodes()) {
            if (node instanceof FuncRParams) {
                return ((FuncRParams)node).getParamSize();
            }
        }
        return 0;
    }

    public ArrayList<Integer> getFuncRParamsTypes() {
        for (ASTNode node : getChildNodes()) {
            if (node instanceof FuncRParams) {
                return ((FuncRParams)node).getParamsType();
            }
        }
        return null;
    }

    public void print(FileWriter fileWriter) throws IOException {
        Iterator<ASTNode> iterator = super.getChildNodes().iterator();
        while (iterator.hasNext()) {
            iterator.next().print(fileWriter);
        }
        fileWriter.append("<UnaryExp>\n");
    }

    public Integer getParamType() {
        ArrayList<ASTNode> child = getChildNodes();
        for (int i = 0; i < child.size(); i++) {
            if (child.get(i) instanceof PrimaryExp) {
                return ((PrimaryExp)child.get(i)).getParamType();
            } else if (child.get(i) instanceof tokenNode) {
                String funcName = ((tokenNode) child.get(i)).getContent();
                if (FunctionsTable.funcHasDefined(funcName)) {
                    if (FunctionsTable.getFuncRetType(funcName).compareTo(LexType.VOIDTK) == 0) {
                        return -1;
                    } else if (FunctionsTable.getFuncRetType(funcName).compareTo(LexType.INTTK) == 0) {
                        return 0;
                    }
                }
            } else if (child.get(i) instanceof UnaryOp) {
                return ((UnaryExp)child.get(i + 1)).getParamType();
            }
        }
        return -1;
    }
}
