package Grammar.GrammarNode.NonTerminate;

import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class Exp extends NonTerminalNode {
    //Exp -> AddExp
    public Exp() {
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
        fileWriter.append("<Exp>\n");
    }

    public Integer getParamType() {
        for (ASTNode node : getChildNodes()) {
            if (node instanceof AddExp) {
                return ((AddExp)node).getParamType();
            }
        }
        return -1;
    }

    public int getConstValue() {
        return ((AddExp)super.getChildNodes().get(0)).getConstValue();
    }

    public void loadPCode() {
        ((AddExp)super.getChildNodes().get(0)).loadPCode();
    }
}
