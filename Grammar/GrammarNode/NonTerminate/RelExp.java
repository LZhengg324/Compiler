package Grammar.GrammarNode.NonTerminate;

import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class RelExp extends NonTerminalNode {
    //RelExp -> AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    public RelExp() {
        super();
    }

    public void addChild(ASTNode node) {
        super.addChild(node);
    }

    public void print(FileWriter fileWriter) throws IOException {
        Iterator<ASTNode> iterator = super.getChildNodes().iterator();
        iterator.next().print(fileWriter);
        fileWriter.append("<RelExp>\n");
        while (iterator.hasNext()) {
            iterator.next().print(fileWriter);
            iterator.next().print(fileWriter);
            fileWriter.append("<RelExp>\n");
        }
    }
}