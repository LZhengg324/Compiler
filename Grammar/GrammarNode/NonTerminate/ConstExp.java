package Grammar.GrammarNode.NonTerminate;

import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class ConstExp extends NonTerminalNode {
    //ConstExp -> AddExp
    public ConstExp() {
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
        fileWriter.append("<ConstExp>\n");
    }

    public int getConstValue() {
        AddExp node = (AddExp) super.getChildNodes().get(0);
        return node.getConstValue();
    }

    public void loadPCode() {
        ((AddExp)super.getChildNodes().get(0)).loadPCode();
    }
}