package Grammar.GrammarNode.NonTerminate;

import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Cond extends NonTerminalNode {
    //Cond -> LOrExp
    public Cond() {
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
        fileWriter.append("<Cond>\n");
    }

    public void loadPCode(String trueLabel, String falseLabel) {
        ((LOrExp)super.getChildNodes().get(0)).loadPCode(trueLabel, falseLabel);
    }
}
