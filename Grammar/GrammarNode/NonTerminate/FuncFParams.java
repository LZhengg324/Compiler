package Grammar.GrammarNode.NonTerminate;

import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;
import Grammar.NodeType;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class FuncFParams extends NonTerminalNode {
    //FuncFParams -> FuncFParam { ',' FuncFParam }
    public FuncFParams() {
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
        fileWriter.append("<FuncFParams>\n");
    }
}
