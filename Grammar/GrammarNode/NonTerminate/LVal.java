package Grammar.GrammarNode.NonTerminate;

import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;
import Grammar.NodeType;
import Grammar.Tables.SymbolsTable;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class LVal extends NonTerminalNode {
    //LVal -> Ident {'[' Exp ']'}
    public LVal() {
        super();
    }

    public void addChild(ASTNode node) {
        super.addChild(node);
    }

    public void print(FileWriter fileWriter) throws IOException {
        Iterator<ASTNode> iterator = super.getChildNodes().iterator();
        while (iterator.hasNext()) {
            iterator.next().print(fileWriter);
        }
        fileWriter.append("<LVal>\n");
    }

    public int expNodes() {
        int cnt = 0;
        for (ASTNode node : getChildNodes()) {
            if (node instanceof Exp) {
                cnt++;
            }
        }
        return cnt;
    }

    public Integer getParamType() {
        ArrayList<ASTNode> child = getChildNodes();
        String name = getIdent();
        if (child.size() == 1) {
            return SymbolsTable.getDimension(name);
        } else {
            if (SymbolsTable.getDimension(name) == 1) {
                if (expNodes() == 1) {
                    return 0;
                } else {
                    return 1;
                }
            } else if (SymbolsTable.getDimension(name) == 2) {
                if (expNodes() == 2) {
                    return 0;
                } else if (expNodes() == 1) {
                    System.out.println("hello");
                    return 1;
                } else if (expNodes() == 0) {
                    return 2;
                }
            }
        }
        return -1;
    }
}
