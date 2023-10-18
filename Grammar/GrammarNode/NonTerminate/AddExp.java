package Grammar.GrammarNode.NonTerminate;

import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;
import Grammar.NodeType;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class AddExp extends NonTerminalNode {
    //AddExp -> MulExp | AddExp ('+' | '−') MulExp
    //AddExp -> MulExp {('+' | '−') MulExp}
    public AddExp() {
        super();
    }

    public void addChild(ASTNode node) {
        super.addChild(node);
    }

    public void print(FileWriter fileWriter) throws IOException {
        Iterator<ASTNode> iterator = super.getChildNodes().iterator();
        iterator.next().print(fileWriter);
        fileWriter.append("<AddExp>\n");
        while (iterator.hasNext()) {
            iterator.next().print(fileWriter);
            iterator.next().print(fileWriter);
            fileWriter.append("<AddExp>\n");
        }
    }

    public Integer getParamType() {
        for (ASTNode node : getChildNodes()) {
            if (node instanceof MulExp) {
                return ((MulExp)node).getParamType();
            }
        }
        return -1;
    }
}
