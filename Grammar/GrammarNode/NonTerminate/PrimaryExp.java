package Grammar.GrammarNode.NonTerminate;

import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;
import Lexical.tokenNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class PrimaryExp extends NonTerminalNode {
    //PrimaryExp -> '(' Exp ')' | LVal | Number
    public PrimaryExp() {
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
        fileWriter.append("<PrimaryExp>\n");
    }

    public Integer getParamType() {
        ArrayList<ASTNode> child = getChildNodes();
        for (int i = 0; i < child.size(); i++) {
            if (child.get(i) instanceof tokenNode) {
                return ((Exp)child.get(i + 1)).getParamType();
            } else if (child.get(i) instanceof LVal) {
                return ((LVal)child.get(i)).getParamType();
            } else if (child.get(i) instanceof NumberNode) {
                return 0;
            }
        }
        return -1;
    }

    public int getConstValue() {
        int ret = 0;
        for (ASTNode node : super.getChildNodes()) {
            if (node instanceof LVal) {
                return ((LVal)node).getConstValue();
            } else if (node instanceof NumberNode) {
                return ((NumberNode)node).getConstValue();
            } else if (node instanceof Exp) {
                return ((Exp)node).getConstValue();
            }
        }
        return ret;
    }

    public void loadPCode() {
        for (ASTNode node : super.getChildNodes()) {
            if (node instanceof LVal) {
                ((LVal)node).loadPCode(false);
            } else if (node instanceof NumberNode) {
                ((NumberNode)node).loadPCode();
            } else if (node instanceof Exp) {
                ((Exp)node).loadPCode();
            }
        }
    }
}
