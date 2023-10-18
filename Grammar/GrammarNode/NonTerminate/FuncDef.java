package Grammar.GrammarNode.NonTerminate;

import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;
import Grammar.NodeType;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class FuncDef extends NonTerminalNode {
    //FuncDef -> FuncType Ident '(' [FuncFParams] ')' Block
    public FuncDef() {
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
        fileWriter.append("<FuncDef>\n");
    }
}
