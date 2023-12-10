package Grammar.GrammarNode.NonTerminate;

import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;
import Lexical.LexType;
import Lexical.tokenNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class UnaryOp extends NonTerminalNode {
    //UnaryOp -> '+' | '−' | '!'
    public UnaryOp() {
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
        fileWriter.append("<UnaryOp>\n");
    }

    public LexType getOpType() {
        return ((tokenNode)super.getChildNodes().get(0)).getLexType();
    }
}
